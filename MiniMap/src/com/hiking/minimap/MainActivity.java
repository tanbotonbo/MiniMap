package com.hiking.minimap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.hiking.minimap.waypoints.WaypointCreatorActivity;
import com.hiking.minimap.waypoints.WaypointListActivity;

public class MainActivity extends Activity
{
	public final String TAG = "MainActivity";	
	FrameLayout frame;
	RadarPanel radarPanel;
	public Intent waypointCreator;

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		frame = (FrameLayout) findViewById(R.id.frame);	
		radarPanel = new RadarPanel(this);
		frame.addView(radarPanel);
	}

	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void onPause()
	{
		super.onPause();
		//radarPanel.closeThread();
	}
	
	protected void onResume()
	{
		super.onResume();
		//radarPanel.startThread();
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.action_settings:
				openSettings();
				return true;
		case R.id.new_waypoint:
				openWaypointCreator();
				return true;
		case R.id.edit_waypoints:
				openWaypointEditor();
				return true;
		default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	//settings to be implemented later
	public void openSettings()
	{
		Intent settings = new Intent (this, SettingsActivity.class);
		startActivity(settings);
	}
	//a new activity which adds a waypoint to the xml, which will be loaded
	//upon returning to the minimap
	public void openWaypointCreator()
	{
		waypointCreator = new Intent (this, WaypointCreatorActivity.class);
		startActivity(waypointCreator);
	}
	//opens an activity which displays a list of all waypoints and allows you to choose one
	//this opens a new activity which allows you to edit or delete the waypoint
	public void openWaypointEditor()
	{
		Intent editor = new Intent(this, WaypointListActivity.class);
		this.startActivity(editor);
	}
}
