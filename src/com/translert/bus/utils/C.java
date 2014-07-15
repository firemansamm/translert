package com.translert.bus.utils;

import android.graphics.Typeface;

public final class C {
	public static final int CONVERT_LATLNG_TO_SVY21 = 21;
	public static final int RETURN_TIMER_RUNNING = 11;
	public static final int RETURN_TIMER_OVER = 22;
	public static final int RETURN_TIMER_START = 33;
	public static final int GET_DISTANCE_INIT = 101;
	public static final int GET_DISTANCE = 103;
	public static final int DISTANCE_MESSAGE = 102;
	public static final int ALARM_MESSAGE = 404;
	public static final long DELAY_LONG_SIMULATOR = 2 * 1000;
	public static final long DELAY_SHORT_SIMULATOR = 333;
	public static final long DELAY_NORMAL = 3 * 60 * 1000/2;
	public static final long DELAY_OVERDRIVE = 2 * 1000;
	public static final float MIN_DISTANCE = 0;
	public static final double OVERDRIVE_THRESHOLD = 2000;
	public static final double BUS_SPEED = 50/3;
	
//	public static final String token = "qo/s2TnSUmfLz+32CvLC4RMVkzEFYjxqyti1KhByvEacEdMWBpCuSSQ+IFRT84QjGPBCuz/cBom8PfSm3GjEsGc8PkdEEOEr";
//	public static final String routingStart = "http://www.onemap.sg/publictransportation/service1.svc/routesolns?token=";
//	public static final String routingEnd = "&startstop=&endstop=&walkdist=300&mode=bus&routeopt=cheapest&retgeo=false&maxsolns=1&callback=";
//	public static final String searchStart = "http://www.onemap.sg/API/services.svc/basicSearch?token=";
//	public static final String sdSearchStart = "http://www.streetdirectory.com/api/?mode=search&act=all&output=json&start=0&limit=20&country=sg&profile=template_1&q=";
//	public static final String BUS_STOP = " (BUS STOP)";
	
	public static Typeface headingFont;
	public static Typeface bodyFont;
	public static Typeface numFont;
	
	public static final long[] vibratingPattern = {0, 500, 500};
	

}
