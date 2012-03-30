/*******************************************************************************
 * Copyright (c) 2009-2010, G. Weirich and medelexis AG
 * All rights reserved.
 * $Id$
 *******************************************************************************/

package ch.elexis.labortarif2009.ui;

import org.eclipse.swt.SWT;

import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListenerImpl;
import ch.elexis.data.PersistentObject;
import ch.elexis.labortarif2009.data.Labor2009Tarif;
import ch.elexis.util.viewers.CommonViewer;
import ch.elexis.util.viewers.DefaultLabelProvider;
import ch.elexis.util.viewers.SimpleWidgetProvider;
import ch.elexis.util.viewers.ViewerConfigurer;
import ch.elexis.views.codesystems.CodeSelectorFactory;

public class Labor2009Selector extends CodeSelectorFactory {
	CommonViewer cv;
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		this.cv = cv;
		ViewerConfigurer vc =
			new ViewerConfigurer(new Labor2009ContentProvider(), new DefaultLabelProvider(),
			new Labor2009ControlFieldProvider(cv), new ViewerConfigurer.DefaultButtonProvider(),
			new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
		
		ElexisEventDispatcher.getInstance().addListeners(
			new UpdateEventListener(cv, Labor2009Tarif.class, ElexisEvent.EVENT_RELOAD));
		
		return vc;
	}
	
	@Override
	public void dispose(){
		cv.dispose();
	}
	
	@Override
	public String getCodeSystemName(){
		return Labor2009Tarif.CODESYSTEM_NAME;
	}
	
	@Override
	public Class<? extends PersistentObject> getElementClass(){
		return Labor2009Tarif.class;
	}
	
	private class UpdateEventListener extends ElexisEventListenerImpl {
		
		CommonViewer viewer;
		
		UpdateEventListener(CommonViewer viewer, final Class<?> clazz, int mode){
			super(clazz, mode);
			this.viewer = viewer;
		}
		
		@Override
		public void runInUi(ElexisEvent ev){
			viewer.notify(CommonViewer.Message.update);
		}
	}
}
