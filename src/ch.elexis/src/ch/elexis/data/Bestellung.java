/*******************************************************************************
 * Copyright (c) 2006-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *    $Id: Bestellung.java 5317 2009-05-24 15:00:37Z rgw_ch $
 *******************************************************************************/

package ch.elexis.data;

import java.util.ArrayList;
import java.util.List;

import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class Bestellung extends PersistentObject {
	private static final String ITEM_FIELD = "Liste"; //$NON-NLS-1$
	private static final String TABLENAME = "HEAP2"; //$NON-NLS-1$
	private List<Item> alItems;
	
	public enum ListenTyp {
		PHARMACODE, NAME, VOLL
	};
	
	static {
		addMapping(TABLENAME, "Liste=S:C:Contents"); //$NON-NLS-1$
	}
	
	public Bestellung(String name, Anwender an){
		TimeTool t = new TimeTool();
		create(name + ":" + t.toString(TimeTool.TIMESTAMP) + ":" + an.getId()); //$NON-NLS-1$ //$NON-NLS-2$
		alItems = new ArrayList<Item>();
	}
	
	@Override
	public String getLabel(){
		String[] i = getId().split(":"); //$NON-NLS-1$
		TimeTool t = new TimeTool(i[1]);
		return i[0] + ": " + t.toString(TimeTool.FULL_GER); //$NON-NLS-1$
	}
	
	public String asString(ListenTyp type){
		StringBuilder ret = new StringBuilder();
		for (Item i : alItems) {
			switch (type) {
			case PHARMACODE:
				ret.append(i.art.getPharmaCode());
				break;
			case NAME:
				ret.append(i.art.getLabel());
				break;
			case VOLL:
				ret.append(i.art.getPharmaCode()).append(StringTool.space).append(i.art.getName());
				break;
			default:
				break;
			}
			ret.append(",").append(i.num).append(StringTool.lf); //$NON-NLS-1$
		}
		return ret.toString();
	}
	
	public List<Item> asList(){
		return alItems;
	}
	
	public void addItem(Artikel art, int num){
		Item i = findItem(art);
		if (i != null) {
			i.num += num;
		} else {
			alItems.add(new Item(art, num));
		}
	}
	
	public Item findItem(Artikel art){
		for (Item i : alItems) {
			if (i.art.getId().equals(art.getId())) {
				return i;
			}
		}
		return null;
	}
	
	public void removeItem(Item art){
		alItems.remove(art);
		
	}
	
	public void save(){
		StringBuilder sb = new StringBuilder();
		for (Item i : alItems) {
			sb.append(i.art.getId()).append(",").append(i.num).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		set(ITEM_FIELD, sb.toString());
	}
	
	public void load(){
		String[] it = checkNull(get(ITEM_FIELD)).split(";"); //$NON-NLS-1$
		if (alItems == null) {
			alItems = new ArrayList<Item>();
		} else {
			alItems.clear();
		}
		for (String i : it) {
			String[] fld = i.split(","); //$NON-NLS-1$
			if (fld.length == 2) {
				Artikel art = Artikel.load(fld[0]);
				if (art.exists()) {
					alItems.add(new Item(art, Integer.parseInt(fld[1])));
				}
			}
		}
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public static Bestellung load(String id){
		Bestellung ret = new Bestellung(id);
		if (ret != null) {
			ret.load();
		}
		return ret;
	}
	
	protected Bestellung(){}
	
	protected Bestellung(String id){
		super(id);
	}
	
	public static class Item {
		public Item(Artikel a, int n){
			art = a;
			num = n;
		}
		
		public Artikel art;
		public int num;
	}
}
