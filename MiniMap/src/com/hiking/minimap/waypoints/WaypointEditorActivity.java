package com.hiking.minimap.waypoints;

import com.hiking.minimap.R;
import com.hiking.minimap.R.layout;
import com.hiking.minimap.R.menu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class WaypointEditorActivity extends Activity 
{
	private EditText name;
	private EditText desc;
	private EditText longitude;
	private EditText latitude;
	private Button save;
	private TextView accuracy;
	private final String TAG = "WaypointEditorActivity";
	SharedPreferences prefs;
	private int point;
	Waypoint waypoint;
	SeekBar redBar;
	SeekBar greenBar;
	SeekBar blueBar;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		Log.d(TAG, "Loading waypoint " + getIntent().getIntExtra("wN", 0) + "'s editor");
		point = getIntent().getIntExtra("wN", 0);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waypoint_editor);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		name = (EditText) findViewById(R.id.name);
		desc = (EditText) findViewById(R.id.desc);
		longitude = (EditText) findViewById(R.id.longitude);
		latitude = (EditText) findViewById(R.id.latitude);
		save = (Button) findViewById(R.id.save);

		redBar = (SeekBar) findViewById(R.id.redBar);
		greenBar = (SeekBar) findViewById(R.id.greenBar);
		blueBar = (SeekBar) findViewById(R.id.blueBar);
		
		waypoint = new Waypoint(prefs.getString("waypoint" + Integer.toString(point), ""));
		
		name.setText(waypoint.getName());
		desc.setText(waypoint.getDesc());
		longitude.setText(Double.toString(waypoint.getLocation().getLongitude()));
		latitude.setText(Double.toString(waypoint.getLocation().getLatitude()));
		
		String colorHex = Integer.toHexString(waypoint.getColor());
		try
		{
		redBar.setProgress(Integer.parseInt(colorHex.substring(2,4), 16));
		greenBar.setProgress(Integer.parseInt(colorHex.substring(4,6), 16));
		blueBar.setProgress(Integer.parseInt(colorHex.substring(6,8), 16));
		}
		catch(Exception e)
		{
			
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.waypoint_editor, menu);
		return true;
	}
	
	public void saveWaypointChanges(View v)
	{
		String color = Integer.toString(Color.rgb(redBar.getProgress(), greenBar.getProgress(), blueBar.getProgress()));
		Log.d(TAG, Integer.toHexString(Integer.parseInt(color)));
		Log.d(TAG, "Edited waypoint " + point);
		prefs.edit().putString("waypoint" + Integer.toString(point), name.getText() + "\n" + desc.getText() + "\n" + longitude.getText() + "\n" + latitude.getText() + "\n" + color).commit();
		Toast toast = Toast.makeText(this, "Saved Changes", Toast.LENGTH_SHORT);
		toast.show();
	}
}
