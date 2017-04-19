package com.repay.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mechdome.aboutmechdome.AboutMechDomeActivity;
import com.repay.android.adddebt.AddDebtActivity;
import com.repay.android.database.DatabaseHandler;
import com.repay.android.model.Friend;
import com.repay.android.settings.SettingsActivity;
import com.repay.android.settings.SettingsFragment;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class MainActivity extends Activity
{
	private DatabaseHandler			mDB;
	private ArrayList<Friend>		mFriends;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().setDisplayShowTitleEnabled(false);

		mDB = new DatabaseHandler(this);

		getFragmentManager().beginTransaction().replace(R.id.start_fragmentframe, new StartFragment()).commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
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

			case R.id.action_about_mechdome:
				Intent intentAboutMD = new Intent();
				intentAboutMD.setClass(this, AboutMechDomeActivity.class);
				startActivity(intentAboutMD);
				return true;

			default:
				return false;
		}
	}

	public ArrayList<Friend> getFriends()
	{
		return mFriends;
	}

	public void updateFriends()
	{
		for (Friend friend : mFriends)
		{
			mDB.updateFriendRecord(friend);
		}
	}

	public DatabaseHandler getDB()
	{
		return mDB;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		mFriends = mDB.getAllFriends();
		// Sort the list
		Collections.sort(mFriends);
		if(SettingsFragment.getSortOrder(this) == SettingsFragment.SORTORDER_OWETHEM)
		{
			Collections.reverse(mFriends);
		}
	}
}
