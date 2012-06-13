package org.oddb.ch;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt
public enum ComplementaryType {
	// Komplementärprodukt-Typ. Kann in Company oder Registration gesetzt sein zu einem von 5
// Werten:
		complementary, // Komplementärprodukte (allgemein)
		homeopathy, // Homöopathische Produkte
		anthroposophy, // Heilmittel auf Grundlage antroposophischer Erkenntnis
		phytotherapy, // Phytotherapeutische Produkte
		unknown // NULL Unbekannt / nicht gesetzt
	
}
