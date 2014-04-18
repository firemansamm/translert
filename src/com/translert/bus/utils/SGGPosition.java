package com.translert.bus;

//import com.ibm.util.CoordinateConversion;

import net.qxcg.svy21.*;

public class SGGPosition {
	
	public double easting;
	public double northing;
	public String title;
	
	public SGGPosition (double x, double y, String title) {
		
		this.easting = x;
		this.northing = y;
		this.title = title;
		
	}

	public SGGPosition(double latitude, double longitude, String title, int conversion) {
		
		if (conversion == C.CONVERT_LATLNG_TO_SVY21) {
			
			SVY21Coordinate result = SVY21.computeSVY21(latitude, longitude);
			
			this.easting = result.getEasting();
			this.northing = result.getNorthing();
			this.title = title;
		}
		
	}
	
	public double getDistance(SGGPosition other) {
		
		return Math.sqrt(Math.pow(other.easting - easting, 2) + Math.pow (other.northing - northing, 2));
		
	}
	
	public String format() {
		
		return title + " is at " + String.format("%.4f, %.4f",easting ,northing);
		
	}

}
