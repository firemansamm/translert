package com.translert;

import java.io.Serializable;
import java.util.Date;

public class Trip implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3342303852376353336L;
	Date date;
	String source, destination;
	int minutes, type, xfc;
	
	@SuppressWarnings("deprecation")
	public Trip(String d, String s, String dest, int tt, int ty, int transfer){
		date = new Date(Date.parse(d));
		source = s;
		destination = dest;
		minutes = tt;
		type = ty;
		xfc = transfer;
	}
}
