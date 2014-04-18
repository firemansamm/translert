package com.translert.bus;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.translert.bus.utils.BusRoute;
import com.translert.bus.utils.BusStep;
import com.translert.bus.utils.C;
import com.translert.bus.utils.GPSTracker;
import com.translert.bus.utils.SGGPosition;

public class F {
	
		
		public static SGGPosition getGPS() {
			
			GPSTracker gps = BusEnterNumberActivity.gps;
			
			SGGPosition currentPosition = null; 
			
			if (gps.canGetLocation()) {
				
	        	currentPosition = 
	        			new SGGPosition (gps.getLatitude(), gps.getLongitude(), "Current position", C.CONVERT_LATLNG_TO_SVY21);
	        	
	        } else {
	        	
	        	//gps.showSettingsAlert();
	        	Log.d("translert", "cannot get GPS");
	        	
	        }
			
			return currentPosition;
			
		}
		
		public static SGGPosition getBusStopPosition (Context context, String busDestination, String busNumber) {
			
			
			SGGPosition result = null;
			final String busStopNameLC = busDestination.toLowerCase();
			
			String filename = "bus-stops.json";
			String jsonStops = loadJSONFromAsset (context, filename);
			
			filename = "bus-services/" + busNumber + ".json";
			Log.d("translert", "getting list of bus stops from " + filename);
			String jsonRoute = loadJSONFromAsset (context, filename);
			Log.d("translert", jsonRoute);
			
			
			try {
				JSONArray  stops  = new JSONObject (jsonRoute).getJSONObject("1").getJSONArray("stops");
				ArrayList<Integer> stopsList = new ArrayList<Integer>();
				
				for (int i = 0; i < stops.length(); i++) {
					stopsList.add( stops.getInt(i) );
				}
				
				JSONArray allStops = new JSONArray (jsonStops);
				for (int i = 0; i < allStops.length(); i++) {
					
					JSONObject currentObject = allStops.getJSONObject(i);
					int currentObjectStopNo = currentObject.getInt("no");
					
					if ( stopsList.contains(currentObjectStopNo) ) {
						
						String name = currentObject.getString("name").toLowerCase();
						String no = String.format("%05d", currentObjectStopNo);
						Log.d("stopno to string", no);
						
						
						if (busStopNameLC.equals(name) || busStopNameLC.equals(no)) {
							Log.d("translert", "found the stop");
							Double lat = currentObject.getDouble("lat");
							Double lng = currentObject.getDouble("lng");
							Log.d("translert", lat + "," + lng);
							result = new SGGPosition(lat, lng, name, C.CONVERT_LATLNG_TO_SVY21);
							break;
						}
					}
				}
					
				
				
			} catch (JSONException e) {
				return null;
			}
			
			return result;
			
		}
		
		public static SGGPosition getSdBusStopPosition (String name) {
			
			SGGPosition position = null;
			
			
			try {
				String link = C.sdSearchStart + URLEncoder.encode(name, "UTF-8");
				Log.d("translert", link);
				URI linkUri = new URI (link);
				String results = uriToString(linkUri);
				Log.d("translert", results);
				position = sdBusStopGeocode(results);
				
			} catch (UnsupportedEncodingException e) {
			} catch (URISyntaxException e) {
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			} catch (JSONException e) {
			}
			
			if (position != null) {
				Log.d("translert", position.format());
				return position;
			} else {
				return null;
			}
			
		}
		
		public static SGGPosition getSdPosition (String name) {
			SGGPosition position = null;
			
			
			try {
				String link = C.sdSearchStart + URLEncoder.encode(name, "UTF-8");
				Log.d("translert", link);
				URI linkUri = new URI (link);
				String results = uriToString(linkUri);
				Log.d("translert", results);
				position = sdGeocode(results);
			} catch (UnsupportedEncodingException e) {
			} catch (URISyntaxException e) {
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			} catch (JSONException e) {
			}
			
			return position;
				
			
		}
		
