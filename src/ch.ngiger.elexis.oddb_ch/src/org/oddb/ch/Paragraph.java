package org.oddb.ch;

import java.util.Map;

public class Paragraph {
	Format[] formats;//   (Array (Text::Format))  -> Formatdefinitionen
	String text; //              (String)                -> unformatierter Text
	Boolean preformatted; //      (Boolean)               -> Wenn ja, sollte whitespace 1:1 übernommen werden.
	public Format[] getFormats(){
		return formats;
	}
	public void setFormats(Format[] formats){
		this.formats = formats;
	}
	public String getText(){
		return text;
	}
	public void setText(String text){
		this.text = text;
	}
	public Boolean getPreformatted(){
		return preformatted;
	}
	public void setPreformatted(Boolean preformatted){
		this.preformatted = preformatted;
	}


}
