package com.hiking.minimap;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

@SuppressLint("WrongCall")
public class MainThread extends Thread
{
	public final String TAG = "MainThread";
	private boolean running = false;
	private SurfaceHolder surfaceHolder;
	private RadarPanel radarPanel;
	long time;
	
	public void run()
	{
		Canvas canvas;
		Log.d(TAG, "Starting game loop");
		time = System.currentTimeMillis();
		int elapsed = 0;
		while(running)
		{
			elapsed += time - System.currentTimeMillis();
			if(elapsed > 50)
			{
				elapsed -= 50;
				canvas = null;
				try
				{
					canvas = this.surfaceHolder.lockCanvas();
					synchronized(surfaceHolder)
					{
						//update game state
						//draw the canvas on the panel
						this.radarPanel.draw(canvas);
					}
				}
				finally
				{
					//in case of exception no inconsistency
					if(canvas!=null)
					{
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}
		Log.d(TAG, getState().toString());
		Log.d(TAG, "Thread ending probably");
		return;
	}

	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public MainThread(SurfaceHolder surfaceHolder, RadarPanel radarPanel)
	{
		super();
		this.surfaceHolder = surfaceHolder;
		this.radarPanel = radarPanel;
	}
}
