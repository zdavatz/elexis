package org.oddb.ch;

import java.util.Map;

import org.oddb.ch.ActiveAgent;
import org.oddb.ch.GalenicForm;

//see http://ch.oddb.org/resources/downloads/datadesc/oddb.yaml.txt

public class Composition {
	//	Composition:
	//		- galenic_form      (GalenicForm)          -> Galenische Form
	//		- active_agents     (Array (ActiveAgent)   -> Wirkstoff/Dosis

	public String toString() {
		String name = null;
		if (active_agents == null || active_agents.length == 0)
			name = String.format("gf: %1$d", counter);
		else
		{
			if (active_agents[0].substance.descriptions.containsKey("de"))
				name  = active_agents[0].substance.descriptions.get("de");
			else 
				name = "Unbekannt";
		}
		String gf = "gf ???";
		if (galenic_form != null)
			gf = galenic_form.toString();
		return String.format("composition: %1$d %2$s %3$s", counter, name, gf);
	}
	ActiveAgent[] active_agents; // Wirkstoff/Dosis
	GalenicForm galenic_form; // Galenische Form
	
	static int counter ;
	public Composition()
	{
		super();
		counter ++;
	}
	public GalenicForm getGalenic_form(){
		return galenic_form;
	}
	public void setGalenic_form(GalenicForm galenic_form){
		this.galenic_form = galenic_form;
	}
	public ActiveAgent[] getActive_agents(){
		return active_agents;
	}
	public void setActive_agents(ActiveAgent[] active_agents){
		this.active_agents = active_agents;
	}
	
}
