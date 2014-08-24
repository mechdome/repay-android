package com.repay.android.settings;

import android.app.Activity;
import android.os.Bundle;

import com.repay.android.R;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class SettingsActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.settings_activity);

		getFragmentManager().beginTransaction().replace(R.id.settings_FrameLayout, new SettingsFragment()).commit();
	}
}
