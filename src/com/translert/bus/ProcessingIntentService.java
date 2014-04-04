package com.translert.bus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.json.JSONException;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.translert.activity.C;


public class ProcessingIntentService extends IntentService {
	
	
	
	static Handler handler;
	HTTPtoJSONString httpToString = new HTTPtoJSONString();
	OneMapJSONparser oneMapJSONparser = new OneMapJSONparser();
	GPSTracker gps;
	static Message returnMsg;
	
	static private final String token = "qo/s2TnSUmfLz+32CvLC4RMVkzEFYjxqyti1KhByvEacEdMWBpCuSSQ+IFRT84QjGPBCuz/cBom8PfSm3GjEsGc8PkdEEOEr";
	
	static private final String routingStart = "http://www.onemap.sg/publictransportation/service1.svc/routesolns?token=";
	
	static private final String routingEnd = "&startstop=&endstop=&walkdist=300&mode=bus&routeopt=cheapest&retgeo=false&maxsolns=1&callback=";
	
	static private final String searchStart = "http://www.onemap.sg/API/services.svc/basicSearch?token=";
	
	static private final String BUS_STOP = " (BUS STOP)";
	
	public ProcessingIntentService () {
		super("processingIntentService");
	}
	
	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		gps = new GPSTracker(this);
		Log.d("translert", "IntentService received some intent");
		int messageFlag = intent.getIntExtra("messageFlag", 0);
		
		switch (messageFlag) {
		
		case 0:
			break;
		case C.GET_DISTANCE_INIT:
			
			SGGPosition origin2 = getGPS();
			String busDestination = intent.getStringExtra("busDestination");
			SGGPosition destination2 = getPosition (busDestination);
			double distance = origin2.getDistance(destination2);
			
			Intent outputIntent = new Intent (this, com.translert.activity.BusProgressActivity.class);
			outputIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			outputIntent.putExtra("distance", formatDistanceToString(distance));
			startActivity(outputIntent);
			Log.d("translert", "IntentService initiated progress activity");
			
			break;
		
		}
			
		
		
	}
	
	private String formatDistanceToString(double distance) {
		if (distance > 1000) {
			return String.format("%.1f km", distance/1000);
		} else {
			return String.format("%s m", distance);
		}
	}
	
	
	public SGGPosition getGPS() {
		
		SGGPosition currentPosition = null; 
		
		if (gps.canGetLocation()) {
			
        	currentPosition = 
        			new SGGPosition (gps.getLatitude(), gps.getLongitude(), "Current position", C.CONVERT_LATLNG_TO_SVY21);
        	
        } else {
        	
        	//gps.showSettingsAlert();
        	Log.d("translert", "cannot get GPS");
        	currentPosition = new SGGPosition(0,0, "empty");
        	
        }
		
		return currentPosition;
		
	}
	
	public SGGPosition getPosition (String name) {
    	
		SGGPosition position = null;
		
    	try {
    		String linkString = searchStart + token + "&searchVal=" + URLEncoder.encode(name, "UTF-8");
    		Log.d("translert", linkString);
			URI link = new URI(linkString);
			String results = httpToString.doit(link);
			Log.d("translert", results);
	    	position = oneMapJSONparser.geocode(results);
	    	Log.d("translert", position.format());
		
    	} catch (URISyntaxException e) {
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
		} catch (IOException e) {
		} catch (JSONException e) {
		}
    	
    	return busRoute;
    	
    }

}
