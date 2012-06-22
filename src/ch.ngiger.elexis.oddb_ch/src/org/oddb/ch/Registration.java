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

import java.util.List;
import java.util.Map;
//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class Registration {
	String iksnr; // Registrations-Id der Swissmedic ;ehemals IKS)
	String registration_date;// Datum der ersten Zulassung in der Schweiz
	String revision_date;// Datum der letzen gültigen Registrationsurkunde
	String expiration_date; // Ende der Gültigkeit der letzen gültigen Registrationsurkunde
	String inactive_date; // Falls ein Produkt unabhängig der Swissmedic ausser Handel genommen
// wird:
// Datum der Ausserhandelnahme
	Map<String, Sequence> sequences; // Hash ;Sequence.seqnr, Sequence)) // Sequenzen der
// Registration ;siehe Sequence)
	Indication indication; // Indikationsbezeichnung aus der Registrationsurkunde
	String generic_type; // siehe Glossar ;GenericType)
	String complementary_type; // siehe Glossar ;ComplementaryType)
	boolean export_flag; // Produkt ist nur für den Export bestimmt
	int fachinfo_oid; // Verbindungsschlüssel zur Fachinformation ;in fachinfo.yaml)
	public static int counter;
	
	public String toString(){
		String name = "";
		String atc = "";
		if (this.sequences == null) 
			return "";
		if (this.sequences.values().toArray().length > 0) {
			Sequence sq = (Sequence) this.sequences.values().toArray()[0];
			name = sq.name_base;
			atc = sq.getAtc_class().code;
		}
		String msg =
			String
				.format(
					"%8$s: atc %9$s, iksnr: %1$s revision_date %2$s exp %3$s inactiv %4$s %5$s comp %6$s oid %7$s",
					this.iksnr, this.revision_date, this.expiration_date, this.inactive_date,
					this.generic_type, this.complementary_type, this.fachinfo_oid, name, atc);
		return msg;
	}
	
	public Registration(){
		counter++;
	}
	
	public String getIksnr(){
		return iksnr;
	}
	
	public void setIksnr(String iksnr){
		this.iksnr = iksnr;
	}
	
	public String getRegistration_date(){
		return registration_date;
	}
	
	public void setRegistration_date(String registration_date){
		this.registration_date = registration_date;
	}
	
	public String getRevision_date(){
		return revision_date;
	}
	
	public void setRevision_date(String revision_date){
		this.revision_date = revision_date;
	}
	
	public String getExpiration_date(){
		return expiration_date;
	}
	
	public void setExpiration_date(String expiration_date){
		this.expiration_date = expiration_date;
	}
	
	public String getInactive_date(){
		return inactive_date;
	}
	
	public void setInactive_date(String inactive_date){
		this.inactive_date = inactive_date;
	}
	
	public Map<String, Sequence> getSequences(){
		return sequences;
	}
	
	public void setSequences(Map<String, Sequence> sequences){
		this.sequences = sequences;
	}
	
	public Indication getIndication(){
		return indication;
	}
	
	public void setIndication(Indication indication){
		this.indication = indication;
	}
	
	public String getGeneric_type(){
		return generic_type;
	}
	
	public void setGeneric_type(String generic_type){
		this.generic_type = generic_type;
	}
	
	public String getComplementary_type(){
		return complementary_type;
	}
	
	public void setComplementary_type(String complementary_type){
		this.complementary_type = complementary_type;
	}
	
	public boolean isExport_flag(){
		return export_flag;
	}
	
	public void setExport_flag(boolean export_flag){
		this.export_flag = export_flag;
	}
	
	public int getFachinfo_oid(){
		return fachinfo_oid;
	}
	
	public void setFachinfo_oid(int fachinfo_oid){
		this.fachinfo_oid = fachinfo_oid;
	}
}
