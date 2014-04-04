package com.translert.bus;

//import com.ibm.util.CoordinateConversion;

import net.qxcg.svy21.*;
import com.translert.activity.C;

public class SGGPosition {
	
	public double x;
	public double y;
	public String title;
	static private final int CONVERT_LATLNG_TO_SVY21 = 1;
	
	public SGGPosition (double x, double y, String title) {
		
		this.x = x;
		this.y = y;
		this.title = title;
		
	}

	public SGGPosition(double x, double y, String title, int conversion) {
		
		if (conversion == C.CONVERT_LATLNG_TO_SVY21) {
			
			SVY21Coordinate result = SVY21.computeSVY21(x, y);
			
			this.x = result.getEasting();
			this.y = result.getNorthing();
			this.title = title;
			
		} else {
			this.x = x;
			this.y = y;
			this.title = title;
					
		}
		
	}
	
	public double getDistance(SGGPosition other) {
		
		return Math.sqrt(Math.pow(other.x - x, 2) + Math.pow (other.y - y, 2));
		
	}
	
	public String format() {
		
		return title + " is at " + String.format("%.4f, %.4f",x ,y);
		
	}

}
