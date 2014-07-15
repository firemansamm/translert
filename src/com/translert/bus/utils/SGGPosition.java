package com.translert.bus.utils;

//import com.ibm.util.CoordinateConversion;

import android.location.Location;
//import net.qxcg.svy21.*;


public class SGGPosition{
	
//	public double easting;
//	public double northing;
	public double latitude;
	public double longitude;
	public String title;
	/*
	public SGGPosition (double x, double y, String title) {
		this.easting = x;
		this.northing = y;
		this.title = title;
		
	}
*/
	public SGGPosition(Location location, String title) {
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.title = title;
	}
	
	public SGGPosition(double latitude, double longitude, String title, int conversion) {
		
		if (conversion == C.CONVERT_LATLNG_TO_SVY21) {
			
//			SVY21Coordinate result = SVY21.computeSVY21(latitude, longitude);
			
			this.latitude = latitude;
			this.longitude = longitude;
			
//			this.easting = result.getEasting();
//			this.northing = result.getNorthing();
			this.title = title;
		}
		
	}
	
	public double getDistance(SGGPosition other) {
		float[] results = new float[1];
		Location.distanceBetween(this.latitude, this.longitude, other.latitude, other.longitude, results);
		return (double) results[0];
	}
	
	public float getDistance(Location location) {
		float[] results = new float[1];
		Location.distanceBetween(this.latitude, this.longitude, location.getLatitude(), location.getLongitude(), results);
		return results[0];
	}
	
	public String format() {
		return title + " is at " + String.format("%.4f, %.4f",this.latitude ,this.longitude);
	}

}
