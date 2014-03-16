package com.translert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class Station{
	public String longName, shortName;
	public ArrayList<String> line;
	public ArrayList<Integer> no;
	public boolean isInterchange;
	public Station(JSONObject parsedObject) throws JSONException {
		longName = parsedObject.getString("longName");
		String sign = parsedObject.getString("line") + parsedObject.getString("no");
		if(!lines.contains(parsedObject.getString("line"))){
			lines.add(parsedObject.getString("line"));
			names.add(new ArrayList<String>());
		}
		int pp = lines.indexOf(parsedObject.getString("line"));
		names.get(pp).add(longName);
		if(reverseLookup.containsKey(longName)){
			Station x = reverseLookup.get(longName);
			x.line.add(parsedObject.getString("line"));
			String orig = parsedObject.getString("no");
			if(!orig.equals("")) x.no.add(Integer.parseInt(orig));
			else x.no.add(0);
			x.isInterchange = true;
			signLookup.put(sign, x);
			return;
		}
		shortName = parsedObject.getString("shortName");
		line = new ArrayList<String>();
		line.add(parsedObject.getString("line"));
		no = new ArrayList<Integer>();
		String orig = parsedObject.getString("no");
		if(!orig.equals("")) no.add(Integer.parseInt(orig));
		else no.add(0);
		reverseLookup.put(longName, this);
		shortLookup.put(shortName, this);
		all.add(this);
		signLookup.put(sign, this);
	}
	
	public static ArrayList<Station> all = new ArrayList<Station>();
	public static Map<String, Station> reverseLookup = new HashMap<String, Station>();
	public static Map<String, Station> shortLookup = new HashMap<String, Station>();
	public static Map<String, Station> signLookup = new HashMap<String, Station>();
	public static ArrayList<String> lines = new ArrayList<String>();
	public static ArrayList<ArrayList<String>> names = new ArrayList<ArrayList<String>>();
}