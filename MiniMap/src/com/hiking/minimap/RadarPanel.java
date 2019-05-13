package com.hiking.minimap;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.hiking.minimap.waypoints.Waypoint;

public class RadarPanel extends SurfaceView implements SurfaceHolder.Callback
{
	public final String TAG = "RadarPanel";
	public final Bitmap marker =  BitmapFactory.decodeResource(getResources(), R.drawable.marker);
	public final Bitmap arrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
	public SharedPreferences prefs;
	private MainThread thread;

	private int RANGE;
	private ArrayList<Waypoint> waypoints;
	int numPoints;
	private Paint textPaint;
	
	private LocationManager locManager;
	private LocationListener locListener;
	private GpsStatus.Listener gpsStatus;
	private Location loc;

	public RadarPanel(Context context)
	{
		super(context);
		
		gpsStatus = new GpsStatus.Listener() 
		{
			//a secondary activator of fixOutput(), only seems to function when the 
			//activity starts...
			@Override
			public void onGpsStatusChanged(int indicator) 
			{
				if(indicator == GpsStatus.GPS_EVENT_STARTED)
				{
					locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locListener);
					Toast enabled = Toast.makeText(getContext(), "GPS Provider Enabled", Toast.LENGTH_SHORT);
					enabled.show();
				}
				if(indicator == GpsStatus.GPS_EVENT_STOPPED)
				{
					locManager.removeUpdates(locListener);
					Toast disabled = Toast.makeText(getContext(), "GPS Provider Disabled", Toast.LENGTH_SHORT);
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
					Toast toast = Toast.makeText(getContext(), "Null Location", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				loc = location;
			}
			
			public void onProviderDisabled(String provider) 
			{
				Toast toast = Toast.makeText(getContext(), "Provider Disabled", Toast.LENGTH_SHORT);
				toast.show();
			}
			
			public void onProviderEnabled(String provider) 
			{
				Toast toast = Toast.makeText(getContext(), "Provider Enabled", Toast.LENGTH_SHORT);
				toast.show();
			}
			
			public void onStatusChanged(String provider, int status, Bundle extras) 
			{
				//Toast toast = Toast.makeText(getContext(), "GPS is being weird", Toast.LENGTH_SHORT);
				//toast.show();
			}
		};
		
		locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		locManager.addGpsStatusListener(gpsStatus);
		loc = new Location("GPS");
		loc.setLongitude(0);
		loc.setLatitude(0);

		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		RANGE = Integer.parseInt(prefs.getString("range", "100"));
		numPoints = prefs.getInt("waypointCount", 0);
		Log.d(TAG, "There are " + Integer.toString(numPoints) + " waypoints saved on the phone");
		
		textPaint = new Paint();
		textPaint.setTextSize(20);
		textPaint.setColor(Color.WHITE);
		textPaint.setStyle(Style.FILL);
		
		//loads waypoints starting at "waypoint0"
		loadWaypoints();
		
		//this apparently allows event interception
		getHolder().addCallback(this);
		//make focusable to handle events apparently
		setFocusable(true);
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
	{
		
	}

	public void surfaceCreated(SurfaceHolder holder) 
	{
		RANGE = Integer.parseInt(prefs.getString("range", "100"));
		Log.d(TAG, "Range is " + RANGE + " meters");
		numPoints = prefs.getInt("waypointCount", 0);
		Log.d(TAG, "There are " + Integer.toString(numPoints) + " waypoints saved on the phone");
		
		//loads waypoints starting at "waypoint0"
		loadWaypoints();
		
		Log.d(TAG, "Surface Created");
		//Now start render thread
		thread = new MainThread(getHolder(), this);
		startThread();
		
		//second start GPS
		if ( locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 25, 0, locListener);
		}
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		Log.d(TAG, "Surface Destroyed");
		//end thread
		closeThread();
		//end GPS for now
		locManager.removeUpdates(locListener);
	}
	
	public boolean onTouchEvent(MotionEvent event)
	{
		//x = (int) event.getX();
		//y = (int) event.getY();
		Log.d(TAG, "Coords: x=" + event.getX() + ", y=" + event.getY());
		return super.onTouchEvent(event);
	}
	
	public void draw(Canvas canvas)
	{
		if(canvas==null)
		{
			Log.d(TAG, "Canvas is null");
			return;
		}
		canvas.drawColor(Color.BLACK);
		
		canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.radar_screen), 40, 300, null);
		
		canvas.drawText("Accuracy: " + loc.getAccuracy() + " meters", 40, 60, textPaint);
		
		//draw waypoints here
		int wP = prefs.getInt("waypointCount", 0);
		//display each waypoint in order
		Paint p = new Paint();
		for(int index = 0; index<wP; index++)
		{
			Waypoint w = waypoints.get(index);
			double distance = loc.distanceTo(w.getLocation())/RANGE*320;
			if(distance>320)
				distance = 320;
			double angle = (loc.bearingTo(w.getLocation())-90)*Math.PI/180;
			int xDist = ((int) (Math.cos(angle)*distance)) + 360;
			int yDist = ((int) (Math.sin(angle)*distance)) + 620;
			
			p.setColorFilter(new LightingColorFilter(w.getColor(), 0));
			if(distance==320)
			{
				//Log.d(TAG, Integer.toString(xDist)+ " " + Integer.toString(yDist) + " " + Double.toString(angle));
				canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.arrow), xDist-8, yDist-12, p);
			}
			else
				canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.marker), xDist-8, yDist-12, p);
		}
		//canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.marker), x-8, y-12, p);
	}
	
	/*public void addWaypoint(String fullDesc)
	{
		waypoints.add(new Waypoint(fullDesc));
		numPoints++;
	}
	
	public void removeWaypoint(int num)
	{
		if(num<waypoints.size())
		{
			waypoints.remove(num);
			numPoints--;
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
	}*/

	public void closeThread()
	{
		thread.setRunning(false);
		boolean retry = true;
		while(retry)
		{
			try
			{
				thread.join();
				retry = false;
			}
			catch(InterruptedException e)
			{
				//try again shutting down the thread
			}
		}
	}
	
	public void startThread()
	{
		if(!thread.getState().equals(Thread.State.RUNNABLE))
		{
			thread.setRunning(true);
			thread.start();
		}
		else
		{
			Log.d(TAG, "Apparently not runnable???");
			Log.d(TAG, thread.getState().toString());
		}
	}
	
	private void loadWaypoints()
	{
		waypoints = new ArrayList<Waypoint>();
		for(int i = 0; i<numPoints; i++)
		{
			Log.d(TAG, "Waypoint " + i + ": " + prefs.getString("waypoint" + Integer.toString(i), ""));
			waypoints.add(new Waypoint(prefs.getString("waypoint" + Integer.toString(i), "")));
		}
		numPoints = waypoints.size();
	}
}
