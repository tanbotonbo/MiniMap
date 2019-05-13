package com.hiking.minimap.waypoints;

import android.graphics.Color;
import android.location.Location;

public class Waypoint 
{
	private String name;
	private String desc;
	private Location location;
	private int color;
	
	public Waypoint(String fullDesc)
	{
		location = new Location("Waypoint Service");
		this.name = fullDesc.substring(0, fullDesc.indexOf("\n"));
		fullDesc = fullDesc.substring(fullDesc.indexOf("\n")+1);
		this.desc = fullDesc.substring(0, fullDesc.indexOf("\n"));
		fullDesc = fullDesc.substring(fullDesc.indexOf("\n") + 1);
		try
		{
			this.location.setLongitude(Double.parseDouble(fullDesc.substring(0, fullDesc.indexOf("\n"))));
			fullDesc = fullDesc.substring(fullDesc.indexOf("\n")+1);
			this.location.setLatitude(Double.parseDouble(fullDesc.substring(0, fullDesc.indexOf("\n"))));
		}
		catch(Exception e)
		{
			this.location.setLongitude(0);
			fullDesc = fullDesc.substring(fullDesc.indexOf("\n")+1);
			this.location.setLatitude(0);
		}
		fullDesc = fullDesc.substring(fullDesc.indexOf("\n")+1);
		this.color = Integer.parseInt(fullDesc);
	}
	
	public Waypoint(Location location, String name, String desc)
	{
		this.location = location;
		this.name = name;
		this.desc = desc;
	}
	
	public String toString()
	{
		return name + "\n" + desc + "\n" + location.getLongitude() + "\n" + location.getLatitude() + "\n" + Integer.toString(color);
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDesc()
	{
		return desc;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public int getColor()
	{
		return color;
	}
}
