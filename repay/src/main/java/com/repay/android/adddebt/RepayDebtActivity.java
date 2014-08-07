package com.repay.android.adddebt;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.repay.android.model.Debt;
import com.repay.android.model.Friend;
import com.repay.android.R;
import com.repay.android.database.DatabaseHandler;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 * This class will only show the EnterAmountFragment so that
 * I can get the partial amount the person has repaid. Calling
 * the other fragment is unnecessary.
 *
 */

public class RepayDebtActivity extends DebtActivity
{
	private Friend						mFriend;
	private DebtFragment				mEnterAmount;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_adddebt);
		
		if(getIntent().hasExtra(FRIEND))
		{
			mFriend = (Friend) getIntent().getExtras().get(FRIEND);
			mBuilder.addSelectedFriend(mFriend);
		}
		else
		{
			Toast.makeText(this, "Friend not found", Toast.LENGTH_SHORT).show();
			finish();
		}

		mBuilder.setDescription(DEBT_REPAID_TEXT);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(true);

		// Instantiate fragment
		mEnterAmount = new EnterAmountFragment();
		
		getFragmentManager().beginTransaction().replace(R.id.activity_adddebt_framelayout, mEnterAmount).commit();
	}

	@Override
	public void onNextButtonClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_enterdebtamount_donebtn:
			mEnterAmount.saveFields();
			save();
			break;

		default:
			break;
		}
	}

	@Override
	public void save() {
		if(mFriend.getDebt().compareTo(BigDecimal.ZERO) > 0){
			mBuilder.setInDebtToMe(false); // Negate because I don't want add with a negative
		}
		super.save();
	}
}