		public static SGGPosition getPosition (String name) {
	    	
			SGGPosition position = null;
			
	    	try {
	    		String linkString = C.searchStart + C.token + "&searchVal=" + URLEncoder.encode(name, "UTF-8");
	    		Log.d("translert", linkString);
				URI link = new URI(linkString);
				String results = uriToString(link);
				Log.d("translert", results);
		    	position = geocode(results);
		    	Log.d("translert", position.format());
			
	    	} catch (URISyntaxException e) {
			} catch (IOException e) {
			} catch (JSONException e) {
			}
	    	
	    	return position;
	    	
	    }
	    
		
	    public static BusRoute getRoute (SGGPosition origin, SGGPosition destination) {
	    	
	    	
	    	BusRoute busRoute = null;
	    	String link = C.routingStart + C.token
	    			+ String.format ("&sl=%.4f,%.4f", origin.easting, origin.northing)
	    			+ String.format ("&el=%.4f,%.4f", destination.easting, destination.northing)
	    			+ C.routingEnd;
			
	    	try {
	    		
		    	URI linkURI = new URI(link);
		    	String results = uriToString(linkURI);
				busRoute = getRoute(results, origin, destination);
				
				for (int i = 0; i< busRoute.getLength(); i ++) {
					
					BusStep busStep = busRoute.getStep(i);
		    		busStep.setStartPosition( getPosition (busStep.getStartCode() + C.BUS_STOP)  ); 
		    		busStep.setEndPosition  ( getPosition (busStep.getEndCode()   + C.BUS_STOP)  );
		    	
		    	}
				
	    	} catch (URISyntaxException e) {
			} catch (IOException e) {
			} catch (JSONException e) {
			}
	    	
	    	return busRoute;
	    	
	    }
	    
	    //get string from link method
	    
	    private static String uriToString (URI uri) throws ClientProtocolException, IOException {
			
			String response = null;
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpGet httpGet = new HttpGet(uri);
	        HttpResponse httpResponse = httpClient.execute(httpGet);
	        HttpEntity httpEntity = httpResponse.getEntity();
	        response = EntityUtils.toString(httpEntity);
			
			return response;
			
		}
	    
	    //JSON parsing methods
	    
	    private static String loadJSONFromAsset(Context context, String filename) {
	        String json = null;
	        try {
	            InputStream is = context.getAssets().open(filename);
	            int size = is.available();
	            byte[] buffer = new byte[size];
	            is.read(buffer);
	            is.close();
	            json = new String(buffer, "UTF-8");
	        } catch (IOException ex) {
	            ex.printStackTrace();
	            return null;
	        }
	        return json;
	    }
	    
	    private static SGGPosition geocode(String JSONstring) throws JSONException {
	    	
			JSONObject geocodes = new JSONObject(JSONstring);
			JSONArray results = geocodes.getJSONArray("SearchResults");
			JSONObject firstresult = results.getJSONObject(1);
			
			Double x = firstresult.getDouble("X");
			Double y = firstresult.getDouble("Y");
			String title = firstresult.getString("SEARCHVAL");
			
			return new SGGPosition (x, y, title);
			
		}
		
		private static BusRoute getRoute(String JSONstring, SGGPosition positionA, SGGPosition positionB)
				throws JSONException, URISyntaxException, ClientProtocolException, IOException{
			
			JSONObject object = new JSONObject(JSONstring);
			JSONArray busROUTE = object.getJSONArray("BusRoute");
			JSONObject firstSolution = busROUTE.getJSONObject(0);
			JSONArray steps = firstSolution.getJSONArray("STEPS");
			
			int numberOfSteps = steps.length();
			BusStep[] busRoute = new BusStep[numberOfSteps];
			
			for (int i = 0; i < numberOfSteps; i++) {
				
				JSONObject step = steps.getJSONObject(i);
				
				String startCode = step.getString("BoardId");
				String startTitle = null;
				if (startCode.equals("") ) {
					
					startCode = busRoute[i-1].getEndCode();
					startTitle = busRoute[i-1].getEndTitle();
					
				} else {
					startTitle = step.getString("BoardDesc");
				}
				
				String busCode = step.getString("ServiceID");
				
				String endCode = step.getString("AlightId");
				String endTitle = step.getString ("AlightDesc");
				
				BusStep busStep = new BusStep(startCode, startTitle, busCode, endCode, endTitle);
				busRoute[i] = busStep;
				
			}
			
			return new BusRoute (busRoute, positionA, positionB);
			
		}
		
		private static SGGPosition sdGeocode(String JSONstring) throws JSONException {
			
			JSONArray geocodes = new JSONArray(JSONstring);
			JSONObject firstresult = geocodes.getJSONObject(1);
			Double longitude = firstresult.getDouble("x");
			Double latitude = firstresult.getDouble("y");
			String title = firstresult.getString("t");
			
			return new SGGPosition(latitude, longitude, title, C.CONVERT_LATLNG_TO_SVY21);
		
		}
		
		private static SGGPosition sdBusStopGeocode(String JSONstring) throws JSONException {
			
			SGGPosition returnResult = null;
			
			JSONArray geocodes = new JSONArray(JSONstring);
			for (int i = 1; i < geocodes.length(); i++) {
				JSONObject ithResult = geocodes.getJSONObject(i);
				String title = ithResult.getString("t");
				if (title.contains(" (B")) {
					Double longitude = ithResult.getDouble("x");
					Double latitude = ithResult.getDouble("y");
					returnResult = new SGGPosition(latitude, longitude, title, C.CONVERT_LATLNG_TO_SVY21);
					break;
				}
			}
			
			return returnResult;
		
		}
	

}
