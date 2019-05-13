package com.hiking.minimap.waypoints;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hiking.minimap.R;

public class WaypointListActivity extends Activity 
{
	EditText waypointList;
	EditText numToEdit;
	
	SharedPreferences prefs;
	int numPoints;
	ArrayList<Waypoint> waypoints;
	String TAG = "WaypointListActivity";
	
	Intent editor;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waypoint_list);
		waypointList = (EditText) findViewById(R.id.waypointList);
		numToEdit = (EditText) findViewById(R.id.numToEdit);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		numPoints = prefs.getInt("waypointCount", 0);
		Log.d(TAG, "There are " + Integer.toString(numPoints) + " waypoints saved on the phone");
		
		//loads waypoints starting at "waypoint0"
		waypoints = new ArrayList<Waypoint>();
		for(int i = 0; i<numPoints; i++)
		{
			Log.d(TAG, "Waypoint " + i + ": " + prefs.getString("waypoint" + Integer.toString(i), ""));
			waypoints.add(new Waypoint(prefs.getString("waypoint" + Integer.toString(i), "")));
		}
	}
	
	protected void onResume()
	{
		updateList();
		super.onResume();
	}
	
	protected void onPause()
	{
		super.onPause();
	}

	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.waypoint_list, menu);
		return true;
	}
	
	public void editWaypoint(View v)
	{
		int wN = Integer.parseInt(numToEdit.getText().toString());
		if(wN>=0&&wN<numPoints)
		{
			editor = new Intent(this, WaypointEditorActivity.class);
			editor.putExtra("wN", wN);
			Log.d(TAG, "Opening the editor for waypoint " + Integer.toString(editor.getIntExtra("wN", 0)));
			this.startActivity(editor);
		}
		else
		{
			Toast toast = Toast.makeText(this, "Not a valid waypoint", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	public void deleteWaypoint()
	{
		int wN = Integer.parseInt(numToEdit.getText().toString());
		if(wN>=0&&wN<numPoints)
		{
			numPoints--;
			waypoints.remove(wN);
			Toast toast = Toast.makeText(this, "Waypoint Deleted", Toast.LENGTH_SHORT);
			//Toast toast = Toast.makeText(this, waypoints.get(wN).getLocation().getLongitude() + "\n" + waypoints.get(wN).getLocation().getLatitude()/*"Would Delete " + waypoints.get(wN).getName()*/, Toast.LENGTH_SHORT);
			toast.show();
			saveWaypoints();
			updateList();
		}
		else
		{
			Toast toast = Toast.makeText(this, "Not a valid waypoint", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	//for use before closing
	public void saveWaypoints()
	{
		prefs.edit().putInt("waypointCount", numPoints).commit();
		for(int i = numPoints-1; i>=0; i--)
		{
			prefs.edit().putString("waypoint" + Integer.toString(i), waypoints.get(i).toString()).commit();
		}
	}
	
	public void updateList()
	{
		waypointList.setText("");
		for(int i = 0; i<numPoints; i++)
		{
			Waypoint w = waypoints.get(i);
			waypointList.setText(waypointList.getText()+"\nWaypoint " + i + ": " + w.getName()+" - "+w.getDesc());
		}
	}
	
	public void deleteChoice(View v) {
	    new AlertDialog.Builder(WaypointListActivity.this)
	            .setTitle("Delete Pressed")
	            .setMessage("Are you sure you want to delete?")
	            .setPositiveButton("YES",
	                    new DialogInterface.OnClickListener() {
	                        @TargetApi(11)
	                        public void onClick(DialogInterface dialog, int id) {
	                            deleteWaypoint();
	                            dialog.cancel();
	                        }
	                    })
	            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
	                @TargetApi(11)
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            }).show();
	}
}
