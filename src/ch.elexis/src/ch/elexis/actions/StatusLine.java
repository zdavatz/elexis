// $Id: StatusLine.java 23 2006-03-24 15:36:01Z rgw_ch $
/*
 * Created on 15.09.2005
 */
package ch.elexis.actions;

import org.eclipse.ui.IViewSite;

public class StatusLine {
	
	public static void setText(IViewSite site, String text){
		site.getActionBars().getStatusLineManager().setMessage(text);
	}
	
}
