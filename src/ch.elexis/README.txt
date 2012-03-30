$Id$
------------------------------------------------------------------------

Versionen
=========

TODO: Datum angeben, wann welches Release veröffentlicht wurde. Evtl. mit SVN-Tag.

DATUM 1.0
DATUM 1.1
DATUM 1.1.1
DATUM 1.1.2
1.8.2008 1.3.3
8.8.2008: Letzte revision 1.3.3, SVN rev. 4245
1.9.2008: Letzte revision 1.3.4, SVN rev. 4329. 
          Release 1.3.4 auf Server, Trunk auf 1.4.0
1.1.2009: Release 1.4.0 auf Server, Trunk auf 2.0.0
12.9.2010: Release 2.1.1 branch

Änderungen
==========

TODO: Alle relevanten Änderungen angeben, insbesondere Verhaltensänderungen.

07.11.2007, danlutz
 * Ausdruck TarmedRechnung:
   Wenn der Druckerschacht in den Druckereinstellungen konfiguriert ist, wird
   dieser verwendet. Sonst wird die Standard-Einstellung bzw. der in der
   Vorlage eingestellte Druckerschacht verwendet.
   Bisher wurde der in den Druckereinstellungen konfigurierte Schacht gar nicht
   berücksichtigt. Dies führte bei bestimmten Druckermodellen dazu, dass der
   Standard-Schacht, nicht aber der in der Vorlage eingestellte Schacht
   verwendet wurde.
   Betroffene Drucker: "Drucker mit A4-Papier mit ESR", "Drucker mit weissem A4-Papier"

29.3.2008, gweirich
 *  Tarmed-Rechnung: Positionen werden sortiert, Hierarchie:
     1 - Datum
     2 - Tarmedlkeistungen nach Ziffer
     3 - andere Leistungen alphabetisch 
     
 * Omnivore: Spalten können sortiert werden durch Klick auf Spaltenkopf
 
 26.5.08 gweirich
 * Standardschriftarten für alle Views können eungestellt werden, als User-Settimg.
 Eine View, die die vom Anwender gewünschte Standardschriftart einstellen will, kann diese 
 Schriftart mit Desk.getFont(PreferenceConstants.USR_DEFAULTFONT) einlesen. Ausserdem sollte
 eine solche View einen UserChangeListener bei GlobalEvents einhängen, um bei einem Userwechsel jeweils 
 wieder die richtige Schriftart einzustellen.
  
 * Agenda merkt sich den zuletzt eingestellten Bereich
 
 * KonsDetailView und CodeSelektor merken sich die zuletzt eingestellten Grössenverhältnisse der einzelnen 
 Teilfenster.
  
 * Anwendereinstellungen zeigen die vorhandenen EInstellungsseiten an
 
 --------------------------
 1.8.08 gweirich, 1.3.3
 Folgende Plugins sind obsolet und sollten aus dem workspace entfernt werden:
 - jdomwrapper (jdom.jar befindet sich jetzt im Zentralplugin und wird von diesem exportiert)
 - Sgam-Exchange (Compiliert nicht mehr mit xChange 1.0.1)
 - Elexis-EMR-Printer (Compiliert nicht mehr mit xChange 1.0.1)
 
Diese Plugins werden demnächst aus dem aktiven Zweig des Repository gelöscht.

---------------------------------------
6.9.2008 gweirich 1.4.0
Straffung des Codes. ch.elexis.Result wurde deprecated und gegen ch.elexis.tools.Result
ersetzt. Dieses hat keine SWT- und JFace- Abhängigkeiten und kann darum auch für
Interaktionszwecke verwendet werden. Alle RnOutputter müssen angepasst werden.
(Im Allgemeinen reicht es, auf ch.rgw.tools.Result zu verweisen und bei Fehlern statt Log.xxx
die entsprechenden Result.SEVERITY.xxx werte zu verwenden)
Eclipse-Spezifische Anpassungen werden im ch.elexis.uil.ReusltAdapter gemacht.


Abrechnung: Konzept mit %-Zuschlägen udn Abzügen geändert. Diese werden wie gewöhnliche Codes
behandelt und erhalten auch immer einen positiven Taxpunktwert, aber erhalten
einen internen Skalierungsfaktor in Höhe ihres Prozentsatzes. Dieser Skalierungsfaktor kann auch negativ sein.

-------------------------------------------------------
30.9.2008 gweirich 1.4.0
a) Weitere Straffung: Das Package ch.rgw.tools, welches weder SWT- noch Swing- Abhängigkeiten enthält,
wurde in ein eigenes Plugin elexis-utilities ausgelagert. Dies vereinfacht die Verwendung desselben Codes auf verschiedenen 
Plattformen (z.B. die geplanten Elexis- Webservices werden die Klassen aus ch.rgw.crypt zur Kommunikation
nutzen).

Folge: Viele Plugins werden zunächst nicht mehr compilieren. Dort bitte eine Abhängigkeit auf ch.rgw.tools
eintragen, dann sollte es wieder gehen (Natürlich erst, nachdem das Plugin elexis-utilities ausgecheckt wurde).
Wenn compile-errors auftreten hilft meist ein "clean".

b) Der MySQL Connector wurde aus dem Kernplugin entfernt und in ein eigenes Plugin "mysql-adapter" ausgelagert.
Dies um Lizenzproblemen zuvorzukommen. Mysql-connector steht unter der GPL, die nicht in allen Punkten
zur EPL von Elexis kompatibel ist. Jetzt sollte es sauber sein. Kurzum: Man muss auch das Plugin 
mysql-adapter auschecken. Der Elexis-Kern und elexis-utilities haben Abhängigkeiten zu diesem Plugin.
Es ist geplant, auch die anderen Datenbankverbindungen aus dem Kern herauszunehmen.

In der Run-Configuration müssen die entsprechenden Plugins ebenfalls eingetragen werden
(z.B. mit Klick auf "Add required plugins", damit sie beim Start eingebunden werden.

-------------------------------------------------------------------------

20.1.2009 gweirich 2.0.0
Umbau des Rechtesystems. Statt einfacher Strings definieren jetzt ACE's (Access Control Elements)
einzelne Zugriffsrechte. Diese haben den Vorteil, lokalisiert werden zu können (Der im Gruppen-und-Rechte-
Dialog angezeigte Text muss nicht der Name des Rechts sein).
