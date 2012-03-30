// $Id: GLZInputStream.java 23 2006-03-24 15:36:01Z rgw_ch $
package ch.rgw.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Simple und nicht besonders effiziente Implementation eines GLZ-InputStreams Macht einfach eine
 * Zwischenspeicherung der decodierten Daten.
 * 
 * @author Gerry
 * 
 */
public class GLZInputStream extends InputStream {
	byte[] decomp;
	int pointer;
	
	public GLZInputStream(InputStream in) throws Exception{
		ByteArrayOutputStream dcs = new ByteArrayOutputStream();
		GLZ glz = new GLZ();
		glz.expand(in, dcs);
		dcs.flush();
		decomp = dcs.toByteArray();
		pointer = 0;
	}
	
	@Override
	public int read() throws IOException{
		if (pointer == decomp.length) {
			return -1;
		}
		return decomp[pointer++];
	}
	
}
