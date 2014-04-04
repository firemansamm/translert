package com.translert.bus;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OneMapJSONparser {

	public OneMapJSONparser() {
		// TODO Auto-generated constructor stub
	}
	
//	public int[] nextbus(String JSONstring, String buscode) throws JSONException {
//		int [] nextSubsequent = new int[2];
//		JSONArray bustimings = new JSONArray(JSONstring);
//		for (int i = 0; i < bustimings.length(); i++) {
//			JSONObject bustiming = bustimings.getJSONObject(i);
//			String thisbuscode = bustiming.getString("b");
//			if (buscode == thisbuscode) {
//				nextSubsequent[0] = bustiming.getInt("n");
//				nextSubsequent[1] = bustiming.getInt("s");
//				break;
//			} else {
//				continue;
//			}
//		}
//		return nextSubsequent;
//	}
	
	public SGGPosition geocode(String JSONstring) throws JSONException {
	
		JSONObject geocodes = new JSONObject(JSONstring);
		JSONArray results = geocodes.getJSONArray("SearchResults");
		JSONObject firstresult = results.getJSONObject(1);
		
		Double x = firstresult.getDouble("X");
		Double y = firstresult.getDouble("Y");
		String title = firstresult.getString("SEARCHVAL");
		
		return new SGGPosition (x, y, title);
		
	}
	
	public BusRoute getRoute(String JSONstring, SGGPosition positionA, SGGPosition positionB)
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
				
				startCode = busRoute[i-1].endCode;
				startTitle = busRoute[i-1].endTitle;
				
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

}
