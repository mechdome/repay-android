package com.repay.android.migration;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.repay.android.R;

/**
 * Created by Matt Allen
 * 19/04/15
 * mattallen092@gmail.com
 */
public class MigrationActivity extends ActionBarActivity implements OnMigrationFinishedListener
{
	private static final String FRAGMENT = "fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragmentholder);

		if (getFragmentManager().findFragmentByTag(FRAGMENT) == null)
		{
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.fragment, new MigrationStartFragment(), FRAGMENT)
					.commit();
		}
	}

	@Override
	public void onMigrationFinished()
	{
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment, new MigrationFinishFragment(), FRAGMENT)
				.commit();
	}
}
