/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    N. Giger - 2012 adapted to ODDB-Artikel
 *    
 *******************************************************************************/
package ch.ngiger.elexis.oddb_ch.data;

import ch.elexis.data.Artikel;

public class OddbArtikel extends Artikel {
	
	public static final String ODDB_NAME = "ODDB";
	
	/*
	 * @param text: Präparatname
	 * @param ean des Artikels
	 */
	public OddbArtikel(String text, String ean){
		String fullId = String.format("%1$s.%2$s",ODDB_NAME,ean);  //$NON-NLS-1$
		create(fullId);
		set(new String[] {	
			Artikel.FLD_NAME, Artikel.FLD_TYP, Artikel.FLD_SUB_ID, Artikel.FLD_KLASSE
		}, new String[] {
			text, ODDB_NAME, "", OddbArtikel.class.getName()
		}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		// setExt("FullText", text); //$NON-NLS-1$
	}
	
	@Override
	protected String getConstraint(){
		return "Typ='ODDB'"; //$NON-NLS-1$
	}
	
	protected void setConstraint(){
		set("Typ", ODDB_NAME); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public String getLabel(){
		return getCode() + " " + checkNull(get("Name")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public String getCode(){
		return checkNull(get("SubID")); //$NON-NLS-1$
	}
	
	@Override
	public String getCodeSystemName(){
		return ODDB_NAME; //$NON-NLS-1$
	}
	
	public static OddbArtikel load(String id){
		return new OddbArtikel(id);
	}
	
	protected OddbArtikel(String id){
		super(id);
	}
	
	protected OddbArtikel(){}
	
	@Override
	public boolean isDragOK(){
		return true;
	}
	
	public String toString(){
		StringBuffer msg = new StringBuffer(" label: " + this.getLabel());
		msg.append(" ean13: " + this.getEAN());
		msg.append(" EK: " + this.getEKPreis());
		msg.append(" VK: " + this.getVKPreis());
		msg.append(" verpackungsEinheit: " + this.getVerpackungsEinheit());
		msg.append(" abgabeEinheit: " + this.getAbgabeEinheit());
		msg.append(" Verkaufseinheit: " + this.getVerkaufseinheit());
		msg.append(" atc: " + this.getATC_code());
		msg.append(" pharmacode: " + this.getPharmaCode());
		return msg.toString();		
	}
	
}