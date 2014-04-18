package com.translert.train.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.translert.R;
import com.translert.train.MainActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RecentTripsAdapter extends BaseAdapter {
	
	MainActivity baseActivity;
	
	
	public RecentTripsAdapter(MainActivity a){
		baseActivity = a;
	}

	@Override
	public int getCount() {
		return MainActivity.pref.recent.size() + 1;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View ret;
		LayoutInflater inf = (LayoutInflater) baseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ret = inf.inflate(R.layout.listrow, parent, false);
		if(position == 0){
			TextView tv = (TextView) ret.findViewById(R.id.name);
			tv.setText("Create a new trip...");
			tv.setTextSize(17.0f);
			tv = (TextView) ret.findViewById(R.id.dates);
			tv.setVisibility(View.GONE);
		}else{
			Trip t = MainActivity.pref.recent.get(position - 1);
			TextView tv = (TextView) ret.findViewById(R.id.name);
			tv.setText(t.source + " - " + t.destination);
			tv = (TextView) ret.findViewById(R.id.dates);
			Format formatter = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.ENGLISH);
			//String typestr = (t.type == 0) ? "shortest" : "least transfers";
			String xfstr = ", " + t.xfc + " transfer" + (t.xfc > 0 ? "s" : "");
			tv.setText(formatter.format(t.date) + ", " + t.minutes + " mins" + (t.xfc > 0 ? xfstr : "") /*+ " (" + typestr + ")"*/);
		}
		return ret;
	}

}
