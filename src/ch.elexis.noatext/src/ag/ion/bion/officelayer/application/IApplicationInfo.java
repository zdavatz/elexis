/****************************************************************************
 * ubion.ORS - The Open Report Suite                                        *
 *                                                                          *
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * Subproject: NOA (Nice Office Access)                                     *
 *                                                                          *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU Lesser General Public License Version 2.1.              *
 *                                                                          * 
 * GNU Lesser General Public License Version 2.1                            *
 * ======================================================================== *
 * Copyright 2003-2005 by IOn AG                                            *
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
 *  http://www.ion.ag                                                       *
 *  info@ion.ag                                                             *
 *                                                                          *
 ****************************************************************************/
 
/*
 * Last changes made by $Author: andreas $, $Date: 2006-10-04 14:14:28 +0200 (Mi, 04 Okt 2006) $
 */
package ag.ion.bion.officelayer.application;

/**
 * Information provider of an office application.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 10398 $
 */
public interface IApplicationInfo {
  
  /** Empty array of application info objects. */
  public static final IApplicationInfo[] EMPTY_APPLICATION_INFOS_ARRAY = new IApplicationInfo[0];
  
  //----------------------------------------------------------------------------
  /**
   * Returns home of the office application.
   * 
   * @return home of the office application
   * 
   * @author Andreas Bröcker
   */
  public String getHome();
  //----------------------------------------------------------------------------
  /**
   * Returns properties of the office application. The properties
   * will be invetigated from the bootstrap.ini file. Returns null
   * if the properties are not available.
   * 
   * @return properties of the office application or null
   * if the properties are not available
   * 
   * @author Andreas Bröcker
   */
  public IApplicationProperties getProperties();
  //----------------------------------------------------------------------------  
  /**
   * Returns major version of the office application. Returns <code>-1</code>
   * if the major version is not available.
   * 
   * @return major version of the office application or <code>-1</code>
   * if the major version is not available
   * 
   * @author Andreas Bröcker
   */
  public int getMajorVersion();
  //----------------------------------------------------------------------------  
  /**
   * Returns minor version of the office application. Returns <code>-1</code>
   * if the minor version is not available.
   * 
   * @return minor version of the office application or <code>-1</code>
   * if the minor version is not available
   * 
   * @author Andreas Bröcker
   */
  public int getMinorVersion();  
  //----------------------------------------------------------------------------  
  /**
   * Returns update version of the office application. Returns <code>-1</code>
   * if the update version is not available.
   * 
   * @return update version of the office application or <code>-1</code>
   * if the update version is not available
   * 
   * @author Andreas Bröcker
   */
  public int getUpdateVersion();  
  //----------------------------------------------------------------------------  
  
}