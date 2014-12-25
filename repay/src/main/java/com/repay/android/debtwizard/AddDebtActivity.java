package com.repay.android.debtwizard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.repay.android.R;
import com.repay.android.SettingsActivity;
import com.repay.android.debtwizard.fragment.ChoosePersonFragment;
import com.repay.android.debtwizard.fragment.DebtFragment;
import com.repay.android.debtwizard.fragment.DebtSummaryFragment;
import com.repay.android.debtwizard.fragment.EnterAmountFragment;

import java.math.BigDecimal;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 */

public class AddDebtActivity extends DebtActivity
{

	private static final String TAG = AddDebtActivity.class.getName();

	private int mFrame;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adddebt);

		mFrame = R.id.activity_adddebt_framelayout;

		if (getFragmentManager().findFragmentById(mFrame) == null)
		{
			// Show the first fragment
			getFragmentManager()
				.beginTransaction()
				.replace(mFrame, new ChoosePersonFragment())
				.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_settings:
				Intent intent = new Intent();
				intent.setClass(this, SettingsActivity.class);
				startActivity(intent);
				return true;

			default:
				return false;
		}
	}

	public void onNextButtonClick(View v)
	{
		((DebtFragment)getFragmentManager().findFragmentById(mFrame)).saveFields();
		if (((DebtFragment)getFragmentManager().findFragmentById(mFrame)) instanceof ChoosePersonFragment)
		{
			if (getDebtBuilder().getSelectedFriends() != null && getDebtBuilder().getSelectedFriends().size() > 0)
			{
				getFragmentManager().beginTransaction().replace(mFrame, new EnterAmountFragment()).addToBackStack(null).commit();
			}
			else
			{
				Toast.makeText(this, "Please choose 1 or more people first", Toast.LENGTH_SHORT).show();
			}
		}
		else if (((DebtFragment)getFragmentManager().findFragmentById(mFrame)) instanceof EnterAmountFragment)
		{
			if (getDebtBuilder().getAmount().compareTo(BigDecimal.ZERO) > 0)
			{
				getFragmentManager().beginTransaction().replace(mFrame, new DebtSummaryFragment()).addToBackStack(null).commit();
			}
			else
			{
				Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
			}
		}
		else if (((DebtFragment)getFragmentManager().findFragmentById(mFrame)) instanceof DebtSummaryFragment)
		{
			save();
		}
	}
}
