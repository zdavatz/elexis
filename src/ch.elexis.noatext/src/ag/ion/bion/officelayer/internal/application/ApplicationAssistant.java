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
package ag.ion.bion.officelayer.internal.application;

import ag.ion.bion.officelayer.application.IApplicationAssistant;
import ag.ion.bion.officelayer.application.IApplicationInfo;
import ag.ion.bion.officelayer.application.IApplicationProperties;
import ag.ion.bion.officelayer.application.OfficeApplicationException;

import ag.ion.bion.officelayer.runtime.IOfficeProgressMonitor;

import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;

import java.io.File;
import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Assistant for office applications.
 * 
 * @author Andreas Bröcker
 * @version $Revision: 10398 $
 */
public class ApplicationAssistant implements IApplicationAssistant {
  
  private static final String KEY_MAIN_PART = "OpenOffice.org"; //$NON-NLS-1$
  
  private static final String RELATIVE_BOOTSTRAP        = "program" + File.separator + "bootstrap"; //$NON-NLS-1$ //$NON-NLS-2$
  private static final String APPLICATION_EXECUTEABLE   = "soffice"; //$NON-NLS-1$
  private static final String PROGRAM_FOLDER            = "program"; //$NON-NLS-1$
  
  //----------------------------------------------------------------------------
  /**
   * Constructs new ApplicationAssistant.
   * 
   * @throws OfficeApplicationException if the office application assitant can not
   * be constructed
   * 
   * @author Andreas Bröcker
   */
  public ApplicationAssistant() throws OfficeApplicationException {
    this(null);
  }
  //----------------------------------------------------------------------------
  /**
   * Constructs new ApplicationAssistant.
   * 
   * @param nativeLibPath path to the ICE registry library
   * 
   * @throws OfficeApplicationException if the office application assitant can not
   * be constructed
   * 
   * @author Andreas Bröcker
   */
  public ApplicationAssistant(String nativeLibPath) throws OfficeApplicationException {
    if(System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {       //$NON-NLS-1$ //$NON-NLS-2$
      try {
        if(nativeLibPath != null)
          System.load(nativeLibPath + "\\ICE_JNIRegistry.dll"); //$NON-NLS-1$
        else
          System.loadLibrary("ICE_JNIRegistry"); //$NON-NLS-1$
      }
      catch(Throwable throwable) {
        throw new OfficeApplicationException(throwable);
      }
    }
  }
  //----------------------------------------------------------------------------
  /**
   * Returns informations about available local office applications.
   * 
   * @return informations about available local office applications
   * 
   * @author Andreas Bröcker
   */
  public IApplicationInfo[] getLocalApplications() {
    return getLocalApplications(null);
  }
  //----------------------------------------------------------------------------
  /**
   * Returns informations about available local office applications.
   * 
   * @param officeProgressMonitor office progress monitor to be used
   * 
   * @return informations about available local office applications
   * 
   * @author Andreas Bröcker
   * @author Markus Krüger
   */
  public IApplicationInfo[] getLocalApplications(IOfficeProgressMonitor officeProgressMonitor) {
    ArrayList arrayList = new ArrayList();
    if(System.getProperty("oo.application.path") != null) {
      String path = new File(System.getProperty("oo.application.path")).getAbsolutePath();
      IApplicationInfo applicationInfo = findLocalApplicationInfo(path);
      if(applicationInfo != null)
        arrayList.add(applicationInfo);
    }
    else if(System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) { //$NON-NLS-1$ //$NON-NLS-2$
      try {
        String[] possibleKeys = getPossibleKeys();
        if(officeProgressMonitor != null)
          officeProgressMonitor.beginTask(Messages.getString("ApplicationAssistant.monitor_message_scannig_registry"), possibleKeys.length); //$NON-NLS-1$
        for(int i=0, n=possibleKeys.length; i<n; i++) {
          if(officeProgressMonitor != null)
            officeProgressMonitor.beginSubTask(Messages.getString("ApplicationAssistant.monitor_scanning_key", possibleKeys[i])); //$NON-NLS-1$
          RegistryKey registryKey = Registry.openSubkey(Registry.HKEY_CLASSES_ROOT, possibleKeys[i], RegistryKey.ACCESS_READ);
          if(registryKey != null) {
            String path = registryKey.getDefaultValue();
            int position = path.indexOf(APPLICATION_EXECUTEABLE);
            if(position != -1) {              
              path = path.substring(1, position -9);
              IApplicationInfo applicationInfo = findLocalApplicationInfo(path);
              if(applicationInfo != null)
                arrayList.add(applicationInfo);              
            }
          }
          if(officeProgressMonitor != null)
            if(officeProgressMonitor.isCanceled())
              break;
        }
      }
      catch(Throwable throwable) {
        return IApplicationInfo.EMPTY_APPLICATION_INFOS_ARRAY;
      }
    }
    else {
      try {
        if(officeProgressMonitor != null)
          officeProgressMonitor.beginTask(Messages.getString("ApplicationAssistant.monitor_looking_for_office_application"), IOfficeProgressMonitor.WORK_UNKNOWN); //$NON-NLS-1$
        ArrayList possibleOfficeHomes = new ArrayList();
        File file = new File("/opt"); //$NON-NLS-1$
        findPossibleOfficeHomes(officeProgressMonitor, file, possibleOfficeHomes);
        file = new File("/usr"); //$NON-NLS-1$
        findPossibleOfficeHomes(officeProgressMonitor, file, possibleOfficeHomes);
        if(officeProgressMonitor != null)
          officeProgressMonitor.beginSubTask(Messages.getString("ApplicationAssistant.monitor_buildung_application_infos")); //$NON-NLS-1$
        String[] officeHomes = (String[])possibleOfficeHomes.toArray(new String[possibleOfficeHomes.size()]);
        for(int i=0, n=officeHomes.length; i<n; i++) {
          IApplicationInfo applicationInfo = findLocalApplicationInfo(officeHomes[i]);
          if(applicationInfo != null)
            arrayList.add(applicationInfo);
        }  
      }
      catch(Throwable throwable) {
        return IApplicationInfo.EMPTY_APPLICATION_INFOS_ARRAY;
      }
    }
    
    if(officeProgressMonitor != null)
      if(officeProgressMonitor.needsDone())
        officeProgressMonitor.done();
    
    return (IApplicationInfo[])arrayList.toArray(new IApplicationInfo[arrayList.size()]);
  }
  //----------------------------------------------------------------------------
  /**
   * Looks for application info on the basis of the submitted application 
   * home path. Returns null if the application info can not be provided.
   * 
   * @param home home path to be used
   * 
   * @return application info on the basis of the submitted application 
   * home path or null if the application info can not be provided
   * 
   * @author Andreas Bröcker
   */
  public IApplicationInfo findLocalApplicationInfo(String home) {
    if(home == null)
      return null;
    
    File file = null;
    if(System.getProperty("os.name").toLowerCase().indexOf("windows") != -1)  //$NON-NLS-1$ //$NON-NLS-2$
      file = new File(home + File.separator + PROGRAM_FOLDER + File.separator + APPLICATION_EXECUTEABLE + ".exe"); //$NON-NLS-1$
    else
      file = new File(home + File.separator + PROGRAM_FOLDER + File.separator + APPLICATION_EXECUTEABLE + ".bin"); //$NON-NLS-1$
    
    if(file.canRead()) 
      return new ApplicationInfo(home, findApplicationProperties(home));
    
    //fallback for OpenOffice.org 1.1.x
    if(System.getProperty("os.name").toLowerCase().indexOf("windows") == -1) { //$NON-NLS-1$ //$NON-NLS-2$
      file = new File(home + File.separator + PROGRAM_FOLDER + File.separator + APPLICATION_EXECUTEABLE + ".sh"); //$NON-NLS-1$
      if(file.canRead()) 
        return new ApplicationInfo(home, findApplicationProperties(home));
    }
    return null;
  }
  //----------------------------------------------------------------------------
  /**
   * Looks for possible office home path entries.
   * 
   * @param officeProgressMonitor office progress monitor to be used (can be null)
   * @param root root file entry to be used
   * @param list list to be filled with possible office home entries
   * 
   * @author Andreas Bröcker
   */
  private void findPossibleOfficeHomes(IOfficeProgressMonitor officeProgressMonitor, File root, List list) {
    if(root == null)
      return;
  
    if(root.isDirectory()) 
      if(officeProgressMonitor != null)
        officeProgressMonitor.beginSubTask(Messages.getString("ApplicationAssistant.monitor_scanning_directory", root.getName())); //$NON-NLS-1$
      
    File[] files = root.listFiles();    
    if(files != null) {
      for(int i=0, n=files.length; i<n; i++) {
        if(officeProgressMonitor !=null)
          if(officeProgressMonitor.isCanceled())
            break;
        File file = files[i];
        if(file != null) {
          String fileName = file.getName();
          boolean homePathIdentified = false;
          if(System.getProperty("os.name").toLowerCase().indexOf("windows") == -1) { //$NON-NLS-1$ //$NON-NLS-2$
            if(fileName.equals(APPLICATION_EXECUTEABLE + ".bin") || fileName.equals(APPLICATION_EXECUTEABLE + ".sh"))  //$NON-NLS-1$ //$NON-NLS-2$
              homePathIdentified = true;
          }
          else {
            if(fileName.equals(APPLICATION_EXECUTEABLE + ".exe")) //$NON-NLS-1$
              homePathIdentified = true;
          }
          
          if(homePathIdentified) {              
            File parent = file.getParentFile();
            if(parent != null) {
              parent = parent.getParentFile(); 
              if(parent != null)
                list.add(parent.getAbsolutePath());
            }
          }          
          if(file.isDirectory())            
            findPossibleOfficeHomes(officeProgressMonitor, file, list);          
        }
      }
    }    
  }  
  //----------------------------------------------------------------------------
  /**
   * Returns application properties on the basis of the submitted office home
   * path. Returns null if the application properties can not be found.
   * 
   * @param home home of the office application
   * 
   * @return application properties on the basis of the submitted office home
   * path or null if the application properties can not be found
   * 
   * @author Andreas Bröcker
   */
  private IApplicationProperties findApplicationProperties(String home) {
    File file = null;
    if(System.getProperty("os.name").toLowerCase().indexOf("windows") != -1)  //$NON-NLS-1$ //$NON-NLS-2$
      file = new File(home + File.separator + RELATIVE_BOOTSTRAP + ".ini"); //$NON-NLS-1$
    else
      file = new File(home + File.separator + RELATIVE_BOOTSTRAP + "rc"); //$NON-NLS-1$
    if(file.canRead()) {
      try {
        FileInputStream fileInputStream = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(fileInputStream);
        return new ApplicationProperties(properties);
      }
      catch(Throwable throwable) {
        return null;
      }
    }
    return null;
  }   
  //----------------------------------------------------------------------------
  /**
   * Returns possible windows registry keys of OpenOffice.org.
   * 
   * @return possible windows registry keys of OpenOffice.org
   * 
   * @author Andreas Bröcker
   */
  private String[] getPossibleKeys() {
    /**
     * Is this too much ????
     */    
    ArrayList arrayList = new ArrayList();
    int majorVersion    = 1;
    int minorVersion    = 0;
    int updateVersion   = 0;
    
    for(; majorVersion <= 4; minorVersion=0, majorVersion++) {
      arrayList.add("Applications\\" + KEY_MAIN_PART + " " + majorVersion + "." + minorVersion + "\\shell\\edit\\command"); //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-4$
      arrayList.add("Applications\\" + KEY_MAIN_PART + " " + majorVersion + "." + minorVersion + "." + updateVersion + "\\shell\\edit\\command"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
      for(updateVersion = 1; updateVersion <= 150; updateVersion++) {
        if(updateVersion < 10 || updateVersion > 80)
          arrayList.add("Applications\\" + KEY_MAIN_PART + " " + majorVersion + "." + minorVersion + "." + updateVersion + "\\shell\\edit\\command");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
      }      
      
      for(minorVersion = 1, updateVersion =1; minorVersion <= 10; updateVersion = 1, minorVersion++) {
        arrayList.add("Applications\\" + KEY_MAIN_PART + " " + majorVersion + "." + minorVersion + "\\shell\\edit\\command"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        arrayList.add("Applications\\" + KEY_MAIN_PART + " " + majorVersion + "." + minorVersion + "." + updateVersion + "\\shell\\edit\\command"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        for(updateVersion = 2; updateVersion <= 150; updateVersion++) {
          if(updateVersion < 10 || updateVersion > 80)
            arrayList.add("Applications\\" + KEY_MAIN_PART + " " + majorVersion + "." + minorVersion + "." + updateVersion + "\\shell\\edit\\command");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        }
      }
    }    
    return (String[])arrayList.toArray(new String[arrayList.size()]);
  }
  //----------------------------------------------------------------------------
  
}