package ch.elexis.eigenleistung;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;

import ch.elexis.data.Eigenleistung;
import ch.elexis.data.PersistentObject;
import ch.elexis.views.IDetailDisplay;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

public class EigenleistungDetailDisplay implements IDetailDisplay {
	private Text textCode;
	private Text textBezeichnung;
	private Text textEKP;
	private Text textVKP;
	private Text textZeit;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public Composite createDisplay(Composite parent, IViewSite site){
		Composite ret = new Composite(parent, SWT.None);
		ret.setLayout(new GridLayout(2, false));
		
		Label lblCode = new Label(ret, SWT.NONE);
		lblCode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCode.setText("Code");
		
		textCode = new Text(ret, SWT.BORDER);
		textCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textCode.setTextLimit(20);
		
		Label lblBezeichnung = new Label(ret, SWT.NONE);
		lblBezeichnung.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBezeichnung.setText("Bezeichnung");
		
		textBezeichnung = new Text(ret, SWT.BORDER | SWT.MULTI);
		textBezeichnung.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textBezeichnung.setTextLimit(80);
		
		Label lblEKP = new Label(ret, SWT.NONE);
		lblEKP.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEKP.setText("Einkaufspreis");
		
		textEKP = new Text(ret, SWT.BORDER);
		textEKP.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textEKP.setTextLimit(6);
		
		Label lblVKP = new Label(ret, SWT.NONE);
		lblVKP.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblVKP.setText("Verkaufspreis");
		
		textVKP = new Text(ret, SWT.BORDER);
		textVKP.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textVKP.setTextLimit(6);
		
		Label lblZeit = new Label(ret, SWT.NONE);
		lblZeit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblZeit.setText("Zeitbedarf");
		
		textZeit = new Text(ret, SWT.BORDER);
		textZeit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textZeit.setTextLimit(4);
		return null;
	}
	
	@Override
	public Class<? extends PersistentObject> getElementClass(){
		return Eigenleistung.class;
	}
	
	@Override
	public void display(Object obj){
		Eigenleistung e = (Eigenleistung) obj;
		textCode.setText(e.get(Eigenleistung.CODE));
		textBezeichnung.setText(e.get(Eigenleistung.BEZEICHNUNG));
		textEKP.setText(e.get(Eigenleistung.EK_PREIS));
		textVKP.setText(e.get(Eigenleistung.VK_PREIS));
		textZeit.setText(e.get(Eigenleistung.TIME));	
	}
	
	@Override
	public String getTitle(){
		return Eigenleistung.CODESYSTEM_NAME;
	}
	
}
