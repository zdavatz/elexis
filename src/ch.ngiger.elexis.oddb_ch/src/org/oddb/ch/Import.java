/*******************************************************************************
 * Copyright (c) 2012 Niklaus Giger <niklaus.giger@member.fsf.org>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Niklaus Giger <niklaus.giger@member.fsf.org> - initial API and implementation
 ******************************************************************************/
package org.oddb.ch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Tag;

import org.oddb.ch.Address2;
import org.oddb.ch.Company;

import ch.rgw.tools.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Import {
	public static final Logger logger = LoggerFactory.getLogger(Import.class);
	
	public class ElexisArtikel {
		public String ean13;
		public String name;
		public Money EKPreis;
		public Money VKPreis;
		public String pharmacode;
		public String atc_code;
		public String verpackungsEinheit;
		public String abgabeEinheit;
		public String partString;
		
		public String toString(){
			StringBuffer msg = new StringBuffer(" name: " + name);
			if (partString != null)
				msg.append(" partString: " + partString);
			if (ean13 != null)
				msg.append(" ean13: " + ean13);
			if (EKPreis != null)
				msg.append(String.format(" EK: %1$s %2$s", EKPreis.toString(),
					EKPreis.getCentsAsString()));
			if (VKPreis != null)
				msg.append(String.format(" VK: %1$s %2$s", VKPreis.toString(),
					VKPreis.getCentsAsString()));
			if (verpackungsEinheit != null)
				msg.append(" verpackungsEinheit: " + verpackungsEinheit);
			if (abgabeEinheit != null)
				msg.append(" abgabeEinheit: " + abgabeEinheit);
			if (atc_code != null)
				msg.append(" atc: " + atc_code);
			if (pharmacode != null)
				msg.append(" pharmacode: " + pharmacode);
			return msg.toString();
			
		}
	}
	
	/*
	 * Access Registration via iksnr
	 */
	public HashMap<String, Registration> registrations = new HashMap<String, Registration>();
	/*
	 * Array of all companies
	 */
	public HashMap<String, Company> companies;
	/*
	 * Access Sequence via Sequenznumerierung aus der Registrationsurkunde
	 */
	public HashMap<String, Sequence> sequences;
	/*
	 * Access Package via ean13
	 */
	public HashMap<String, Package> packages;
	public ArrayList<ElexisArtikel> articles;
	
	public Import(){
		super();
		registrations = new HashMap<String, Registration>();
		companies = new HashMap<String, Company>();
		sequences = new HashMap<String, Sequence>();
		packages = new HashMap<String, Package>();
		articles = new ArrayList<ElexisArtikel>();
		Registration.counter = 0;
		Address2.counter = 0;
	}
	
	public class CustomConstructor extends SafeConstructor {
		public CustomConstructor(){
			// define tags which begin with !org.oddb...
			this.yamlMultiConstructors.put(Version.ODDB_VERSION_PREFIX, new ConstructYamlMap());
		}
	}
	
	public boolean importFile(String filename){
		try {
			File file = new File(filename);
			System.out.println(String.format("importFile: %1$s %2$d", file.getAbsolutePath(),
				file.length()));
			return importString(ch.rgw.io.FileTool.readTextFile(file, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean importString(String yamlContent){
		Constructor c = new ImportConstructor();
		TypeDescription descr =
			new TypeDescription(Address2.class, new Tag(Version.ODDB_VERSION_PREFIX + "::Adress2"));
		c.addTypeDescription(descr);
		Yaml yaml = new Yaml(c);
		int counter = 0;
		int nrExceptions = 0;
		Date d1 = new Date(System.currentTimeMillis());
		for (Object obj : yaml.loadAll(yamlContent)) {
			Company corp = (Company) obj;
			companies.put(corp.getEan13(), corp);
			for (int j = 0; j < corp.getRegistrations().length; j++) {
				Registration registration = corp.getRegistrations()[j];
				if (j < 3) {
					logger.debug(registration.toString());
				}
				
				registrations.put(registration.getIksnr(), registration);
				Map<String, Sequence> seqs = registration.getSequences();
				Iterator<Entry<String, Sequence>> it = seqs.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, Sequence> seqIter = it.next();
					Sequence seq = seqIter.getValue();
					Iterator<Entry<String, Package>> itP = seq.packages.entrySet().iterator();
					while (itP.hasNext()) {
						Entry<String, Package> p = itP.next();
						Package pack;
						try {
							pack = p.getValue();
							ElexisArtikel a = new ElexisArtikel();
							if (seq.name_descr != null && seq.name_descr.length() > 0)
								a.name = seq.name_base + " " + seq.name_descr;
							else
								a.name = seq.name_base;
							a.atc_code = seq.atc_class.code;
							a.ean13 = pack.ean13;
							a.pharmacode = pack.pharmacode;
							a.VKPreis = new Money(pack.price_public);
							a.EKPreis = new Money(pack.price_exfactory);
							List<Part> parts = pack.parts;
							StringBuffer partS = new StringBuffer("");
							for (int k = 0; k < parts.size(); k++) {
								Part myPart = parts.get(k);
								partS.append(myPart.toString());
							}
							a.partString = parts.toString();
							articles.add(a);
							counter++;
							// logger.debug(a.toString());
						} catch (Exception e) {
						nrExceptions++;
							System.out.println(String.format("\n\n!!!	%1$d: Unexpected class %2$s \n%3$s %4$s\n%5$s\n\n", counter, p.getClass(),
								obj.toString(), p.toString(), seq.toString()));
						}
					}
				}
			}
			logger.info(corp.toString());
		}
		Date d2 = new Date(System.currentTimeMillis());
		long difference = d2.getTime() - d1.getTime();
		String msg = String
				.format("Elapsed %1$d.%2$d seconds. Ignoring %3$d exception(s)", difference / 1000, difference % 1000, nrExceptions); 
		System.out.println(msg);
		logger.info(msg);
		logger
			.info(String
				.format(
					"loaded %1$d companies, %2$d adress2 entities, %3$d sequences, %4$d packages and %5$d registrations",
					companies.size(), Address2.counter, sequences.size(), packages.size(),
					registrations.size()));
		logger.info(String.format("loaded %1$d Registration entities", Registration.counter));
		return true;
	}
	
	public static Map<Integer, Company> convertString(String yamlContent){
		Map<Integer, Company> result = new HashMap<Integer, Company>();
		try {
			Constructor constructor = new Constructor(Company.class);// Car.class is root
			TypeDescription companyDescription = new TypeDescription(Company.class);
			companyDescription.putListPropertyType("adresses", Address2.class);
			constructor.addTypeDescription(companyDescription);
			Yaml yaml = new Yaml(constructor);
			for (Object data : yaml.loadAll(yamlContent)) {
				Company corp = (Company) data;
				result.put(corp.getOid(), corp);
			}
		} catch (Exception e) {
			logger.error("GotException converting String " + e.getMessage());
			return null;
		}
		return result;
	}
}
