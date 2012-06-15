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

// see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class Address2 {
	private String type; // Adresstyp: at_work | at_private | at_praxis
	private String title; // Titel und/oder Anrede
	private String name; // Name wie in der Adresse gewünscht
	private String address; // Strasse/Nr. (französische Schweiz: Nr./Strasse)
	private String location; // PLZ/Ort
	private String canton; // 2-Stelliges Kantonskürzel
	private List<String> additional_lines; // / Zusätzliche Adresszeilen vor Strasse/Nr.
	private List<String> fon; // Mit dieser Adresse verbundene Telefonnummern
	private List<String> fax; // Mit dieser Adresse verbundene Faxnummern
    public static int counter;

    public Address2() {
        counter++;
    }

	public String getType(){
		return type;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	public List<String> getAdditional_lines(){
		return additional_lines;
	}
	
	public void setAdditional_lines(List<String> additional_lines){
		this.additional_lines = additional_lines;
	}
	public String getAddress(){
		return address;
	}
	
	public void setAddress(String address){
		this.address = address;
	}
	
	public String getLocation(){
		return location;
	}
	
	public void setLocation(String location){
		this.location = location;
	}
	
	public String getCanton(){
		return canton;
	}
	
	public void setCanton(String canton){
		this.canton = canton;
	}
	public List<String> getFon(){
		return fon;
	}
	
	public void setFon(List<String> fon){
		this.fon = fon;
	}
	
	public List<String> getFax(){
		return fax;
	}
	
	public void setFax(List<String> fax){
		this.fax = fax;
	}
}
