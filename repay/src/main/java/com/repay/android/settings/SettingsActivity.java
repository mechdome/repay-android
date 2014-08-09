package com.repay.android.settings;

import com.repay.android.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 */

public class SettingsActivity extends Activity implements OnSharedPreferenceChangeListener {

	@SuppressWarnings("unused")
	private static final String TAG = SettingsActivity.class.getName();
	private android.app.FragmentTransaction mFragMan;
	private final int mFrameLayout = R.id.settings_FrameLayout;
	private PreferenceFragment mPrefsFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);

		mPrefsFrag = new SettingsFragment();

		mFragMan = getFragmentManager().beginTransaction();
		mFragMan.add(mFrameLayout, mPrefsFrag);
		mFragMan.commit();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
	}
}
