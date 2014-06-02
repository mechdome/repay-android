package com.repay.android;

import com.repay.android.adddebt.*;
import com.repay.android.settings.SettingsActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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

public class MainActivity extends FragmentActivity {

	private static final String		TAG = MainActivity.class.getName();
	private Fragment 				mStartFr;
	private FragmentTransaction 	mFragMan;
	private final int 				mFrameLayout = R.id.start_fragmentframe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// If the available screen size is that of an average tablet (as defined
		// in the Android documentation) then allow the screen to rotate
		if(getResources().getBoolean(R.bool.lock_orientation)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Instantiate fragments
		mStartFr = new StartFragment();
		getActionBar().setDisplayShowTitleEnabled(false);
		mFragMan = getSupportFragmentManager().beginTransaction();
        if(savedInstanceState != null){
            mFragMan.replace(mFrameLayout, mStartFr);
        } else {
            mFragMan.add(mFrameLayout, mStartFr);
        }
		mFragMan.commit();
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

		case R.id.action_refresh:
			((StartFragment)mStartFr).updateList();
			return true;

		case R.id.action_total:
			((StartFragment)mStartFr).showTotalDialog();
			return true;

		case R.id.action_recalculateTotals:
			Log.i(TAG, "Recalculating total debts...");
			((StartFragment)mStartFr).recalculateTotals();
			Log.i(TAG, "Finished recalculating debts");
			return true;
		}
		return false;
	}
}
