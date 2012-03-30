/*******************************************************************************
 * Copyright (c) 2006-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id: FilterFactory.java 5024 2009-01-23 16:36:39Z rgw_ch $
 *******************************************************************************/

package ch.elexis.data;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.util.SWTHelper;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.Tree;

public class FilterFactory {
	public enum OPERATORS {
		IST, ENTHAELT, BEGINNT, ENDET, ISTNICHT, ENTHAELTNICHT, REGEXP
	};
	
	public enum LINK {
		AND, OR
	};
	
	public static final String[] OperatorNames = {
		"ist", "enthält", "beginnt mit", "endet mit", "ist nicht", "enthält nicht", "Regexp"
	};
	static final String[] LinkNames = {
		"UND", "ODER"
	};
	
	public static Filter createFilter(Class<? extends PersistentObject> clazz, String... strings){
		return new Filter(clazz, strings);
	}
	
	public static TitleAreaDialog createFilterDialog(Filter filter, Shell parent){
		FilterDialog dlg = new FilterDialog(parent, filter);
		dlg.create();
		dlg.setTitle("Filter");
		dlg.getShell().setText("Liste filtern");
		dlg.setMessage("Bitte die Filterbedingungen eingeben");
		return dlg;
	}
	
	public static class Filter extends ViewerFilter implements IFilter {
		private Class<? extends PersistentObject> mine;
		private String[] fields;
		private ArrayList<term> terms;
		
		private Filter(Class<? extends PersistentObject> clazz, String... strings){
			mine = clazz;
			fields = strings;
			terms = new ArrayList<term>();
		}
		
		public boolean addTerm(LINK l, String field, OPERATORS op, String val){
			if (StringTool.isNothing(field)) {
				return false;
			}
			if (l == null) {
				l = LINK.OR;
			}
			if (val == null) {
				val = "";
			}
			terms.add(new term(l, field, op, val));
			return true;
		}
		
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element){
			return select(element);
		}
		
		@SuppressWarnings("unchecked")
		public boolean select(Object toTest){
			if (toTest instanceof Tree) {
				toTest = ((Tree) toTest).contents;
			}
			if (toTest.getClass().equals(mine)) {
				PersistentObject po = (PersistentObject) toTest;
				boolean result = false;
				for (term t : terms) {
					boolean bTerm = checkTerm(po, t);
					if (bTerm == false) {
						if (t.link.equals(LINK.AND)) {
							result = false;
							
						}/*
						 * else{ // false OR true result=result; }
						 */
					} else { // term==true
						if (t.link.equals(LINK.OR)) {
							result = true;
						}/*
						 * else{ result=result; }
						 */
					}
				}
				return result;
			}
			
			return false;
		}
		
