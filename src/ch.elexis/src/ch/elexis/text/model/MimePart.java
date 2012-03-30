/*******************************************************************************
 * Copyright (c) 2006-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *  $Id: MimePart.java 5321 2009-05-28 12:06:28Z rgw_ch $
 *******************************************************************************/
package ch.elexis.text.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public abstract class MimePart {
	private String mimetype;
	protected Object data;
	
	public MimePart(String type, Object data){
		mimetype = type;
		this.data = data;
	}
	
	public String getMimeType(){
		return mimetype;
	}
	
	public abstract byte[] getData();
	
	public static class Binary extends MimePart {
		public Binary(String type, byte[] data){
			super(type, data);
		}
		
		@Override
		public byte[] getData(){
			return (byte[]) data;
		}
		
	}
	
	public static class Img extends MimePart {
		public Img(String type, Image image){
			super(type, image.getImageData());
		}
		
		public byte[] getData(){
			ImageData imd = (ImageData) data;
			return imd.data;
		}
	}
	
}
