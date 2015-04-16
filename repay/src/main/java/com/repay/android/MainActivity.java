package com.repay.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.repay.android.fragment.FriendsListFragment;
import com.repay.android.fragment.SettingsFragment;
import com.repay.lib.manager.DatabaseManager;
import com.repay.model.Person;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class MainActivity extends ActionBarActivity
{
	private DatabaseManager mDB;
	private ArrayList<Person> mPersons;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragmentholder);

		mDB = new DatabaseManager(this);

		getFragmentManager().beginTransaction().replace(R.id.fragment, new FriendsListFragment()).commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_settings:
				Intent intentSettings = new Intent();
				intentSettings.setClass(this, SettingsActivity.class);
				startActivity(intentSettings);
				return true;

			default:
				return false;
		}
	}

	public ArrayList<Person> getFriends()
	{
		return mPersons;
	}

	public void setFriends(ArrayList<Person> persons)
	{
		mPersons = persons;
	}

	public void updateFriends()
	{
		for (Person person : mPersons)
		{
			mDB.updateFriendRecord(person);
		}
	}

	public DatabaseManager getDB()
	{
		return mDB;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		mPersons = mDB.getAllFriends();
		// Sort the list
		Collections.sort(mPersons);
		if (SettingsFragment.getSortOrder(this) == SettingsFragment.SORTORDER_OWETHEM)
		{
			Collections.reverse(mPersons);
		}
	}
}
