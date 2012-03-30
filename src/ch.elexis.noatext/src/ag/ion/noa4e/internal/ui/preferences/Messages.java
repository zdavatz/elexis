/****************************************************************************
 *                                                                          *
 * NOA (Nice Office Access)                                     						*
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU Lesser General Public License Version 2.1.              *
 *                                                                          * 
 * GNU Lesser General Public License Version 2.1                            *
 * ======================================================================== *
 * Copyright 2003-2006 by IOn AG                                            *
 *                                                                          *
 * This library is free software; you can redistribute it and/or            *
 * modify it under the terms of the GNU Lesser General Public               *
 * License version 2.1, as published by the Free Software Foundation.       *
 *                                                                          *
 * This library is distributed in the hope that it will be useful,          *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        *
 * Lesser General Public License for more details.                          *
 *                                                                          *
 * You should have received a copy of the GNU Lesser General Public         *
 * License along with this library; if not, write to the Free Software      *
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,                    *
 * MA  02111-1307  USA                                                      *
 *                                                                          *
 * Contact us:                                                              *
 *  http://www.ion.ag																												*
 *  http://ubion.ion.ag                                                     *
 *  info@ion.ag                                                             *
 *                                                                          *
 ****************************************************************************/
 
/*
 * Last changes made by $Author: andreas $, $Date: 2006/08/07 11:09:18 $
 */
package ag.ion.noa4e.internal.ui.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * Native language binding.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 1.2 $
 */
public class Messages extends NLS {
  
  private static final String BUNDLE_NAME = "ag.ion.noa4e.internal.ui.preferences.messages"; //$NON-NLS-1$

  private Messages() {
  }

  static {
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  public static String LocalOfficeApplicationPreferencesPage_link_define_text;
  public static String LocalOfficeApplicationPreferencesPage_label_application_properties_text;
  public static String LocalOfficeApplicationPreferencesPage_column_name_text;
  public static String LocalOfficeApplicationPreferencesPage_column_value_text;
  
  public static String LocalOfficeApplicationPreferencesPage_description_configure_application;
  public static String LocalOfficeApplicationPreferencesPage_label_application_home;


  public static String LocalOfficeApplicationPreferencesPage_dialog_restart_workbench_title;
  public static String LocalOfficeApplicationPreferencesPage_dialog_restart_workbench_message;
  public static String LocalOfficeApplicationPreferencesPage_prevent_termination_lable;
 
}
