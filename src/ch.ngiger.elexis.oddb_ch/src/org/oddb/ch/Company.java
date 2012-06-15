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

import org.oddb.ch.Address2;
import org.oddb.ch.Registration;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class Company {
	
	private int oid; // Unique Identifier
	private String ean13; // Ean13 der Firma
	private String name; // Firma
	private String business_area; // Geschäftsbereich
	private String generic_type; // siehe Glossar (GenericType)
	private Registration[] registrations; // Produkte der Firma (siehe Registration)
	private String url; // Allgemeine Internet-Adresse
	private String email; // Allgemeine Email-Adresse
	private List<Address2> addresses; // Sammlung von Adressen für diese Firma (i.A. aus der
// Swissmedic-Registrationsurkunde)

	private String contact; // Vorname und Name der Kontaktperson
	private String contact_email; // Kontakt-Email
	
	public static int counter;
	
	public Company()
	{
		super();
		counter++;
	}
	public String toString()
	{
		String msg = String.format("%1$d: %2$s ",  this.oid, this.name);
		if (this.ean13 != null)
			msg += String.format(" ean13 %s", this.ean13);
		if (this.url != null)
			msg += String.format(" url %3s", this.url);
		return msg;
	}
	public int getOid(){
		return oid;
	}
	
	public void setOid(int oid){
		this.oid = oid;
	}
	
	public String getEan13(){
		return ean13;
	}
	
	public void setEan13(String ean13){
		this.ean13 = ean13;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getBusiness_area(){
		return business_area;
	}
	
	public void setBusiness_area(String business_area){
		this.business_area = business_area;
	}
	
	public String getGeneric_type(){
		return generic_type;
	}
	
	public void setGeneric_type(String generic_type){
		this.generic_type = generic_type;
	}
	
	public Registration[] getRegistrations(){
		return registrations;
	}
	
	public void setRegistrations(Registration[] registrations){
		this.registrations = registrations;
	}
	
	public String getUrl(){
		return url;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public String getEmail(){
		return email;
	}
	
	public void setEmail(String email){
		this.email = email;
	}

	public List<Address2> getAddresses(){
		return addresses;
	}
	
	public void setAddresses(List<Address2> addresses){
		this.addresses = addresses;
	}
	
	public String getContact(){
		return contact;
	}
	
	public void setContact(String contact){
		this.contact = contact;
	}
	
	public String getContact_email(){
		return contact_email;
	}
	
	public void setContact_email(String contact_email){
		this.contact_email = contact_email;
	}
}
