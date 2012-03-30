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
 * Last changes made by $Author: markus $, $Date: 2007-07-11 13:39:54 +0200 (Mi, 11 Jul 2007) $
 */
package ag.ion.bion.officelayer.internal.application.connection;

import ag.ion.bion.officelayer.runtime.IOfficeProgressMonitor;

import com.sun.star.comp.beans.ContainerFactory;
import com.sun.star.comp.beans.LocalOfficeWindow;
import com.sun.star.comp.beans.OfficeConnection;
import com.sun.star.comp.beans.OfficeWindow;

import com.sun.star.lang.EventObject;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XEventListener;

import com.sun.star.connection.XConnection;
import com.sun.star.connection.XConnector;

import com.sun.star.bridge.XBridge;
import com.sun.star.bridge.XBridgeFactory;

import com.sun.star.beans.XPropertySet;
import com.sun.star.uno.XComponentContext;

import com.sun.star.uno.UnoRuntime;

import com.sun.star.lib.uno.helper.UnoUrl;

import com.sun.star.lib.util.NativeLibraryLoader;

import java.awt.Container;

import java.io.File;

import java.net.MalformedURLException;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Office connection implementation. This class bases on the implementation
 * of the OpenOffice.org API Bean package.
 * 
 * @author Andreas Bröcker
 * @author Markus Krüger
 * @version $Revision: 11514 $
 */
public class LocalOfficeConnectionGhost implements OfficeConnection {

  //TODO: adapt to modifications of OpenOffice.org !

  private static final String     OFFICE_APP_NAME         = "soffice";   //$NON-NLS-1$
  private static final String     OFFICE_LIB_NAME         = "officebean"; //$NON-NLS-1$
  private static final String     OFFICE_ID_SUFFIX        = "_Office";   //$NON-NLS-1$

  private static boolean          msvcrLoaded             = false;
  private static boolean          uwinapiLoaded           = false;
  private static boolean          jawtLoaded              = false;
  private static boolean          officebeanLoaded        = false;

  private static long             m_nBridgeCounter        = 0;

  private static String           mProgramPath            = null;

  private Process                 process                 = null;
  private ContainerFactory        containerFactory        = null;
  private XComponentContext       context                 = null;
  private XBridge                 bridge                  = null;

  private String                  url                     = null;
  private String                  connType                = null;
  private String                  pipe                    = null;
  private String                  port                    = null;
  private String                  protocol                = null;
  private String                  initialObject           = null;

  private List                    components              = new Vector();

  private OfficeConnectionWrapper officeConnectionWrapper = null;
  private IOfficeProgressMonitor  officeProgressMonitor   = null;

  //----------------------------------------------------------------------------
  /**
   * Internal office connection wrapper.
   * 
   * @author Andreas Bröcker
   */
  private class OfficeConnectionWrapper implements OfficeConnection {

    //----------------------------------------------------------------------------
    /**
     * Sets a connection URL.
     * 
     * This implementation accepts a UNO URL with following format:<br />
     * 
     * <pre>
     *  url    := uno:localoffice[,&lt;params&gt;];urp;StarOffice.ServiceManager
     *  params := &lt;path&gt;[,&lt;pipe&gt;]
     *  path   := path=&lt;pathv&gt;
     *  pipe   := pipe=&lt;pipev&gt;
     *  pathv  := platform_specific_path_to_the_local_office_distribution
     *  pipev  := local_office_connection_pipe_name
     * </pre>
     * 
     * @param url
     *          this is UNO URL which discribes the type of a connection
     * 
     * @throws MalformedURLException
     *           if the URL is not valid
     * 
     * @author Andreas Bröcker
     */
    public void setUnoUrl(String url) throws MalformedURLException {
      LocalOfficeConnectionGhost.this.setUnoUrl(url);
    }
    //----------------------------------------------------------------------------
    /**
     * Sets an AWT container catory.
     * 
     * @param containerFactory
     *          this is a application provided AWT container factory
     * 
     * @author Andreas Bröcker
     */
    public void setContainerFactory(ContainerFactory containerFactory) {
      LocalOfficeConnectionGhost.this.setContainerFactory(containerFactory);
    }
    //----------------------------------------------------------------------------
    /**
     * Retrives the UNO component context.
     * 
     * Establishes a connection if necessary and initialises the UNO service
     * manager if it has not already been initialised. This method can return
     * <code>null</code> if it fails to connect to the office application.
     * 
     * @return the office UNO component context
     * 
     * @author Andreas Bröcker
     */
    public XComponentContext getComponentContext() {
      return LocalOfficeConnectionGhost.this.getComponentContext();
    }
    //----------------------------------------------------------------------------
    /**
     * Creates an office window.
     * 
     * The window is either a sub-class of java.awt.Canvas (local) or
     * java.awt.Container (RVP).
     * 
     * @param container
     *          this is an AWT container
     * 
     * @return the office window instance
     * 
     * @author Andreas Bröcker
     */
    public OfficeWindow createOfficeWindow(Container container) {
      return LocalOfficeConnectionGhost.this.createOfficeWindow(container);
    }
    //----------------------------------------------------------------------------
    /**
     * Closes the connection.
     * 
     * @author Andreas Bröcker
     */
    public void dispose() {
      LocalOfficeConnectionGhost.this.dispose();
    }
    //----------------------------------------------------------------------------
    /**
     * Adds an event listener to the object.
     * 
     * @param eventListener
     *          is a listener object
     * 
     * @author Andreas Bröcker
     */
    public void addEventListener(XEventListener eventListener) {
      LocalOfficeConnectionGhost.this.addEventListener(eventListener);
    }
    //----------------------------------------------------------------------------
    /**
     * Removes an event listener from the listener list.
     * 
     * @param eventListener
     *          is a listener object
     * 
     * @author Andreas Bröcker
     */
    public void removeEventListener(XEventListener eventListener) {
      LocalOfficeConnectionGhost.this.removeEventListener(eventListener);
    }
    //----------------------------------------------------------------------------

  }

