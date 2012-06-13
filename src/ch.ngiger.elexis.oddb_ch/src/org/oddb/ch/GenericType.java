package org.oddb.ch;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public enum GenericType {
//		 Produkt-Typ. Kann in Company oder Registration gesetzt sein zu einem von 4 Werten:
		 original       , //  Originalprodukt
		 generic        , //  Generikum
		 complementary  , //  Produkt der Komplementärmedizin
		 unknown 		 //  NULL ; Unbekannt / nicht gesetzt
}
