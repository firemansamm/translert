package com.translert.bus;
//package com.translert;
//
//import java.util.ArrayList;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//public class StreetDirectoryJSONparser {
//
//	public StreetDirectoryJSONparser() {
//		// TODO Auto-generated constructor stub
//	}
//	
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
//	
//	public SGGPosition geocode(String JSONstring) throws JSONException {
//	
//		JSONArray geocodes = new JSONArray(JSONstring);
//		JSONObject firstresult = geocodes.getJSONObject(1);
//		Double longitude = firstresult.getDouble("x");
//		Double latitude = firstresult.getDouble("y");
//		String title = firstresult.getString("t");
//		
//		return new SGGPosition(longitude, latitude, title);
//	
//	}
//	//public ArrayList<BusStep> getRoute(String JSONstring) throws JSONException{
//	public ArrayList<BusStep> getRoute(String JSONstring) throws JSONException{
//		
//		ArrayList<BusStep> BusRoute = new ArrayList<>();
//		
//		
//		JSONObject direction = new JSONObject(JSONstring);
//		JSONObject zero = direction.getJSONObject("0");
//		int steps = zero.getInt("total");
//		
//		for (int i = 1; i<=steps; i++) {
//			String name = "" + i;
//			JSONObject step = direction.getJSONObject(name);
//			String title = step.getString("title");
//			
//			if (title.contains("Bus Stop")) {
//				SGGPosition startUTM = new SGGPosition (0.0, 0.0);
//				SGGPosition endUTM = new SGGPosition (0.0, 0.0);
//				
//				String startCode = title.substring(9,15);
//				
////				if (step.get("x") != null && step.get("y") != null) {
////					startUTM = new UTMposition(step.getDouble("x"), step.getDouble("y"));
////				}
//				
//				
//				String bcl = step.getString("desc1");
//				String busCode = bcl.substring(9, bcl.length()-1);
//				
//				String endname = "" + (i+1);
//				JSONObject endstep = direction.getJSONObject(endname);
//				String endtitle = endstep.getString("title");
//				
//				String endCode = endtitle.substring(endtitle.length() - 6);
////				if (endstep.get("x") != null && endstep.get("y") != null) {
////					endUTM = new UTMposition(endstep.getDouble("x"), endstep.getDouble("y"));
////				}
//				
//				
//				BusStep busStep = new BusStep(startCode, null, busCode, endCode, null);
//				
//				BusRoute.add(busStep);
//				
//			} else {
//				continue;
//			}
//			
//		}
//		
//		
//		return BusRoute;
//	}
//
//}
