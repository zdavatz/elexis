package ch.elexis.text;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.ElexisException;
import ch.elexis.actions.ElexisEventListener;
import ch.elexis.text.EnhancedTextField2.UserChangeListener;
import ch.elexis.text.IRangeHandler.OUTPUT;
import ch.elexis.text.model.SSDRange;
import ch.elexis.util.IKonsExtension;
import ch.rgw.tools.GenericRange;

public abstract class AbstractRichTextDisplay extends Composite implements IRichTextDisplay {
	
	protected HashMap<String, IRangeHandler> renderers = new HashMap<String, IRangeHandler>();
	protected IAction copyAction, cutAction, pasteAction;
	
	public AbstractRichTextDisplay(Composite parent){
		super(parent, SWT.NONE);
	}
	
	@Override
	public void addXrefHandler(String id, IKonsExtension ike){
		renderers.put(id, adapt(ike));
	}
	
	@Override
	public void setXrefHandlers(Map<String, IKonsExtension> handlers){
		// we don't need xrefhandlers but some clients send them, so convert to renderers
		for (String key : handlers.keySet()) {
			renderers.put(key, adapt(handlers.get(key)));
		}
	}
	
	@Override
	public void insertXRef(int pos, String textToDisplay, String providerId, String itemID){
		SSDRange range = new SSDRange(pos, textToDisplay.length(), providerId, itemID);
		insertRange(range);
	}
	
	@Override
	public abstract void insertRange(SSDRange range);
	
	@Override
	public void addDropReceiver(Class<?> clazz, IKonsExtension konsExtension){
	// Not needed anymore. A DropReceiver is just a Handler
	
	}
	
	@Override
	public abstract String getWordUnderCursor();
	
	@Override
	public abstract String getContentsAsXML();
	
	@Override
	public abstract String getContentsPlaintext();
	
	@Override
	public abstract GenericRange getSelectedRange();
	
	/**
	 * Adapter for existing code. DO NOT use this in new code. Purpose: convert an IKonsExtension to
	 * an IRangeRenderer
	 * 
	 * @param ik
	 *            an iKonsExtention
	 * @return an IRangeRenderer with the same properties as the input
	 * @deprecated only for compatibility reasons
	 */
	IRangeHandler adapt(final IKonsExtension ik){
		return new IRangeHandler() {
			
			@Override
			public boolean canRender(String rangeType, OUTPUT outputType){
				return outputType.equals(OUTPUT.STYLED_TEXT);
			}
			
			@Override
			public Object doRender(SSDRange range, OUTPUT outputType, IRichTextDisplay display)
				throws ElexisException{
				StyleRange sr = new StyleRange();
				sr.start = range.getPosition();
				sr.length = range.getLength();
				ik.doLayout(sr, range.getHint(), range.getID());
				return sr;
			}
			
			@Override
			public IAction[] getActions(String rangeType){
				return ik.getActions();
			}
			
			@Override
			public boolean onSelection(SSDRange range){
				return ik.doXRef(range.getContents(), range.getID());
				
			}
			
			@Override
			public void inserted(SSDRange range, Object context){
				ik.insert(range, 0);
			}
			
			@Override
			public void removed(SSDRange range, Object context){
				ik.removeXRef(range.getContents(), range.getID());
			}
		};
	}
	
}
