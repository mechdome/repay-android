package com.repay.android;

import com.repay.android.adddebt.*;
import com.repay.android.settings.SettingsActivity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 */

public class MainActivity extends Activity {

	private static final String		TAG = MainActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().setDisplayShowTitleEnabled(false);
		getFragmentManager().beginTransaction().replace(R.id.start_fragmentframe, new StartFragment()).commit();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){

		case R.id.action_adddebt:
			Intent intent = new Intent();
			intent.setClass(this, AddDebtActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_settings:
			Intent intentSettings = new Intent();
			intentSettings.setClass(this, SettingsActivity.class);
			startActivity(intentSettings);
			return true;
		}
		return false;
	}
}