  //----------------------------------------------------------------------------

  //----------------------------------------------------------------------------
  /**
   * Internal local office window wrapper.
   * 
   * @author Andreas Bröcker
   */
  private class LocalOfficeWindowWrapper extends LocalOfficeWindow {

    //----------------------------------------------------------------------------
    /**
     * Constructs new LocalOfficeWindowWrapper.
     * 
     * @param officeConnection office connection to be used
     * 
     * @author Andreas Bröcker
     */
    protected LocalOfficeWindowWrapper(OfficeConnection officeConnection) {
      super(officeConnection);
    }
    //----------------------------------------------------------------------------

  }

  //----------------------------------------------------------------------------

//  //----------------------------------------------------------------------------
//  /**
//   * Internal stream processor.
//   * 
//   * @author Andreas Bröcker
//   */
//  private class StreamProcessor extends Thread {
//    
//    private java.io.InputStream inputStream = null;
//    private java.io.PrintStream printStream = null;
//
//    //----------------------------------------------------------------------------
//    /**
//     * Constructs new StreamProcessor.
//     * 
//     * @param inputStream input stream to be used
//     * @param printStream print stream to be used
//     * 
//     * @author Andreas Bröcker
//     */ 
//    public StreamProcessor(final java.io.InputStream inputStream, final java.io.PrintStream printStream) {
//      this.inputStream = inputStream;
//      this.printStream = printStream;
//      start();
//    }
//    //----------------------------------------------------------------------------
//    /**
//     * Processes streams.
//     * 
//     * @author Andreas Bröcker
//     */
//    public void run() {
//      java.io.BufferedReader bufferedReader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream));
//      try {
//        for (;;) {
//          String string = bufferedReader.readLine();
//          if (string == null) {
//            break;
//          }
//          printStream.println(string);
//        }
//      }
//      catch (java.io.IOException ioException) {
//        ioException.printStackTrace(System.err);
//      }
//    }
//    //----------------------------------------------------------------------------
//    
//  }
//  //----------------------------------------------------------------------------

  //----------------------------------------------------------------------------
  /**
   * Internal service in order to start the native office application.
   * 
   * @author Andreas Bröcker
   */
  private class OfficeService {

