package com.translert;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

public class PathFinder {

	//experimental implementation of Dijkstra's algorithm with comparators...
	//TODO: needs more accurate edge weights
	
	int changeTime = 4, stationTime = 3; //est time for changing trains is 4 mins according to SMRT, assume avg time of 3 mins betw statinos
	public static State answer;
	
	public class Transfer{
		public Station position;
		public int atTime;
		public Transfer(Station pos, int time){
			atTime = time;
			position = pos;
		}
	}
	
	public class State{
		public int totalTime, curNo;
		public Station position, start, end;
		public String onLine;
		public ArrayList<Transfer> xfers;
		public boolean changing;
		public State(Station pos, int time, String line, int no, boolean changed, ArrayList<Transfer> xfs, Station b, Station e){
			position = pos;
			xfers = xfs;
			if(changed) xfers.add(new Transfer(pos, time - changeTime));
			totalTime = time;
			changing = changed;
			onLine = line;
			curNo = no;
			start = b;
			end = e;
		}
	}
	
	Activity baseActivity;
	
	public PathFinder(Activity a){
		baseActivity = a;
		String jsonData = loadJSONFromAsset();
		try {
			JSONObject obj = new JSONObject(jsonData);
			JSONObject subObject = obj.getJSONObject("Result");
			JSONArray stationsArray = subObject.getJSONArray("Stations");
			for(int i=0;i<stationsArray.length();i++){
				new Station(stationsArray.getJSONObject(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public class shortestTimeComparator implements Comparator<State>{
		@Override
		public int compare(State arg0, State arg1) {
			if(arg0.totalTime < arg1.totalTime) return -1;
			if(arg0.totalTime > arg1.totalTime) return 1;
			return 0;
		}
	}
	
	public class leastTransferComparator implements Comparator<State>{
		@Override
		public int compare(State arg0, State arg1) {
			if(arg0.xfers.size() < arg1.xfers.size()) return -1;
			if(arg0.xfers.size() > arg1.xfers.size()) return 1;
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public State routeMe(Station begin, Station end, int comparisonType){
		//comparison types: 0 - shortest time
		//					1 - least transfers
		PriorityQueue<State> pq;
		HashMap<String, Boolean> visited = new HashMap<String, Boolean>();
		if(comparisonType == 0) pq = new PriorityQueue<State>(1, new shortestTimeComparator()); 
		else pq = new PriorityQueue<State>(1, new leastTransferComparator()); 
		for(int i=0;i<begin.line.size();i++){
			pq.add(new State(begin, 0, begin.line.get(i), begin.no.get(i), false, new ArrayList<Transfer>(), begin, end));
		}
		State answerState = null;
		while(pq.size() != 0){
			State cur = pq.remove();
			if(cur.position.longName.equals(end.longName)){
				Log.d("translert", "we are at end!");
				Log.d("translert", "we took " + String.valueOf(cur.totalTime) + " minutes.");
				for(int i=0;i<cur.xfers.size();i++){
					Log.d("translert", "we had to transfer at " + cur.xfers.get(i).position.longName + " at " + String.valueOf(cur.xfers.get(i).atTime) + " minutes.");
				}
				answerState = cur;
				break;
			}
			if(visited.containsKey(cur.onLine + cur.curNo)) continue;
			visited.put(cur.onLine + cur.curNo, true);
			//next station
			String newKey = cur.onLine + String.valueOf(cur.curNo + 1);
			if(Station.signLookup.containsKey(newKey)){
				Station e = Station.signLookup.get(cur.onLine + String.valueOf(cur.curNo + 1));
				pq.add(new State(e, cur.totalTime + stationTime, cur.onLine, cur.curNo + 1, false, (ArrayList<Transfer>) cur.xfers.clone(), begin, end));
			}
			//previous station
			newKey = cur.onLine + String.valueOf(cur.curNo - 1);
			if(Station.signLookup.containsKey(newKey)){
				Station e = Station.signLookup.get(cur.onLine + String.valueOf(cur.curNo - 1));
				pq.add(new State(e, cur.totalTime + stationTime, cur.onLine, cur.curNo - 1, false, (ArrayList<Transfer>) cur.xfers.clone(), begin, end));
			}
			//any changes
			if(!cur.position.isInterchange) continue;
			for(int i=0;i<cur.position.line.size();i++){
				if(cur.position.line.get(i).equals(cur.onLine)) continue;
				State n = new State(cur.position, cur.totalTime + changeTime, cur.position.line.get(i), cur.position.no.get(i), true, (ArrayList<Transfer>) cur.xfers.clone(), begin, end);
				pq.add(n);
			}
		}
		return answerState;
		//MainActivity.pref.addTrip(new Trip(new Date().toString(), begin.longName, end.longName, answerState.totalTime, comparisonType, answerState.xfers.size()));
	}
	
	public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = baseActivity.getAssets().open("stations.txt");
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
