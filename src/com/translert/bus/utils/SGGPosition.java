package com.translert.bus.utils;

//import com.ibm.util.CoordinateConversion;

import android.location.Location;
import android.util.Log;
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
		try {
			float[] results = new float[1];
			Location.distanceBetween(this.latitude, this.longitude, other.latitude, other.longitude, results);
			Log.d("translert", "LatLng");
			return (double) results[0];
		} catch (Exception e) {
			return 0;
//			return Math.sqrt(Math.pow(other.easting - easting, 2) + Math.pow (other.northing - northing, 2));
		}
	}
	
	public String format() {
		return title + " is at " + String.format("%.4f, %.4f",this.latitude ,this.longitude);
	}

}
