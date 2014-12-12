package com.repay.android;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.repay.android.fragment.SettingsFragment;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class SettingsActivity extends ActionBarActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		getFragmentManager().beginTransaction().replace(R.id.settings_FrameLayout, new SettingsFragment()).commit();
	}
}
