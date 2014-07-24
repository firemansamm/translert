package com.translert.bus.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

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
			busNumber = busNumber.trim();
			String filename = "bus-services/" + busNumber + ".json";
			String jsonRoute = loadJSONFromAsset (context, filename);
			ArrayList<String> stopNumbersOnRoute = new ArrayList<String>();
			for (int i = 1; i <=2 ; i++) {
				JSONArray  stopsJSON  = new JSONObject (jsonRoute).getJSONObject(String.valueOf(i)).getJSONArray("stops");
				for (int j = 0; j < stopsJSON.length(); j++) {
					stopNumbersOnRoute.add( stopsJSON.getString(j) );
				}
			}
			
			filename = "bus-stops-by-number.json";
			JSONObject stopNumbersAll = new JSONObject (loadJSONFromAsset (context, filename));
			
			Iterator<String> iterator = stopNumbersOnRoute.iterator();
			while (iterator.hasNext()) {
				String stopNumber = iterator.next();
				String name = stopNumbersAll.getJSONObject(stopNumber).getString("name");
				busStopNames.add(name);
			}
		} catch (JSONException e) {
			return null;
		}
		return busStopNames;
	}
	
	public static SGGPosition getSingleBusStop (Context context, String busStopName, String busNumber) {
		SGGPosition result = null;
		try {
			String jsonStops = loadJSONFromAsset (context, "bus-stops-by-name.json");
			JSONObject obj = new JSONObject(jsonStops).getJSONObject(busStopName);
			Double lat = obj.getDouble("lat");
			Double lng = obj.getDouble("lng");
			result = new SGGPosition(lat, lng, busStopName);
			Log.d("Found stop", result.toString());
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
