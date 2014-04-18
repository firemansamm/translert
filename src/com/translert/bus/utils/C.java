package com.translert.bus;

public final class C {
	public static final int CONVERT_LATLNG_TO_SVY21 = 21;
	public static final int RETURN_TIMER_RUNNING = 11;
	public static final int RETURN_TIMER_OVER = 22;
	public static final int RETURN_TIMER_START = 33;
	public static final int GET_DISTANCE_INIT = 101;
	public static final int GET_DISTANCE = 103;
	public static final int RETURN_DISTANCE = 102;
	public static final int RETURN_DISTANCE_REACHED = 404;
	public static final long DELAY_LONG = 5 * 1000;
	public static final long DELAY_SHORT = 2 * 1000;
	
	
	public static final String token = "qo/s2TnSUmfLz+32CvLC4RMVkzEFYjxqyti1KhByvEacEdMWBpCuSSQ+IFRT84QjGPBCuz/cBom8PfSm3GjEsGc8PkdEEOEr";
	public static final String routingStart = "http://www.onemap.sg/publictransportation/service1.svc/routesolns?token=";
	public static final String routingEnd = "&startstop=&endstop=&walkdist=300&mode=bus&routeopt=cheapest&retgeo=false&maxsolns=1&callback=";
	public static final String searchStart = "http://www.onemap.sg/API/services.svc/basicSearch?token=";
	public static final String sdSearchStart = "http://www.streetdirectory.com/api/?mode=search&act=all&output=json&start=0&limit=20&country=sg&profile=template_1&q=";
	public static final String BUS_STOP = " (BUS STOP)";

}