    //----------------------------------------------------------------------------
    /**
     * Retrive the office service identifier.
     * 
     * @return The identifier of the office service.
     */
    public String getIdentifier() {
      if(pipe == null)
        return getPipeName();
      else
        return pipe;
    }
    //----------------------------------------------------------------------------
    /**
     * Starts the office process.
     * 
     * @throws java.io.IOException if the service can not be started
     * 
     * @author Andreas Bröcker
     */
    public void startupService() throws java.io.IOException {
      int nSizeCmdArray = 5;
      String sOption = null;
      // examine if user specified command-line options in system properties.
      // We may offer later a more sophisticated way of providing options if
      // the need arises. Currently this is intended to ease the pain during
      // development with pre-release builds of OOo where one wants to start
      // OOo with the -norestore options. The value of the property is simple
      // passed on to the Runtime.exec call.
      try {
        sOption = System.getProperty("com.sun.star.officebean.Options"); //$NON-NLS-1$
        if(sOption != null)
          nSizeCmdArray++;
      }
      catch(java.lang.SecurityException securityException) {
        securityException.printStackTrace();
      }
      // create call with arguments
      String[] cmdArray = new String[nSizeCmdArray];
      cmdArray[0] = (new File(getProgramPath(), OFFICE_APP_NAME)).getPath();
      cmdArray[1] = "-nologo"; //$NON-NLS-1$
      cmdArray[2] = "-nodefault"; //$NON-NLS-1$
      cmdArray[3] = "-norestore"; //$NON-NLS-1$
      if(connType.equals("pipe")) //$NON-NLS-1$
        cmdArray[4] = "-accept=pipe,name=" + getIdentifier() + ";" + protocol //$NON-NLS-1$ //$NON-NLS-2$
            + ";" + initialObject; //$NON-NLS-1$
      else if(connType.equals("socket")) //$NON-NLS-1$
        cmdArray[4] = "-accept=socket,port=" + port + ";urp"; //$NON-NLS-1$ //$NON-NLS-2$
      else
        throw new java.io.IOException("No connection specified"); //$NON-NLS-1$

      if(sOption != null)
        cmdArray[5] = sOption;

      // start process
      process = Runtime.getRuntime().exec(cmdArray);
      if(process == null)
        throw new RuntimeException("Cannot start soffice: " + cmdArray); //$NON-NLS-1$
      //new StreamProcessor(process.getInputStream(), System.out);
      //new StreamProcessor(process.getErrorStream(), System.err);
    }
    //----------------------------------------------------------------------------
    /**
     * Retrives the ammount of time to wait for the startup.
     * 
     * @return the ammount of time to wait in seconds(?)
     * 
     * @author Andreas Bröcker
     */
    public int getStartupTime() {
      return 60;
    }
    //----------------------------------------------------------------------------

  }
  //----------------------------------------------------------------------------

