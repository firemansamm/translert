package com.translert;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferencer {
	
	SharedPreferences prefs;
	MainActivity baseActivity;
	public ArrayList<Trip> recent = new ArrayList<Trip>();
	
	Preferencer(MainActivity a){
		baseActivity = a;
		prefs = a.getSharedPreferences("prefs", Context.MODE_PRIVATE);
		loadObj();
	}
	
	public void addTrip(Trip a){
		recent.add(0, a);
		while(recent.size() > 5) recent.remove(5);
		writeObj();
		MainActivity.mAdapter.notifyDataSetChanged();
	}

	public void writeObj(){
		try {
			BufferedOutputStream bs = new BufferedOutputStream(new FileOutputStream("trips"));
			ObjectOutputStream oos = new ObjectOutputStream(bs);
			oos.writeObject(recent);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadObj(){
		try {
			BufferedInputStream bs = new BufferedInputStream(new FileInputStream("trips"));
			ObjectInputStream ois = new ObjectInputStream(bs);
			recent = (ArrayList<Trip>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
