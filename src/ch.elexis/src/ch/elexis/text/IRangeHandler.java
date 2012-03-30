package ch.elexis.text;

import org.eclipse.jface.action.IAction;

import ch.elexis.ElexisException;
import ch.elexis.text.model.SSDRange;

/**
 * An IRangeHandler can add, display and act upon clicking on SSDRanges. It registers itself for one
 * ore more types of SSDRanges It can display supported SSDRanges on zero or more output types
 * 
 * An IRangeHandler is connected to the framework with the ExtensionPoint ch.elexis.ServiceRegistry
 * as a Service with the name "TextContentsExtension".
 * 
 * @author Gerry Weirich
 * 
 */
public interface IRangeHandler {
	public static enum OUTPUT {
		HTML, STYLED_TEXT, XCHANGE
	};
	
	/**
	 * Ask whether this IRangeRenderer can create output of a specific type
	 * 
	 * @param rangeType
	 *            the type of the Range to render
	 * @param outputType
	 *            teh type of desired output
	 * @return
	 */
	public boolean canRender(String rangeType, OUTPUT outputType);
	
	/**
	 * creaste a visual representation of a Range in a given type
	 * 
	 * @param range
	 *            the Range to render
	 * @param outputType
	 *            the desired type of output
	 * @return an output specific result.
	 */
	public Object doRender(SSDRange range, OUTPUT outputType, IRichTextDisplay display)
		throws ElexisException;
	
	/**
	 * return an array of actions that are possible on these ranges
	 * 
	 * @param rangeType
	 *            the type of ranges in question
	 * @return an array of all Actions that can be done on this range. can be null. The first Action
	 *         on index [0] will be executed if the user double-clicks on the range, the others will
	 *         be presented in a context menu if the user right-clicks on the range. (that is: the
	 *         user right-clicks or double clicks on some text between start and start+length of the
	 *         range. A click on a separate window created by a range will not be handled by the
	 *         framework)
	 */
	public IAction[] getActions(String rangeType);
	
	/**
	 * The user double-clicked or selected the specified range
	 * 
	 * @param range
	 *            the range that was selected by the user
	 * @return
	 */
	public boolean onSelection(SSDRange range);
	
	/**
	 * The user inserted a Range into a context (e.g. drag&drop)
	 * 
	 * @param range
	 *            the SSDRange that was inserted
	 * @param context
	 *            the context the range was inserted into
	 */
	public void inserted(SSDRange range, Object context);
	
	/**
	 * The user removed a Range from a context
	 * 
	 * @param range
	 *            the range that was removed
	 * @param context
	 *            the context it was removed from
	 */
	public void removed(SSDRange range, Object context);
	
}
