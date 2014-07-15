package com.translert.bus.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class F {
	
		public static ArrayList<String> getBusStopNames (Context context, String busNumber) {
			ArrayList<String> busStopNames = new ArrayList<String>();
			
			try {
				String filename = "bus-services/" + busNumber + ".json";
				String jsonRoute = loadJSONFromAsset (context, filename);
				ArrayList<String> stopsList = new ArrayList<String>();
				for (int i = 1; i <=2 ; i++) {
					JSONArray  stops  = new JSONObject (jsonRoute).getJSONObject(String.valueOf(i)).getJSONArray("stops");
					for (int j = 0; j < stops.length(); j++) {
						stopsList.add( stops.getString(j) );
					}
				}	
				
				filename = "bus-stops.json";
				String jsonStops = loadJSONFromAsset (context, filename);
				JSONArray allStops = new JSONArray (jsonStops);
				
				for (int i = 0; i < allStops.length(); i++) {
					JSONObject currentObject = allStops.getJSONObject(i);
					String no = currentObject.getString("no");
					if ( stopsList.contains(no) ) {
						String name = currentObject.getString("name");
						busStopNames.add(name);
					}
				}
			} catch (JSONException e) {
				return null;
			}
			return busStopNames;
		}
		
		public static SGGPosition getBusStopPosition (Context context, String busDestination, String busNumber) {
			SGGPosition result = null;
			final String busStopNameLC = busDestination.toLowerCase();
			
			try {
				String filename = "bus-services/" + busNumber + ".json";
				String jsonRoute = loadJSONFromAsset (context, filename);
				ArrayList<String> stopsList = new ArrayList<String>();
				for (int i = 1; i <= 2; i++) {
					JSONArray  stops  = new JSONObject (jsonRoute).getJSONObject(String.valueOf(i)).getJSONArray("stops");
					for (int j = 0; j < stops.length(); j++) {
						stopsList.add( stops.getString(j) );
					}
				}
				
				filename = "bus-stops.json";
				String jsonStops = loadJSONFromAsset (context, filename);
				JSONArray allStops = new JSONArray (jsonStops);
				for (int i = 0; i < allStops.length(); i++) {
					
					JSONObject currentObject = allStops.getJSONObject(i);
					String no = currentObject.getString("no");
					
					if ( stopsList.contains(no) ) {
						
						String name = currentObject.getString("name").toLowerCase();
//						Log.d("stop number & stop name", no + ", " + name);
						
						if (name.contains(busStopNameLC) || no.contains(busStopNameLC)) {
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
}
