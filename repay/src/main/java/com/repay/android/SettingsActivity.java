package com.repay.android;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
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
	private GoogleApiClient mGoogleApiClient;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragmentholder);
		getFragmentManager().beginTransaction().replace(R.id.fragment, new SettingsFragment()).commit();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Drive.API)
				.addScope(Drive.SCOPE_FILE)
				.build();
	}

	public GoogleApiClient getGoogleApiClient()
	{
		return mGoogleApiClient;
	}
}
