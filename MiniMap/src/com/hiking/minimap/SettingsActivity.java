package com.hiking.minimap;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.app.Activity;
import android.view.Menu;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_settings);
		getFragmentManager().beginTransaction().replace(android.R.id.content,new PreferenceFragment()
		{
			public void onCreate(Bundle savedInstanceState) 
		    {
		    	super.onCreate(savedInstanceState);
		    	addPreferencesFromResource(R.xml.settings);
		    }
		}).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

}
