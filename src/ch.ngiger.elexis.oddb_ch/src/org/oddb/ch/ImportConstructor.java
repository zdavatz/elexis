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

import org.oddb.ch.Import.CustomConstructor;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.constructor.SafeConstructor.ConstructYamlMap;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

// Here we establish the mapping between the yaml entities
// and the Java classes we want to import
// This is sometimes a little be tricky!!

public class ImportConstructor extends Constructor {
	
	public ImportConstructor(){
		// define tags which begin with !org.yaml.
		String prefix = Version.ODDB_VERSION_PREFIX;
		this.yamlMultiConstructors.put(prefix, new ConstructYamlMap());
	}
	
	protected Construct getConstructor(Node node){
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Company"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Company.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::ActiveAgent"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.ActiveAgent.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Address2"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Address2.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::AtcClass"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.AtcClass.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Chapter"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Chapter.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::CommercialForm"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.CommercialForm.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::ComplementaryType"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.ComplementaryType.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Composition"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Composition.class);
		}
		if (node.getTag().equals(
			// Here we use a simple Map (but with a constructor which know about the
			// oddb tag!. Using a class did not work
			new Tag(Version.ODDB_VERSION_PREFIX + "::SimpleLanguage::Descriptions"))) {
			node.setUseClassConstructor(false);
			node.setType(CustomConstructor.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::LimitationText"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Descriptions.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Dose"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Dose.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Text::Document"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Document.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::DDD"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.DDD.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Text::Format"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Format.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::GalenicForm"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.GalenicForm.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::GalenicGroup"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.GalenicGroup.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::GenericTypexx"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.GenericType.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Indication"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Indication.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::LimitationText"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.LimitationText.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Package"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Package.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Text::Paragraph"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Paragraph.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Part"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Part.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Registration"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Registration.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Text::Section"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Section.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Sequence"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Sequence.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::SlEntry"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.SlEntry.class);
		}
		if (node.getTag().equals(new Tag(Version.ODDB_VERSION_PREFIX + "::Substance"))) {
			node.setUseClassConstructor(true);
			node.setType(org.oddb.ch.Substance.class);
		}
		return super.getConstructor(node);
	}
}
