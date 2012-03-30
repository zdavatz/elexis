/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
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

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IAction;

import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.VersionInfo;

public class ICD10 extends PersistentObject implements IDiagnose {
	public static final String VERSION = "1.0.2";
	public static final String TABLENAME = "ICD10";
	
	static final String create = "DROP INDEX icd1;" + //$NON-NLS-1$
		"DROP INDEX icd2;" + //$NON-NLS-1$
		"DROP TABLE ICD10;" + //$NON-NLS-1$
		"CREATE TABLE ICD10 (" + //$NON-NLS-1$
		"ID       VARCHAR(25) primary key, " + //$NON-NLS-1$
		"lastupdate BIGINT," + "deleted  CHAR(1) default '0'," + //$NON-NLS-1$
		"parent   VARCHAR(25)," + //$NON-NLS-1$
		"ICDCode  VARCHAR(10)," + //$NON-NLS-1$
		"encoded  TEXT," + //$NON-NLS-1$
		"ICDTxt   TEXT," + //$NON-NLS-1$
		"ExtInfo  BLOB);" + //$NON-NLS-1$
		"CREATE INDEX icd1 ON ICD10 (parent);" + //$NON-NLS-1$
		"CREATE INDEX icd2 ON ICD10 (ICDCode);" + //$NON-NLS-1$
		"INSERT INTO " + TABLENAME + " (ID,ICDTxt) VALUES ('1'," + JdbcLink.wrap(VERSION) + ");";
	
	public static void initialize(){
		createOrModifyTable(create);
	}
	
	static {
		addMapping("ICD10", "parent", "Code=ICDCode", "Text=ICDTxt", "encoded", "ExtInfo"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

		ICD10 check = load("1");
		if (check.state() < PersistentObject.DELETED) {
			initialize();
		} else {
			VersionInfo vi = new VersionInfo(check.get("Text"));
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder("1.0.1")) {
					getConnection().exec(
						"ALTER TABLE " + TABLENAME + " ADD deleted CHAR(1) default '0';");
					check.set("Text", VERSION);
				}
				if (vi.isOlder("1.0.2")) {
					getConnection().exec("ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT;");
					check.set("Text", VERSION);
				}
			}
		}
	}
	static final int LEVEL = 0;
	static final int TERMINAL = 1;
	static final int GENERATED = 2;
	static final int KIND = 3;
	static final int CHAPTER = 4;
	static final int GROUP = 5;
	static final int SUPERCODE = 6;
	static final int CODE = 7;
	static final int CODE_SHORT = 8;
	static final int CODE_COMPACT = 9;
	static final int TEXT = 10;
	
	public static ICD10 load(final String id){
		return new ICD10(id);
	}
	
	public ICD10(final String parent, final String code, final String shortCode){
		create(null);
		set("Code", code); //$NON-NLS-1$
		set("encoded", shortCode); //$NON-NLS-1$
		set("parent", parent); //$NON-NLS-1$
		set("Text", getField(TEXT)); //$NON-NLS-1$
	}
	
	/*
	 * public String createParentCode(){ String code=getField(CODE); String ret="NIL"; String
	 * chapter=getField(CHAPTER); String group=chapter+":"+getField(GROUP); String
	 * supercode=getField(SUPERCODE); if(code.equals(supercode)){ if(code.equals(group)){
	 * if(code.equals(chapter)){ ret="NIL"; }else{ ret=chapter; } }else{ ret=group; } }else{
	 * ret=supercode; } return ret; }
	 */

	public String getEncoded(){
		return get("encoded"); //$NON-NLS-1$
	}
	
	public String getField(final int f){
		return getEncoded().split(";")[f]; //$NON-NLS-1$
	}
	
	public ICD10(){}
	
	protected ICD10(final String id){
		super(id);
	}
	
	@Override
	public String getLabel(){
		StringBuilder b = new StringBuilder();
		b.append(getCode()).append(" ").append(getText()); //$NON-NLS-1$
		return b.toString();
	}
	
	@Override
	protected String getTableName(){
		return "ICD10"; //$NON-NLS-1$
	}
	
	public String getCode(){
		return get("Code"); //$NON-NLS-1$
	}
	
	public String getText(){
		return get("Text"); //$NON-NLS-1$
	}
	
	public String getCodeSystemName(){
		return "ICD-10"; //$NON-NLS-1$
	}
	
	@SuppressWarnings("unchecked")
	public void setExt(final String name, final String value){
		Map ext = getExtInfo();
		ext.put(name, value);
		writeExtInfo(ext);
	}
	
	public String getExt(final String name){
		Map ext = getExtInfo();
		String ret = (String) ext.get(name);
		return checkNull(ret);
	}
	
	public Map getExtInfo(){
		return getMap(FLD_EXTINFO); //$NON-NLS-1$
	}
	
	public void writeExtInfo(final Map ext){
		setMap(FLD_EXTINFO, ext); //$NON-NLS-1$
	}
	
	@Override
	public boolean isDragOK(){
		if (getField(TERMINAL).equals("T")) { //$NON-NLS-1$
			return true;
		}
		return false;
	}
	
	public String getCodeSystemCode(){
		return "999"; //$NON-NLS-1$
	}
	
	public List<IAction> getActions(Verrechnet kontext){
		// TODO Auto-generated method stub
		return null;
	}
	
}
