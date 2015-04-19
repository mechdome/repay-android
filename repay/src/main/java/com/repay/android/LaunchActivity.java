package com.repay.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.repay.android.migration.MigrationActivity;
import com.repay.lib.manager.DatabaseManager;

/**
 * Created by Matt Allen
 * 19/04/15
 * mattallen092@gmail.com
 *
 * Simple Activity to check if app needs to launch into migration
 * activity for moving data from SQLiteDB to file storage
 */
public class LaunchActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		DatabaseManager mDB = new DatabaseManager(this);
		if (mDB.getNumberOfPeople() == 0)
		{
			startActivity(new Intent(this, MainActivity.class));
		}
		else
		{
			startActivity(new Intent(this, MigrationActivity.class));
		}
	}
}
