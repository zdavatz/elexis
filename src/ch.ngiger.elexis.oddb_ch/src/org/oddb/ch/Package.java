package org.oddb.ch;

import java.util.List;
import java.util.Map;

import org.oddb.ch.Part;
import org.oddb.ch.SlEntry;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class Package {
	String ikscd; // Packungsnummer. Ergibt zusammen mit Registration.iksnr die 8-Stellige
// Swissmedic-Nummer.
	String descr; // Zusätzlicher Beschreibungstext. Selten.
	String ikscat; // Abgabekategorie. A-E
	// TODO:
/*
	List<String> parts; // Packungsbestandteile (siehe Part)
	public List<String> getParts(){
		return parts;
	}
	public void setParts(List<String> parts){
		this.parts = parts;
	}
	*/
	/* did not work when importing via Part
	Part[] parts; // Packungsbestandteile (siehe Part)
	public Part[] getParts(){
		return parts;
	}
	public void setParts(Part[] parts){
		this.parts = parts;
	}
	 */
	List<Part> parts; // Packungsbestandteile (siehe Part)
	public List<Part> getParts(){
		return parts;
	}
	public void setParts(List<Part> parts){
		this.parts = parts;
	}

	boolean out_of_trade; // Nicht im Handel erhältlich
	float price_exfactory; // Exfactorypreis in Rappen
	float price_public; // Publikumspreis in Rappen
	SlEntry sl_entry; // Eintrag in der SL, NULL = Nein, SlEntry = Ja.
	String ean13; // Ean13 des Produkts
	String[] narcotics; // Betäubungsmitteleinträge: CAS Registry Numbers
	String pharmacode; // Pharmacode gemäss RefData
	BoolOrEmpty lppv; // Produkt ist in LPPV
	public BoolOrEmpty isLppv(){
		return lppv;
	}
	public void setLppv(BoolOrEmpty lppv){
		this.lppv = lppv;
	}
	boolean has_generic; // Es gibt Generika zu diesem Produkt
	String deductible; // Selbstbehalt in Prozent
	
	public String getIkscd(){
		return ikscd;
	}
	public void setIkscd(String ikscd){
		this.ikscd = ikscd;
	}
	public String getDescr(){
		return descr;
	}
	public void setDescr(String descr){
		this.descr = descr;
	}
	public String getIkscat(){
		return ikscat;
	}
	public void setIkscat(String ikscat){
		this.ikscat = ikscat;
	}
	public boolean isOut_of_trade(){
		return out_of_trade;
	}
	public void setOut_of_trade(boolean out_of_trade){
		this.out_of_trade = out_of_trade;
	}
	public float getPrice_exfactory(){
		return price_exfactory;
	}
	public void setPrice_exfactory(float price_exfactory){
		this.price_exfactory = price_exfactory;
	}
	public float getPrice_public(){
		return price_public;
	}
	public void setPrice_public(float price_public){
		this.price_public = price_public;
	}
	public SlEntry getSl_entry(){
		return sl_entry;
	}
	public void setSl_entry(SlEntry sl_entry){
		this.sl_entry = sl_entry;
	}
	public String getEan13(){
		return ean13;
	}
	public void setEan13(String ean13){
		this.ean13 = ean13;
	}
	public String[] getNarcotics(){
		return narcotics;
	}
	public void setNarcotics(String[] narcotics){
		this.narcotics = narcotics;
	}
	public String getPharmacode(){
		return pharmacode;
	}
	public void setPharmacode(String pharmacode){
		this.pharmacode = pharmacode;
	}
	public boolean isHas_generic(){
		return has_generic;
	}
	public void setHas_generic(boolean has_generic){
		this.has_generic = has_generic;
	}
	public String getDeductible(){
		return deductible;
	}
	public void setDeductible(String deductible){
	}
	
}
