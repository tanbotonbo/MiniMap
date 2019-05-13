package com.hiking.minimap.waypoints;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.hiking.minimap.R;

public class WaypointCreatorActivity extends Activity 
{
	SeekBar redBar;
	SeekBar greenBar;
	SeekBar blueBar;
	public final String TAG = "WaypointCreatorActivity";
	
	private EditText name;
	private EditText desc;
	private EditText longitude;
	private EditText latitude;
	private Button save;
	private TextView accuracy;
	SharedPreferences prefs;
	LocationListener locListener;
	LocationManager locManager;
	GpsStatus.Listener gpsStatus;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waypoint_creator);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		name = (EditText) findViewById(R.id.name);
		desc = (EditText) findViewById(R.id.desc);
		longitude = (EditText) findViewById(R.id.longitude);
		latitude = (EditText) findViewById(R.id.latitude);
		save = (Button) findViewById(R.id.save);
		accuracy = (TextView) findViewById(R.id.accuracy);
		redBar = (SeekBar) findViewById(R.id.redBar);
		greenBar = (SeekBar) findViewById(R.id.greenBar);
		blueBar = (SeekBar) findViewById(R.id.blueBar);
		
		gpsStatus = new GpsStatus.Listener() 
		{
			//a secondary activator of fixOutput(), only seems to function when the 
			//activity starts...
			public void onGpsStatusChanged(int indicator) 
			{
				if(indicator == GpsStatus.GPS_EVENT_STARTED)
				{
					locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locListener);
					Toast enabled = Toast.makeText(getBaseContext(), "GPS Provider Enabled", Toast.LENGTH_SHORT);
					enabled.show();
				}
				if(indicator == GpsStatus.GPS_EVENT_STOPPED)
				{
					locManager.removeUpdates(locListener);
					Toast disabled = Toast.makeText(getBaseContext(), "GPS Provider Disabled", Toast.LENGTH_SHORT);
					disabled.show();
				}
			}
		};
		
		locListener = new LocationListener()
		{
			public void onLocationChanged(Location location) 
			{
				if(location == null)
				{
					Toast toast = Toast.makeText(getBaseContext(), "Null Location", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				longitude.setText(Double.toString(location.getLongitude()));
				latitude.setText(Double.toString(location.getLatitude()));
				accuracy.setText("Accuracy: " + Double.toString(location.getAccuracy()) + " meters");
			}
			
			public void onProviderDisabled(String provider) 
			{
				Toast toast = Toast.makeText(getBaseContext(), "Provider Disabled", Toast.LENGTH_SHORT);
				toast.show();
			}
			
			public void onProviderEnabled(String provider) 
			{
				Toast toast = Toast.makeText(getBaseContext(), "Provider Enabled", Toast.LENGTH_SHORT);
				toast.show();
			}
			
			public void onStatusChanged(String provider, int status, Bundle extras) 
			{
				//Toast toast = Toast.makeText(getBaseContext(), "GPS Status Changed", Toast.LENGTH_SHORT);
				//toast.show();
			}
		};
		
		locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locManager.addGpsStatusListener(gpsStatus);
	}

	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.waypoint_creator, menu);
		return true;
	}
	
	protected void onResume()
	{
		super.onResume();
		if ( locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 25, 0, locListener);
		}
	}
	
	protected void onPause()
	{
		super.onPause();
		locManager.removeUpdates(locListener);
	}
	
	public void saveWaypoint(View v)
	{
		int i = prefs.getInt("waypointCount", 0);
		String color = Integer.toString(Color.rgb(redBar.getProgress(), greenBar.getProgress(), blueBar.getProgress()));
		Log.d(TAG, color);
		prefs.edit().putString("waypoint" + Integer.toString(i), name.getText() + "\n" + desc.getText() + "\n" + longitude.getText() + "\n" + latitude.getText() + "\n" + color).commit();
		prefs.edit().putInt("waypointCount", i+1).commit();
		Toast toast = Toast.makeText(this, "Waypoint Saved", Toast.LENGTH_SHORT);
		toast.show();
	}
}
