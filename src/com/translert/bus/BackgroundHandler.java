package com.translert.bus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.json.JSONException;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class BackgroundHandler extends Handler {
	
	static Handler uiHandler;
	static HTTPtoJSONString httpToString = new HTTPtoJSONString();
	static OneMapJSONparser oneMapJSONparser = new OneMapJSONparser();
	Handler backgroundHandler= this;
	static GPSTracker gps;
	static Message returnMsg;
	
	static private final String token = "qo/s2TnSUmfLz+32CvLC4RMVkzEFYjxqyti1KhByvEacEdMWBpCuSSQ+IFRT84QjGPBCuz/cBom8PfSm3GjEsGc8PkdEEOEr";
	
	static private final String routingStart = "http://www.onemap.sg/publictransportation/service1.svc/routesolns?token=";
	
	static private final String routingEnd = "&startstop=&endstop=&walkdist=300&mode=bus&routeopt=cheapest&retgeo=false&maxsolns=1&callback=";
	
	static private final String searchStart = "http://www.onemap.sg/API/services.svc/basicSearch?token=";
	
	static private final String BUS_STOP = " (BUS STOP)";
	
	static private final int CONVERT_LATLNG_TO_SVY21 = 1;
	static private final int GET_POSITION = 2;
	static private final int RETURN_POSITION = 3;
	static private final int GET_ROUTE_BETWEEN_2_LOCATIONS = 4;
	static private final int RETURN_ROUTE_BETWEEN_2_LOCATIONS = 5;
	static private final int GET_GPS_PERIODICALLY = 6;
	static private final int RETURN_GPS_PERIODICALLY = 7;
	static private final int GET_ROUTE_FROM_CURRENT_GPS = 8;
	static private final int RETURN_ROUTE_FROM_CURRENT_GPS = 9;

	public BackgroundHandler() {
		// TODO Auto-generated constructor stub
	}

	public BackgroundHandler(Callback callback) {
		super(callback);
		// TODO Auto-generated constructor stub
	}

	public BackgroundHandler(Looper looper) {
		super(looper);
		// TODO Auto-generated constructor stub
	}

	public BackgroundHandler(Looper looper, Callback callback) {
		super(looper, callback);
		// TODO Auto-generated constructor stub
	}
	
	public BackgroundHandler(Looper looper, Handler uiHandler, GPSTracker gps) {
		super(looper);
		BackgroundHandler.uiHandler = uiHandler;
		BackgroundHandler.gps = gps;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void handleMessage (Message msg) {
		
		switch (msg.what) {
		
		case GET_GPS_PERIODICALLY:
			
			this.post(getGPSPeriodically);
			break;
			
		
		case GET_POSITION:
			
			SGGPosition queriedPosition = getPosition((String)msg.obj);
			
			returnMsg = uiHandler.obtainMessage(RETURN_POSITION, queriedPosition);
			uiHandler.sendMessage(returnMsg);
			break;
			
			
		case GET_ROUTE_BETWEEN_2_LOCATIONS:
			String[] locations = (String[]) msg.obj;
			SGGPosition origin1 = getPosition(locations[0]);
			SGGPosition destination1 = getPosition(locations[1]);
			BusRoute busRoute1 = getRoute(origin1, destination1);
			
			returnMsg = uiHandler.obtainMessage(RETURN_ROUTE_BETWEEN_2_LOCATIONS, busRoute1);
			uiHandler.sendMessage(returnMsg);
			break;
			
			
		case GET_ROUTE_FROM_CURRENT_GPS:
			
			SGGPosition origin2 = getGPS();
			SGGPosition destination2 = getPosition ( (String)  msg.obj);
			BusRoute busRoute2 = getRoute(origin2, destination2);
			
			returnMsg = uiHandler.obtainMessage(RETURN_ROUTE_FROM_CURRENT_GPS, busRoute2);
			uiHandler.sendMessage(returnMsg);
			break;
			
			
		}
		
		
			
		
	}
	
	//FUNCTIONS AND STUFF
	
	private Runnable getGPSPeriodically = new Runnable() {
		
		public void run() {
			
			SGGPosition currentPosition = getGPS();
			
			returnMsg = uiHandler.obtainMessage(RETURN_GPS_PERIODICALLY, currentPosition);
        	uiHandler.sendMessage(returnMsg);
			
			backgroundHandler.postDelayed(getGPSPeriodically, 5000);
		
		}
	
	};
	

	public SGGPosition getGPS() {
		
		SGGPosition currentPosition = null; 
		
		if (gps.canGetLocation()) {
			
        	currentPosition = 
        			new SGGPosition (gps.getLatitude(), gps.getLongitude(), "Current position", CONVERT_LATLNG_TO_SVY21);
        	
        } else {
        	
        	gps.showSettingsAlert();
        	
        }
		
		return currentPosition;
		
	}
	
	public SGGPosition getPosition (String name) {
    	
		SGGPosition position = null;
		
    	try {
			URI link = new URI(searchStart + token + "&searchVal=" + URLEncoder.encode(name, "UTF-8"));
			String results = httpToString.doit(link);
	    	position = oneMapJSONparser.geocode(results);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			
		} catch (JSONException e) {
			
		}
    	
    	return position;
    	
    }
    
	
    public BusRoute getRoute (SGGPosition origin, SGGPosition destination) {
    	
    	
    	BusRoute busRoute = null;
		
    	try {
			String link = routingStart + token
	    			+ String.format ("&sl=%.4f,%.4f", origin.x, origin.y)
	    			+ String.format ("&el=%.4f,%.4f", destination.x, destination.y)
	    			+ routingEnd;
	    	
	    	URI linkURI = new URI(link);
	    	String results = httpToString.doit(linkURI);
			busRoute = oneMapJSONparser.getRoute(results, origin, destination);
			
			for (int i = 0; i< busRoute.route.length; i ++) {
	    		
	    		busRoute.route[i].startPosition = getPosition (busRoute.route[i].startCode + BUS_STOP);
	    		busRoute.route[i].endPosition = getPosition (busRoute.route[i].endCode + BUS_STOP);
	    	
	    	}
			
    	} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			
		} catch (JSONException e) {
			
		}
    	
    	return busRoute;
    	
    }
    
    
//  private String getPositionDebug (String name) 
//	throws URISyntaxException, ClientProtocolException, IOException, InterruptedException, ExecutionException, JSONException {
//
//String linkString = searchStart + token + "&searchVal=" + URLEncoder.encode(name, "UTF-8");
//return linkString;
//
//} 
    
//    private String getRouteDebug (SGGPosition positionA, SGGPosition positionB)
//    		throws URISyntaxException, InterruptedException, ExecutionException, ClientProtocolException, JSONException, IOException {
//    	
//    	String link = routingStart + token
//    			+ String.format ("&sl=%.4f,%.4f", positionA.x, positionA.y)
//    			+ String.format ("&el=%.4f,%.4f", positionB.x, positionB.y)
//    			+ routingEnd;
//    	URI linkURI = new URI(link);
//    	String results = httpToString.doit(linkURI);
////    	ArrayList<BusStep> busRoute = oneMapJSONparser.getRoute(results);
//    	return "\n\n" + link + "\n\n" + results;
//    	
//    }
	
	

}
