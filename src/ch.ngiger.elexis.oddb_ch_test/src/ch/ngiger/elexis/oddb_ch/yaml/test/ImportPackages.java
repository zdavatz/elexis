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

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.oddb.ch.Address2;
import org.oddb.ch.Company;
import org.oddb.ch.Import;
import org.oddb.ch.Sequence;
import org.oddb.ch.Version;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

public class ImportPackages {
	
	@Test
	public final void testPackagesMulti(){
		boolean res = false;
		Import oddbImport = new Import();
		File file = new File("rsc/packages.yaml");

		String yamlContent = null;
		try {
			yamlContent = ch.rgw.io.FileTool.readTextFile(file, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Constructor c = new org.oddb.ch.ImportConstructor();
		TypeDescription descr =
			new TypeDescription(Address2.class, new Tag(Version.ODDB_VERSION_PREFIX + "::Adress2"));
		c.addTypeDescription(descr);
		Yaml yaml = new Yaml(c);
		for (Object obj : yaml.loadAll(yamlContent)) {
		}
		assert (res);
	}
	
	@Test
	public final void testPackagesMultiInt(){
		boolean res = false;
		Import oddbImport = new Import();
		File file = new File("rsc/packages_multi_int.yaml");

		String yamlContent = null;
		try {
			yamlContent = ch.rgw.io.FileTool.readTextFile(file, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Constructor c = new org.oddb.ch.ImportConstructor();
		TypeDescription descr =
			new TypeDescription(Address2.class, new Tag(Version.ODDB_VERSION_PREFIX + "::Adress2"));
		c.addTypeDescription(descr);
		Yaml yaml = new Yaml(c);
		for (Object obj : yaml.loadAll(yamlContent)) {
			
		}
		assert (res);
	}
	@Test
	public final void testPackagesMultiClass(){
		boolean res = false;
		Import oddbImport = new Import();
		File file = new File("rsc/packages_multi_class.yaml");

		String yamlContent = null;
		try {
			yamlContent = ch.rgw.io.FileTool.readTextFile(file, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Constructor c = new org.oddb.ch.ImportConstructor();
		TypeDescription descr =
			new TypeDescription(Address2.class, new Tag(Version.ODDB_VERSION_PREFIX + "::Adress2"));
		c.addTypeDescription(descr);
		Yaml yaml = new Yaml(c);
		for (Object obj : yaml.loadAll(yamlContent)) {
			
		}
		assert (res);
	}
	@Test
	public final void testPackagesMultiBoth(){
		boolean res = false;
		Import oddbImport = new Import();
		File file = new File("rsc/packages_multi_both.yaml");

		String yamlContent = null;
		try {
			yamlContent = ch.rgw.io.FileTool.readTextFile(file, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Constructor c = new org.oddb.ch.ImportConstructor();
		TypeDescription descr =
			new TypeDescription(Address2.class, new Tag(Version.ODDB_VERSION_PREFIX + "::Adress2"));
		c.addTypeDescription(descr);
		Yaml yaml = new Yaml(c);
		for (Object obj : yaml.loadAll(yamlContent)) {
		}
		assert (res);
	}
	@Test
	public final void testOrion(){
		boolean res = false;
		Import oddbImport = new Import();
		File file = new File("rsc/orion.yaml");

		String yamlContent = null;
		try {
			yamlContent = ch.rgw.io.FileTool.readTextFile(file, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Constructor c = new org.oddb.ch.ImportConstructor();
		TypeDescription descr =
			new TypeDescription(Address2.class, new Tag(Version.ODDB_VERSION_PREFIX + "::Adress2"));
		c.addTypeDescription(descr);
		Yaml yaml = new Yaml(c);
		int j = 0;
		for (Object obj : yaml.loadAll(yamlContent)) {
			j++;
			System.out.println(String.format("at elem %1$d value %2$s", j, obj.getClass().toString()));
			Company seq = (Company) obj;
			// System.out.println(seq.getAtc_class());
		}
		assert (res);
	}	
}
