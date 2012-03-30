/*******************************************************************************
 * Copyright (c) 2008-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *    $Id$
 *******************************************************************************/
package ch.elexis.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ch.elexis.Desk;
import ch.elexis.core.data.ISticker;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;

/**
 * Eine Markierung für im Prinzip beliebige Objekte. Ein Objekt, das eine Etikette hat, kann diese
 * Etikette zur Darstellung verwenden
 * 
 * @author gerry
 * 
 */
public class Sticker extends PersistentObject implements Comparable<ISticker>, ISticker {
	public static final String IMAGE_ID = "BildID";
	public static final String BACKGROUND = "bg";
	public static final String FOREGROUND = "vg";
	public static final String NAME = "Name";
	static final String TABLENAME = "ETIKETTEN";
	static final String LINKTABLE = "ETIKETTEN_OBJECT_LINK";
	static final String CLASSLINK = "ETIKETTEN_OBJCLASS_LINK";
	static final HashMap<Class<?>, List<Sticker>> cache = new HashMap<Class<?>, List<Sticker>>();
	
	static {
		addMapping(TABLENAME, DATE_COMPOUND, "BildID=Image", "vg=foreground", "bg=background",
			NAME, "wert=importance"

		);
	}
	
	public Sticker(String name, Color fg, Color bg){
		create(null);
		if (fg == null) {
			fg = Desk.getColor(Desk.COL_BLACK);
		}
		if (bg == null) {
			bg = Desk.getColor(Desk.COL_WHITE);
		}
		set(new String[] {
			NAME, FOREGROUND, BACKGROUND
		}, new String[] {
			name, Desk.createColor(fg.getRGB()), Desk.createColor(bg.getRGB())
		});
	}
	
	public Composite createForm(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(2, false));
		Image img = getImage();
		
		GridData gd1 = null;
		GridData gd2 = null;
		;
		Composite cImg = new Composite(ret, SWT.NONE);
		if (img != null) {
			cImg.setBackgroundImage(img);
			gd1 = new GridData(img.getBounds().width, img.getBounds().height);
			gd2 = new GridData(SWT.DEFAULT, img.getBounds().height);
		} else {
			gd1 = new GridData(10, 10);
			gd2 = new GridData(SWT.DEFAULT, SWT.DEFAULT);
		}
		cImg.setLayoutData(gd1);
		Label lbl = new Label(ret, SWT.NONE);
		lbl.setLayoutData(gd2);
		lbl.setText(getLabel());
		lbl.setForeground(getForeground());
		lbl.setBackground(getBackground());
		return ret;
	}
	
	public Image getImage(){
		DBImage image = DBImage.load(get(IMAGE_ID));
		if (image != null) {
			Image ret = Desk.getImage(image.getName());
			if (ret == null) {
				ret = image.getImageScaledTo(16, 16, false);
				Desk.getImageRegistry().put(image.getName(), ret);
			}
			return ret;
		}
		return null;
	}
	
	public void setImage(DBImage image){
		set(IMAGE_ID, image.getId());
	}
	
	public void setForeground(String fg){
		set(FOREGROUND, fg);
	}
	
	public void setForeground(Color fg){
		if (fg != null) {
			set(FOREGROUND, Desk.createColor(fg.getRGB()));
		}
	}
	
	public Color getForeground(){
		String vg = get(FOREGROUND);
		return Desk.getColorFromRGB(vg);
		
	}
	
	public void setBackground(String bg){
		set(BACKGROUND, bg);
	}
	
	public void setBackground(Color bg){
		if (bg != null) {
			set(BACKGROUND, Desk.createColor(bg.getRGB()));
		}
	}
	
	public void register(){
		Desk.getImageRegistry().put(get(NAME), new DBImageDescriptor(get(NAME)));
	}
	
	public Color getBackground(){
		String bg = get(BACKGROUND);
		return Desk.getColorFromRGB(bg);
	}
	
	@Override
	public String getLabel(){
		return get(NAME);
	}
	
	public int getWert(){
		return checkZero(get("wert"));
	}
	
	public void setWert(int w){
		set("wert", Integer.toString(w));
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	@Override
	public boolean delete(){
		StringBuilder sb = new StringBuilder();
		Stm stm = getConnection().getStatement();
		
		sb.append("DELETE FROM ").append(Sticker.LINKTABLE).append(" WHERE ")
			.append("etikette = '").append(getId()).append("'");
		stm.exec(sb.toString());
		getConnection().releaseStatement(stm);
		return super.delete();
	}
	
	private static String insertStickerClassString =
		"INSERT INTO " + CLASSLINK + " (objclass,sticker) VALUES (?,?);";
	private static PreparedStatement insertStickerClass = null;
	
	public void setClassForSticker(Class clazz){
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO " + CLASSLINK + " (objclass,sticker) VALUES (").append(
			JdbcLink.wrap(clazz.getName())).append(",").append(getWrappedId()).append(");");
		getConnection().exec(sb.toString());
		
	}
	
	public void removeClassForSticker(Class<?> clazz){
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM " + CLASSLINK + " WHERE objclass=").append(
			JdbcLink.wrap(clazz.getName())).append(" AND sticker=").append(getWrappedId());
		getConnection().exec(sb.toString());
	}
	
	private static String queryClassStickerString =
		"SELECT objclass FROM " + Sticker.CLASSLINK + " WHERE sticker=?";
	private static PreparedStatement queryClasses = null;
	
	public List<String> getClassesForSticker(){
		ArrayList<String> ret = new ArrayList<String>();
		if (queryClasses == null) {
			queryClasses = getConnection().prepareStatement(queryClassStickerString);
		}
		
		try {
			queryClasses.setString(1, getId());
			ResultSet res = queryClasses.executeQuery();
			while (res != null && res.next()) {
				ret.add(res.getString(1));
			}
			res.close();
		} catch (Exception ex) {
			ExHandler.handle(ex);
			return ret;
		}
		return ret;
		
	}
	
	private static String queryStickerClassString =
		"SELECT sticker FROM " + Sticker.CLASSLINK + " WHERE objclass=?";
	private static PreparedStatement queryStickers = null;
	
	/**
	 * Find all Stickers applicable for a given class
	 * 
	 * @param clazz
	 * @return
	 */
	public static List<Sticker> getStickersForClass(Class<?> clazz){
 		List<Sticker> ret = cache.get(clazz);
		if (ret != null) {
			return ret;
		}
		HashSet<Sticker> uniqueRet = new HashSet<Sticker>();
		if (queryStickers == null) {
			queryStickers = getConnection().prepareStatement(queryStickerClassString);
		}
		
		try {
			queryStickers.setString(1, clazz.getName());
			ResultSet res = queryStickers.executeQuery();
			while (res != null && res.next()) {
				Sticker et = Sticker.load(res.getString(1));
				if (et != null && et.exists()) {
					uniqueRet.add(Sticker.load(res.getString(1)));
				}
			}
			res.close();
		} catch (Exception ex) {
			ExHandler.handle(ex);
			return ret;
		}
		cache.put(clazz, new ArrayList<Sticker>(uniqueRet));
		return new ArrayList<Sticker>(uniqueRet);
	}
	
	public static Sticker load(String id){
		Sticker ret = new Sticker(id);
		if (!ret.exists()) {
			return null;
		}
		return ret;
	}
	
	protected Sticker(String id){
		super(id);
	}
	
	protected Sticker(){}
	
	public int compareTo(ISticker o){
		if (o != null) {
			return o.getWert() - getWert();
		}
		return 1;
	}
}
