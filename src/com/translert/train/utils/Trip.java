package com.translert.train.utils;

import java.io.Serializable;
import java.util.Date;

public class Trip implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3342303852376353336L;
	public Date date;
	public String source, destination;
	public int minutes, type, xfc;
	
	public Trip(long d, String s, String dest, int tt, int ty, int transfer){
		date = new Date(d);
		source = s;
		destination = dest;
		minutes = tt;
		type = ty;
		xfc = transfer;
	}
}