  //----------------------------------------------------------------------------
  /**
   * Constructs new LocalOfficeConnectionGhost.
   * 
   * Sets up paths to the office application and native libraries if values are
   * available in <code>OFFICE_PROP_FILE</code> in the user home directory.<br />
   * "com.sun.star.beans.path" - the office application directory;<br/>
   * "com.sun.star.beans.libpath" - native libraries directory.
   * 
   * @param officeProgressMonitor
   *          office progress monitor to be used (can be null)
   * 
   * @author Andreas Bröcker
   */
  public LocalOfficeConnectionGhost(IOfficeProgressMonitor officeProgressMonitor) {
    loadNativeLibraries();
    this.officeProgressMonitor = officeProgressMonitor;
    try {
      setUnoUrl("uno:pipe,name=" + getPipeName() //$NON-NLS-1$
          + ";urp;StarOffice.ServiceManager"); //$NON-NLS-1$
    }
    catch(java.net.MalformedURLException malformedURLException) {
      // do not consume
    }
  }
  //----------------------------------------------------------------------------
  /**
   * Sets a connection URL.
   * 
   * This implementation accepts a UNO URL with following format:<br />
   * 
   * <pre>
   *  url    := uno:localoffice[,&lt;params&gt;];urp;StarOffice.ServiceManager
   *  params := &lt;path&gt;[,&lt;pipe&gt;]
   *  path   := path=&lt;pathv&gt;
   *  pipe   := pipe=&lt;pipev&gt;
   *  pathv  := platform_specific_path_to_the_local_office_distribution
   *  pipev  := local_office_connection_pipe_name
   * </pre>
   * 
   * @param url
   *          this is UNO URL which discribes the type of a connection
   * 
   * @throws MalformedURLException
   *           if the URL is not valid
   * 
   * @author Andreas Bröcker
   */
  public void setUnoUrl(String url) throws java.net.MalformedURLException {
    this.url = null;

    String prefix = "uno:localoffice"; //$NON-NLS-1$
    if(url.startsWith(prefix))
      parseUnoUrlWithOfficePath(url, prefix);
    else {
      try {
        UnoUrl aURL = UnoUrl.parseUnoUrl(url);
        mProgramPath = null;
        connType = aURL.getConnection();
        pipe = (String) aURL.getConnectionParameters().get("pipe"); //$NON-NLS-1$
        port = (String) aURL.getConnectionParameters().get("port"); //$NON-NLS-1$
        protocol = aURL.getProtocol();
        initialObject = aURL.getRootOid();
      }
      catch(com.sun.star.lang.IllegalArgumentException illegalArgumentException) {
        throw new java.net.MalformedURLException("Invalid UNO connection URL."); //$NON-NLS-1$
      }
    }
    this.url = url;
  }
  //----------------------------------------------------------------------------
  /**
   * Sets an AWT container catory.
   * 
   * @param containerFactory
   *          this is a application provided AWT container factory
   * 
   * @author Andreas Bröcker
   */
  public void setContainerFactory(ContainerFactory containerFactory) {
    this.containerFactory = containerFactory;
  }
  //----------------------------------------------------------------------------
  /**
   * Retrives the UNO component context.
   * 
   * Establishes a connection if necessary and initialises the UNO service
   * manager if it has not already been initialised. This method can return
   * <code>null</code> if it fails to connect to the office application.
   * 
   * @return the office UNO component context
   * 
   * @author Andreas Bröcker
   */
  synchronized public XComponentContext getComponentContext() {
    if(officeProgressMonitor != null)
      if(officeProgressMonitor.isCanceled())
        return null;

    if(context == null)
      context = connect();
    return context;
  }
  //----------------------------------------------------------------------------
  /**
   * Retrives the UNO component context. If no context is set, null will be returned.
   * There will be no try to connect.
   * 
   * @return the office UNO component context
   * 
   * @author Markus Krüger
   */
  synchronized public XComponentContext getCurrentComponentContext() {
    if(officeProgressMonitor != null)
      if(officeProgressMonitor.isCanceled())
        return null;
    return context;
  }
  //----------------------------------------------------------------------------
  /**
   * Creates an office window.
   * 
   * The window is either a sub-class of java.awt.Canvas (local) or
   * java.awt.Container (RVP).
   * 
   * @param container
   *          this is an AWT container
   * 
   * @return the office window instance
   * 
   * @author Andreas Bröcker
   */
  public OfficeWindow createOfficeWindow(Container container) {
    if(officeConnectionWrapper == null)
      officeConnectionWrapper = new OfficeConnectionWrapper();
    return new LocalOfficeWindowWrapper(officeConnectionWrapper);
  }
  //----------------------------------------------------------------------------
  /**
   * Closes the connection.
   * 
   * @author Andreas Bröcker
   */
  public void dispose() {
    Iterator iterator = components.iterator();
    while(iterator.hasNext() == true) {
      try {
        ((XEventListener) iterator.next()).disposing(null);
      }
      catch(RuntimeException runtimeException) {
        // do not consume
      }
    }
    components.clear();

    // Terminate the bridge. It turned out that this is necessary for the bean
    // to work properly when displayed in an applet within Internet Explorer.
    // When navigating off the page which is showing the applet and then going
    // back to it, then the Java remote bridge is damaged. That is the Java
    // threads
    // do not work properly anymore. Therefore when Applet.stop is called the
    // connection
    // to the office including the bridge needs to be terminated.
    if(bridge != null) {
      XComponent xComponent = (XComponent) UnoRuntime.queryInterface(
          XComponent.class, bridge);
      if(xComponent != null)
        xComponent.dispose();
      else
        System.err.println("LocalOfficeConnection: could not dispose bridge!"); //$NON-NLS-1$
      bridge = null;
    }

    containerFactory = null;
    context = null;
  }
  //----------------------------------------------------------------------------
  /**
   * Adds an event listener to the object.
   * 
   * @param eventListener
   *          is a listener object
   * 
   * @author Andreas Bröcker
   */
  public void addEventListener(XEventListener eventListener) {
    if(eventListener == null)
      return;
    components.add(eventListener);
  }
  //----------------------------------------------------------------------------
  /**
   * Removes an event listener from the listener list.
   * 
   * @param eventListener
   *          is a listener object
   * 
   * @author Andreas Bröcker
   */
  public void removeEventListener(XEventListener eventListener) {
    if(eventListener == null)
      return;
    components.remove(eventListener);
  }
  //----------------------------------------------------------------------------
  /**
   * Establishes the connection to the office.
   * 
   * @return constructed component context
   * 
   * @author Andreas Bröcker
   */
  private XComponentContext connect() {
    try {
      if(officeProgressMonitor != null)
        officeProgressMonitor
            .beginSubTask(Messages
                .getString("LocalOfficeConnectionGhost_monitor_constructing_initial_context_message")); //$NON-NLS-1$
      XComponentContext xLocalContext = com.sun.star.comp.helper.Bootstrap
          .createInitialComponentContext(null);

      if(officeProgressMonitor != null)
        officeProgressMonitor.worked(1);

      Object initialObject = null;
      boolean interrupted = false;
      try {
        initialObject = resolve(xLocalContext, url);
      }
      catch(com.sun.star.connection.NoConnectException noConnectException) {
        if(officeProgressMonitor != null)
          officeProgressMonitor
              .beginSubTask(Messages
                  .getString("LocalOfficeConnectionGhost_monitor_starting_native_service_message")); //$NON-NLS-1$
        OfficeService officeService = new OfficeService();
        officeService.startupService();

        long nMaxMillis = System.currentTimeMillis() + 1000
            * officeService.getStartupTime();
        while(initialObject == null) {
          try {
            Thread.sleep(500);
            initialObject = resolve(xLocalContext, url);
            if(officeProgressMonitor != null) {
              if(officeProgressMonitor.isCanceled())
                return null;
            }
          }
          catch(com.sun.star.connection.NoConnectException innerNoConnectException) {
            if(System.currentTimeMillis() > nMaxMillis)
              throw innerNoConnectException;
          }
          catch(InterruptedException interruptedException) {
            interrupted = true;
          }
        }
      }
      finally {
      }

      if(interrupted)
        Thread.currentThread().interrupt();

      if(officeProgressMonitor != null) {
        officeProgressMonitor.worked(1);
        if(officeProgressMonitor.isCanceled())
          return null;
      }

      if(null != initialObject) {
        if(officeProgressMonitor != null)
          officeProgressMonitor
              .beginSubTask(Messages
                  .getString("LocalOfficeConnectionGhost_monitor_constructing_context_message")); //$NON-NLS-1$
        XPropertySet xPropertySet = (XPropertySet) UnoRuntime.queryInterface(
            XPropertySet.class, initialObject);
        Object xContext = xPropertySet.getPropertyValue("DefaultContext"); //$NON-NLS-1$
        XComponentContext xComponentContext = (XComponentContext) UnoRuntime
            .queryInterface(XComponentContext.class, xContext);
        if(officeProgressMonitor != null)
          officeProgressMonitor.worked(1);
        return xComponentContext;
      }
    }
    catch(com.sun.star.connection.NoConnectException exception) {
      System.out.println("Couldn't connect to remote server"); //$NON-NLS-1$
      System.out.println(exception.getMessage());
    }
    catch(com.sun.star.connection.ConnectionSetupException exception) {
      System.out
          .println("Couldn't access necessary local resource to establish the interprocess connection"); //$NON-NLS-1$
      System.out.println(exception.getMessage());
    }
    catch(com.sun.star.lang.IllegalArgumentException exception) {
      System.out.println("uno-url is syntactical illegal ( " + url + " )"); //$NON-NLS-1$ //$NON-NLS-2$
      System.out.println(exception.getMessage());
    }
    catch(com.sun.star.uno.RuntimeException exception) {
      System.out.println("--- RuntimeException:"); //$NON-NLS-1$
      System.out.println(exception.getMessage());
      exception.printStackTrace();
      System.out.println("--- end."); //$NON-NLS-1$
      throw exception;
    }
    catch(java.lang.Exception exception) {
      System.out.println("java.lang.Exception: "); //$NON-NLS-1$
      System.out.println(exception);
      exception.printStackTrace();
      System.out.println("--- end."); //$NON-NLS-1$
      throw new com.sun.star.uno.RuntimeException(exception.toString());
    }

    return null;
  }
  //----------------------------------------------------------------------------
  /**
   * Resolves initial object.
   * 
   * @param xComponentContext component context to be used
   * @param url url to be used
   * 
   * @return initial object
   * 
   * @throws com.sun.star.connection.NoConnectException if no connection is available
   * @throws com.sun.star.connection.ConnectionSetupException if the connection can not be initialized
   * @throws com.sun.star.lang.IllegalArgumentException if the submitted arguments are not valid
   * 
   * @author Andreas Bröcker
   */
  private Object resolve(XComponentContext xComponentContext, String url)
      throws com.sun.star.connection.NoConnectException,
      com.sun.star.connection.ConnectionSetupException,
      com.sun.star.lang.IllegalArgumentException {

    // The function is copied and adapted from the UrlResolver.resolve.
    // We cannot use the URLResolver because we need access to the bridge which
    // has
    // to be disposed when Applet.stop is called.

    String conDcp = null;
    String protDcp = null;
    String rootOid = null;

    if(url.indexOf(';') == -1) {// use old style
      conDcp = url;
      protDcp = "iiop"; //$NON-NLS-1$
      rootOid = "classic_uno"; //$NON-NLS-1$
    }
    else { // new style
      int index = url.indexOf(':');
      url = url.substring(index + 1).trim();

      index = url.indexOf(';');
      conDcp = url.substring(0, index).trim();
      url = url.substring(index + 1).trim();

      index = url.indexOf(';');
      protDcp = url.substring(0, index).trim();
      url = url.substring(index + 1).trim();

      rootOid = url.trim().trim();
    }

    Object rootObject = null;
    XBridgeFactory xBridgeFactory = null;

    XMultiComponentFactory xLocalServiceManager = xComponentContext
        .getServiceManager();
    try {
      xBridgeFactory = (XBridgeFactory) UnoRuntime.queryInterface(
          XBridgeFactory.class, xLocalServiceManager.createInstanceWithContext(
              "com.sun.star.bridge.BridgeFactory", xComponentContext)); //$NON-NLS-1$
    }
    catch(com.sun.star.uno.Exception exception) {
      throw new com.sun.star.uno.RuntimeException(exception.getMessage());
    }
    synchronized(this) {
      if(bridge == null) {
        Object connector = null;
        try {
          connector = xLocalServiceManager.createInstanceWithContext(
              "com.sun.star.connection.Connector", xComponentContext); //$NON-NLS-1$
        }
        catch(com.sun.star.uno.Exception exception) {
          throw new com.sun.star.uno.RuntimeException(exception.getMessage());
        }
        XConnector connector_xConnector = (XConnector) UnoRuntime
            .queryInterface(XConnector.class, connector);
        // connect to the server
        XConnection xConnection = connector_xConnector.connect(conDcp);
        // create the bridge name. This should not be necessary if we pass an
        // empty string as bridge name into createBridge. Then we should always
        // get
        // a new bridge. This does not work because of (i51323). Therefore we
        // create unique bridge names for the current process.
        String sBridgeName = "OOoBean_private_bridge_" //$NON-NLS-1$
            + String.valueOf(m_nBridgeCounter++);
        try {
          bridge = xBridgeFactory.createBridge(sBridgeName, protDcp,
              xConnection, null);
          XComponent xComponent = (XComponent) UnoRuntime.queryInterface(
              XComponent.class, bridge);
          xComponent.addEventListener(new XEventListener() {

            public void disposing(EventObject eventObject) {
              containerFactory = null;
              context = null;
              bridge = null;
            }
          });
        }
        catch(com.sun.star.bridge.BridgeExistsException bridgeExistsException) {
          throw new com.sun.star.uno.RuntimeException(bridgeExistsException
              .getMessage());
        }
      }
      rootObject = bridge.getInstance(rootOid);
      return rootObject;
    }
  }
  //----------------------------------------------------------------------------
  /**
   * Retrives a path to the office program folder.
   * 
   * @return the path to the office program folder
   * 
   * @author Andreas Bröcker
   */
  private String getProgramPath() {
    if(mProgramPath == null) {
      String officeHomePath = System.getProperty("office.home"); //$NON-NLS-1$
      if(officeHomePath != null)
        return officeHomePath + File.separator + "program"; //$NON-NLS-1$
      // determine name of executable soffice
      String exec = OFFICE_APP_NAME; // default for UNIX
      String os = System.getProperty("os.name"); //$NON-NLS-1$

      // running on Windows?
      if(os.startsWith("Windows")) //$NON-NLS-1$
        exec = OFFICE_APP_NAME + ".exe"; //$NON-NLS-1$

      // add other non-UNIX operating systems here
      // ...

      File path = NativeLibraryLoader.getResource(LocalOfficeConnection.class
          .getClassLoader(), exec);
      if(path != null)
        mProgramPath = path.getParent();

      if(mProgramPath == null)
        mProgramPath = ""; //$NON-NLS-1$
    }
    return mProgramPath;
  }
  //----------------------------------------------------------------------------
  /**
   * Parses a connection URL. This method accepts a UNO URL with following
   * format:<br />
   * 
   * <pre>
   *  url    := uno:localoffice[,&lt;params&gt;];urp;StarOffice.NamingService
   *  params := &lt;path&gt;[,&lt;pipe&gt;]
   *  path   := path=&lt;pathv&gt;
   *  pipe   := pipe=&lt;pipev&gt;
   *  pathv  := platform_specific_path_to_the_local_office_distribution
   *  pipev  := local_office_connection_pipe_name
   * </pre>
   * 
   * <h4>Examples</h4>
   * <ul>
   * <li>"uno:localoffice,pipe=xyz_Office,path=/opt/openoffice11/program;urp;StarOffice.ServiceManager";
   * <li>"uno:socket,host=localhost,port=8100;urp;StarOffice.ServiceManager";
   * </ul>
   * 
   * @param url
   *          this is UNO URL which describes the type of a connection
   * @param prefix
   *          prefix to be used
   * 
   * @exception java.net.MalformedURLException
   *              when inappropreate URL was provided
   * 
   * @author OpenOffice.org
   */
  private void parseUnoUrlWithOfficePath(String url, String prefix)
      throws java.net.MalformedURLException {
    // Extruct parameters.
    int index = url.indexOf(";urp;StarOffice.NamingService"); //$NON-NLS-1$
    if(index < 0)
      throw new java.net.MalformedURLException("Invalid UNO connection URL."); //$NON-NLS-1$
    String params = url.substring(prefix.length(), index + 1);

    // Parse parameters.
    String name = null;
    String path = null;
    String pipe = null;
    char ch;
    int state = 0;
    StringBuffer buffer = new StringBuffer();
    for(index = 0; index < params.length(); index += 1) {
      ch = params.charAt(index);
      switch(state) {
        case 0: // initial state
          switch(ch) {
            case ',':
              buffer.delete(0, buffer.length());
              state = 1;
              break;

            case ';':
              state = 7;
              break;

            default:
              buffer.delete(0, buffer.length());
              buffer.append(ch);
              state = 1;
              break;
          }
          break;

        case 1: // parameter name
          switch(ch) {
            case ' ':
            case '=':
              name = buffer.toString();
              state = (ch == ' ') ? 2 : 3;
              break;

            case ',':
            case ';':
              state = -6; // error: invalid name
              break;

            default:
              buffer.append(ch);
              break;
          }
          break;

        case 2: // equal between the name and the value
          switch(ch) {
            case '=':
              state = 3;
              break;

            case ' ':
              break;

            default:
              state = -1; // error: missing '='
              break;
          }
          break;

        case 3: // value leading spaces
          switch(ch) {
            case ' ':
              break;

            default:
              buffer.delete(0, buffer.length());
              buffer.append(ch);
              state = 4;
              break;
          }
          break;

        case 4: // value
          switch(ch) {
            case ' ':
            case ',':
            case ';':
              index -= 1; // put back the last read character
              state = 5;
              if(name.equals("path")) { //$NON-NLS-1$
                if(path == null)
                  path = buffer.toString();
                else
                  state = -3; // error: more then one 'path'
              }
              else if(name.equals("pipe")) { //$NON-NLS-1$
                if(pipe == null)
                  pipe = buffer.toString();
                else
                  state = -4; // error: more then one 'pipe'
              }
              else
                state = -2; // error: unknown parameter
              buffer.delete(0, buffer.length());
              break;

            default:
              buffer.append(ch);
              break;
          }
          break;

        case 5: // a delimeter after the value
          switch(ch) {
            case ' ':
              break;

            case ',':
              state = 6;
              break;

            case ';':
              state = 7;
              break;

            default:
              state = -5; // error: ' ' inside the value
              break;
          }
          break;

        case 6: // leading spaces before next parameter name
          switch(ch) {
            case ' ':
              break;

            default:
              buffer.delete(0, buffer.length());
              buffer.append(ch);
              state = 1;
              break;
          }
          break;

        default:
          throw new java.net.MalformedURLException(
              "Invalid UNO connection URL."); //$NON-NLS-1$
      }
    }
    if(state != 7)
      throw new java.net.MalformedURLException("Invalid UNO connection URL."); //$NON-NLS-1$

    // Set up the connection parameters.
    if(path != null)
      mProgramPath = path;
    if(pipe != null)
      this.pipe = pipe;
  }
  //----------------------------------------------------------------------------
  /**
   * Replaces each substring aSearch in aString by aReplace.
   * 
   * StringBuffer.replaceAll() is not avaialable in Java 1.3.x.
   * 
   * @param aString string to be used
   * @param aSearch search string to be used
   * @param aReplace replacement to be used
   * 
   * @return converted string
   * 
   * @author OpenOffice.org
   */
  private String replaceAll(String aString, String aSearch, String aReplace) {
    StringBuffer aBuffer = new StringBuffer(aString);

    int nPos = aString.length();
    int nOfs = aSearch.length();

    while((nPos = aString.lastIndexOf(aSearch, nPos - 1)) > -1)
      aBuffer.replace(nPos, nPos + nOfs, aReplace);

    return aBuffer.toString();
  }
  //----------------------------------------------------------------------------
  /**
   * Creates a unique pipe name.
   * 
   * @return unique pipe name
   * 
   * @author OpenOffice.org
   */
  private String getPipeName() {
    // turn user name into a URL and file system safe name (% chars will not
    // work)
    String aPipeName = System.getProperty("user.name") + OFFICE_ID_SUFFIX; //$NON-NLS-1$
    aPipeName = replaceAll(aPipeName, "_", "%B7"); //$NON-NLS-1$ //$NON-NLS-2$
    return replaceAll(replaceAll(java.net.URLEncoder.encode(aPipeName),
        "\\+", "%20"), "%", "_"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  }
  //----------------------------------------------------------------------------
  /**
   * Loads the necessary native libraries. 
   * 
   * @author Andreas Bröcker
   * @date 20.03.2006
   */
  private void loadNativeLibraries() {
    String officeHomePath = System.getProperty("office.home"); //$NON-NLS-1$
    if(System.getProperty("os.name").startsWith("Windows")) { //$NON-NLS-1$ //$NON-NLS-2$
      try {
        if(!msvcrLoaded) {
          if(officeHomePath == null)
            NativeLibraryLoader.loadLibrary(LocalOfficeConnection.class
                .getClassLoader(), "msvcr70"); //$NON-NLS-1$
          else
            System.load(officeHomePath + File.separator + "program" //$NON-NLS-1$
                + File.separator + System.mapLibraryName("msvcr70")); //$NON-NLS-1$
          msvcrLoaded = true;
        }
      }
      catch(Throwable throwable) {
        //System.err.println("cannot find msvcr70"); //$NON-NLS-1$
      }

      try {
        if(!msvcrLoaded) {
          if(officeHomePath == null)
            NativeLibraryLoader.loadLibrary(LocalOfficeConnection.class
                .getClassLoader(), "msvcr71"); //$NON-NLS-1$
          else
            System.load(officeHomePath + File.separator + "program" //$NON-NLS-1$
                + File.separator + System.mapLibraryName("msvcr71")); //$NON-NLS-1$
          msvcrLoaded = true;
        }
      }
      catch(Throwable throwable) {
        //System.err.println("cannot find msvcr71"); //$NON-NLS-1$
      }

      try {
        if(!uwinapiLoaded) {
          if(officeHomePath == null)
            NativeLibraryLoader.loadLibrary(LocalOfficeConnection.class
                .getClassLoader(), "uwinapi"); //$NON-NLS-1$
          else
            System.load(officeHomePath + File.separator + "program" //$NON-NLS-1$
                + File.separator + System.mapLibraryName("uwinapi")); //$NON-NLS-1$
          uwinapiLoaded = true;
        }
      }
      catch(Throwable throwable) {
        //System.err.println("cannot find uwinapi"); //$NON-NLS-1$
      }

      try {
        if(!jawtLoaded) {
          if(officeHomePath == null)
            NativeLibraryLoader.loadLibrary(LocalOfficeConnection.class
                .getClassLoader(), "jawt"); //$NON-NLS-1$
          else
            System.load(officeHomePath + File.separator + "program" //$NON-NLS-1$
                + File.separator + System.mapLibraryName("jawt")); //$NON-NLS-1$
          jawtLoaded = true;
        }
      }
      catch(Throwable throwable) {
        //System.err.println("cannot find jawt"); //$NON-NLS-1$
      }
    }

    try {
      if(!officebeanLoaded) {
        if(officeHomePath == null)
          NativeLibraryLoader.loadLibrary(LocalOfficeConnection.class
              .getClassLoader(), OFFICE_LIB_NAME);
        else
          System.load(officeHomePath + File.separator
              + "program" + File.separator //$NON-NLS-1$
              + System.mapLibraryName(OFFICE_LIB_NAME));
        officebeanLoaded = true;
      }
    }
    catch(Throwable throwable) {
      //do not consume
    }
  }
  //----------------------------------------------------------------------------

}