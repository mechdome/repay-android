package com.repay.android.adddebt;

import java.math.BigDecimal;

import com.repay.android.R;
import com.repay.android.database.DatabaseHandler;
import com.repay.android.model.Friend;
import com.repay.android.settings.SettingsActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 */

public class AddDebtActivity extends DebtActivity {

	private static final String 		TAG = AddDebtActivity.class.getName();

	private DebtFragment 				mChoosePerson, mEnterAmount, mSummary;
	private int 						mFrame;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setSubtitle(R.string.fragment_choosefriend_subtitle);
		setContentView(R.layout.activity_adddebt);
		mDB = new DatabaseHandler(this);

		// Instantiate Fragments
		mChoosePerson = new ChoosePersonFragment();
		mEnterAmount = new EnterAmountFragment();
		mSummary = new DebtSummaryFragment();
		mFrame = R.id.activity_adddebt_framelayout;

		// Check Intent first
		if(getIntent().hasExtra(FRIEND))
		{
			mBuilder.addSelectedFriend((Friend) getIntent().getExtras().get(FRIEND));
		}

		// Then check the Bundle
		if(savedInstanceState != null){

		}
		// Show the first fragment
		getFragmentManager().beginTransaction().replace(mFrame, mChoosePerson).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.adddebt, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_settings:
			Intent intent = new Intent();
			intent.setClass(this, SettingsActivity.class);
			startActivity(intent);
			return true;

		default:
			break;
		}
		return false;
	}

	public void onNextButtonClick(View v){
		switch (v.getId()) {
		case R.id.activity_friendchooser_donebtn:
			mChoosePerson.saveFields();
			if(getDebtBuilder().getSelectedFriends() != null &&getDebtBuilder().getSelectedFriends().size() > 0)
			{
				Log.i(TAG, Integer.toString(getDebtBuilder().getSelectedFriends().size()) + " people selected");
				getFragmentManager().beginTransaction().replace(mFrame, mEnterAmount).addToBackStack(null).commit();
			}
			else
			{
				Toast.makeText(this, "Please choose 1 or more people first", Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.fragment_enterdebtamount_donebtn:
			mEnterAmount.saveFields();
			if(getDebtBuilder().getAmount().compareTo(BigDecimal.ZERO)>0)
			{
				getFragmentManager().beginTransaction().replace(mFrame, mSummary).addToBackStack(null).commit();
			}
			else
			{
				Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.fragment_debtsummary_donebtn:
			mSummary.saveFields();
			save();
			break;

		default:
			break;
		}
	}
}