		private boolean checkTerm(PersistentObject p, term t){
			if (!StringTool.isNothing(t.value)) {
				String val = p.get(t.field);
				if (StringTool.isNothing(val)) {
					return false;
				}
				switch (t.op) {
				case BEGINNT:
					if (!val.startsWith(t.value)) {
						return false;
					}
					break;
				case ENTHAELT:
					if (val.indexOf(t.value) == -1) {
						return false;
					}
					break;
				case ENDET:
					if (!val.endsWith(t.value)) {
						return false;
					}
					break;
				case ENTHAELTNICHT:
					if (val.indexOf(t.value) != -1) {
						return false;
					}
					break;
				case IST:
					if (!val.equalsIgnoreCase(t.value)) {
						return false;
					}
					break;
				case ISTNICHT:
					if (val.equalsIgnoreCase(t.value)) {
						return false;
					}
					break;
				case REGEXP:
					if (!val.matches(t.value)) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
		
		private static class term {
			LINK link;
			String field;
			OPERATORS op;
			String value;
			
			term(LINK l, String f, OPERATORS o, String v){
				link = l;
				field = f;
				op = o;
				value = v;
			}
		}
	}
	
	/*
	 * public static class Filter extends ViewerFilter implements IFilter{ private Class mine;
	 * private String[] fields; private String[] values; private OPERATORS[] operators; private
	 * Filter(Class clazz, String...strings ){ mine=clazz; fields=strings; operators=new
	 * OPERATORS[fields.length]; values=new String[fields.length]; } public Query createQuery(){
	 * Query qbe=new Query(mine); modifyQuery(qbe); return qbe; } public void modifyQuery(Query
	 * qbe){ for(int i=0;i<fields.length;i++){ if(!ch.rgw.tools.StringTool.isNothing(values[i])){
	 * qbe.and(); qbe.add(fields[i],operators[i].name(),values[i]); } } } public boolean
	 * select(Object toTest) { if(toTest.getClass().equals(mine)){ PersistentObject
	 * p=(PersistentObject)toTest; for(int i=0;i<fields.length;i++){
	 * if(!StringTool.isNothing(values[i])){ String val=p.get(fields[i]);
	 * if(StringTool.isNothing(val)){ return false; } switch(operators[i]){ case BEGINNT:
	 * if(!val.startsWith(values[i])){ return false; } break; case ENTHAELT:
	 * if(val.indexOf(values[i])==-1){ return false; } break; case ENDET:
	 * if(!val.endsWith(values[i])){ return false; } break; case ENTHAELTNICHT:
	 * if(val.indexOf(values[i])!=-1){ return false; } break; case IST:
	 * if(!val.equalsIgnoreCase(values[i])){ return false; } break; case ISTNICHT:
	 * if(val.equalsIgnoreCase(values[i])){ return false; } break; case REGEXP:
	 * if(!val.matches(values[i])){ return false; } } return true; } } return true; } return false;
	 * }
	 * 
	 * @Override public boolean select(Viewer viewer, Object parentElement, Object element) { return
	 * select(element); }
	 * 
	 * @Override public boolean isFilterProperty(Object element, String property) {
	 * 
	 * return true; }
	 * 
	 * 
	 * }
	 */
	static class FilterDialog extends TitleAreaDialog {
		private static final int num = 4;
		private Filter flt;
		private Combo[] cLinks;
		private Combo[] cFields;
		private Text[] tValues;
		private Combo[] cOps;
		private String[] fields;
		
		public FilterDialog(Shell parentShell, Filter filter){
			super(parentShell);
			flt = filter;
			cLinks = new Combo[num];
			cFields = new Combo[num];
			tValues = new Text[num];
			cOps = new Combo[num];
			fields = new String[flt.fields.length + 1];
			fields[0] = "-";
			for (int i = 1; i < fields.length; i++) {
				fields[i] = flt.fields[i - 1];
			}
		}
		
		@Override
		protected Control createDialogArea(Composite parent){
			Composite ret = new Composite(parent, SWT.NONE);
			ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
			ret.setLayout(new GridLayout(4, false));
			ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
			for (int i = 0; i < num; i++) {
				cLinks[i] = new Combo(ret, SWT.SINGLE);
				cLinks[i].setItems(LinkNames);
				cFields[i] = new Combo(ret, SWT.SINGLE);
				cFields[i].setItems(fields);
				cOps[i] = new Combo(ret, SWT.SINGLE);
				cOps[i].setItems(OperatorNames);
				tValues[i] = new Text(ret, SWT.BORDER);
				tValues[i].setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
			}
			cLinks[0].setEnabled(false);
			
			return ret;
		}
		
		@Override
		protected void okPressed(){
			flt.terms.clear();
			for (int i = 0; i < num; i++) {
				int s = cFields[i].getSelectionIndex();
				if (s > 0) {
					int linkOP = cLinks[i].getSelectionIndex();
					if (linkOP == -1) {
						linkOP = 1;
					}
					LINK l = LINK.values()[linkOP];
					int idxOp = cOps[i].getSelectionIndex();
					OPERATORS op = OPERATORS.values()[idxOp];
					flt.addTerm(l, cFields[i].getItem(s), op, tValues[i].getText());
				}
			}
			super.okPressed();
		}
		
	}
}
