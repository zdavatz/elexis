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
package ch.ngiger.elexis.oddb_ch.yaml.test;

/* As this the first time I use SnakeYaml to parse a YAML file, this unit tests is much more
 * verbose than necessary. But it might server other newcomers as an example how to parse a YAML file.
 * First I created Java classes which just hold the date elements specified at 
 * http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt. 
 * Then I used Eclipse helper Source..Generate Getters and Setters".
 * (Always wondering why Java needs so many boilerplate code)
 */
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oddb.ch.Address2;
import org.oddb.ch.Company;
import org.oddb.ch.Import;
import org.oddb.ch.Version;
import org.oddb.ch.ImportConstructor;
import org.oddb.ch.Registration;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import ch.ngiger.elexis.oddb_ch.Car;

public class ImportFileTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception{}
	
	@Before
	public void setUp() throws Exception{}
	
	@After
	public void tearDown() throws Exception{}
	
	// @Test
	public final void testImportFile(){
		Import demoImport = new Import();
		assertNull(demoImport.importString("demo"));
	}
	
	final static private String oneCompany = "--- !oddb.org,2003/ODDB::Company\n" + "oid: 123\n"
		+ "ean13: \"7601001307001\"\n" + "name: athenstaedt AG\n" + "business_area: ba_pharma\n"
		+ "generic_type:\n" + "url:\n" + "email:\n" + "contact:\n" + "contact_email: tst@tst.org\n";
	
	final static String twoCompanies = String.format("%s\n%s", oneCompany,
		"--- !oddb.org,2003/ODDB::Company\n" + "oid: 120\n" + "ean13: \"7601001053854\"\n"
			+ "name: Veterinaria AG\n" + "business_area:\n" + "generic_type:\n" + "url:\n"
			+ "email:\n" + "contact:\n" + "contact_email:\n");
	
	@Test
	public final void testConverStringSimple(){
		assertNull(Import.convertString("demo"));
		Map<Integer, Company> res1;
		res1 = Import.convertString(oneCompany);
		assertNotNull(res1);
		assertNotNull(res1.get(123));
		assertNull(res1.get(120));
		assert (1 == res1.size());
		Map<Integer, Company> res2;
		res2 = Import.convertString(twoCompanies);
		Company corp = res2.get(123);
		System.out.println(corp);
		assert (2 == res1.size());
		assertNotNull(res2.get(123));
		assertNotNull(res2.get(120));
	}
	
	final static private String oneCompanyWithTwoAdresses = "--- !oddb.org,2003/ODDB::Company\n"
		+ "oid: 123\n" + "ean13: \"7601001307001\"\n" + "name: athenstaedt AG\n"
		+ "business_area: ba_pharma\n" + "generic_type:\n" + "url:\n" + "email:\n" + "addresses:\n"
		+ "- !oddb.org,2003/ODDB::Address2\n" + "  title:\n" + "  name:\n"
		+ "contact:\n" + "contact_email: tst@tst.org\n";
	
	final static private String oneAddress = "--- !oddb.org,2003/ODDB::Address2\n" + "  title:\n"
		+ "  name:\n"
	;
	
	@Test
	public final void testEscapeString(){
		String FoundInOddb = "Injektionsl\\xC3\\xB6sung";
		String Search = "\\xC3\\xB6";
		String soll = "Injektionslösung";
		String ist = FoundInOddb.replaceAll(Matcher.quoteReplacement(Search), "ö");
		System.out.println(ist);
		assert (ist.equals(soll));
	}
	
	@Test
	public final void testCompanyWithTwoAdresses(){
		Constructor constructor = new ImportConstructor();
		Yaml yaml = new Yaml(constructor);
		for (Object data : yaml.loadAll(oneCompanyWithTwoAdresses)) {
			Company corp = (Company) data;
			assertTrue(data.getClass() == corp.getClass());
		}
	}
	
	@Test
	public final void testAdress2(){
		String yamlContent = oneAddress;
		Constructor constructor = new ImportConstructor();
		Yaml yaml = new Yaml(constructor);
		for (Object data : yaml.loadAll(yamlContent)) {
			Address2 adr = (Address2) data;
			assertTrue(data.getClass() == adr.getClass());
		}
		
	}
	
	@Test
	public final void testCarWheel(){
		String content =
			"wheel: !!ch.ngiger.elexis.oddb_ch.Wheel {id: 2}\n" + "map: {id: 3}\n"
				+ "plate: 12-XP-F4\n";
		Yaml yaml = new Yaml();
		Object obj = yaml.load(content);
		System.out.println(obj);
		
		String content2 =
			"--- !car\n" + "plate: 12-XP-F4\n" + "wheels:\n" + "- {id: 1}\n" + "- {id: 2}\n"
				+ "- {id: 3}\n" + "- {id: 4}\n" + "- {id: 5}\n";
		Constructor constructor = new Constructor(Car.class);// Car.class is root
		TypeDescription carDescription = new TypeDescription(Car.class);
		constructor.addTypeDescription(carDescription);
		yaml = new Yaml(constructor);
		obj = yaml.load(content2);
		assertEquals(obj.getClass(), Car.class);
	}
	// @Test
	public final void testFailingNullInt(){
		Constructor constructor = new Constructor(Car.class);// Car.class is root
		TypeDescription carDescription = new TypeDescription(Car.class);
		constructor.addTypeDescription(carDescription);
		String content2 =
			"--- !car\n" + "plate: 12-XP-F4\n" + "wheels:\n" + "- {id: }\n" + "- {id: 2}\n"
				+ "- {id: 3}\n" + "- {id: 4}\n" + "- {id: 5}\n";
		Yaml yaml = new Yaml();
		yaml = new Yaml(constructor);
		Object obj = yaml.load(content2);
		System.out.println(obj);
		System.out.println(obj.getClass().toString());
		
	}
	
	void runOneOddbImport(String fileName)
	{
		Date d1 = new Date(System.currentTimeMillis());
		File file = new File(fileName);
		if (!file.exists())
		{
			System.out.println("Skip missing file: "+fileName );		
			return;
		}
		
		Company.counter = 0;
		Registration.counter = 0;
		Address2.counter = 0;
		Import oddbImport = new Import();
		boolean res = oddbImport.importFile(fileName);		
		Date d2 = new Date(System.currentTimeMillis());
		long difference = d2.getTime() - d1.getTime();
		System.out.println("runOneImportFile passed: "+fileName );		
		System.out.println(String.format("Elapsed %1$d.%2$d seconds importing %3$s", difference / 1000,
			difference % 1000, file.getAbsolutePath()));
	}
	@Test
	public void testOddbImport(){
		File file = new File(System.getProperty("user.dir"));
		try {
			System.out.println("Working Directory = " + file.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		runOneOddbImport("rsc/oddb.yaml");	
		assertEquals(3, Address2.counter);
		assertEquals(5, Registration.counter);
		assertEquals(3, Company.counter);
		runOneOddbImport("/opt/downloads/oddb.yaml");
	}
	
	public class CustomConstructor extends SafeConstructor {
		public CustomConstructor(){
			// define tags which begin with !org.oddb...
			this.yamlMultiConstructors.put(Version.ODDB_VERSION_PREFIX, new ConstructYamlMap());
		}
	}
	
	void runOneImport(String fileName, boolean useAll)
	{
		Date d1 = new Date(System.currentTimeMillis());
		File file = new File(fileName);
		if (!file.exists())
		{
			System.out.println("Skip missing file: "+fileName );		
			return;
		}
		
		System.out.println(fileName + ":  file length is "  + file.length());		
		String yamlContent = null;
		try {
			yamlContent = ch.rgw.io.FileTool.readTextFile(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CustomConstructor custom = new CustomConstructor(); 
		Yaml yaml = new Yaml(custom);
		Company.counter = 0;
		Registration.counter = 0;
		Address2.counter = 0;
		if (useAll)  {
		    int counter = 0;
		    for (Object data : yaml.loadAll(yamlContent)) {
		        // System.out.println(data);
		        counter++;
		    }
			System.out.println(fileName + ": size counter is : " + counter + " file was "  + file.length());		
			assertTrue(counter > 1);
		}
		else
		{
			Map<String, Object> object = null;
			object = (Map<String, Object>) yaml.load(yamlContent);
			System.out.println(object.toString());
			System.out.println(fileName + ": size of object is : " + object.toString().length()+ " file was "  + file.length());		
			assertTrue(object != null);
		}
		Date d2 = new Date(System.currentTimeMillis());
		long difference = d2.getTime() - d1.getTime();
		System.out.println(String.format("Elapsed %1$d.%2$d seconds", difference / 1000,
			difference % 1000));
	}
	
	@Test
	public void testImportWholeAsHash(){
		runOneImport("rsc/composition.yaml", false);
		runOneImport("rsc/oddb.yaml", true);
	}
	
}
