package ch.rgw.tools;

import java.util.regex.Pattern;

public class RegexpFilter implements IFilter {
	Pattern pattern;
	
	public RegexpFilter(String regexp){
		pattern = Pattern.compile(regexp);
	}
	
	public boolean select(Object element){
		String m = element.toString();
		return pattern.matcher(m).matches();
	}
	
}