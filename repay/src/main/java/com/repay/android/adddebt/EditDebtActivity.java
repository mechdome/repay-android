package com.repay.android.adddebt;

import java.math.BigDecimal;

import com.repay.android.model.Debt;
import com.repay.android.model.Friend;
import com.repay.android.R;

import android.os.Bundle;
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

public class EditDebtActivity extends DebtActivity
{
	public static final String 			DEBT = "debt";
	public static final String 			FRIEND = "friend";

	private DebtFragment 				mEnterAmount, mSummary;
	private int 						mFrame;
	private Debt						mDebt;
	private Friend						mFriend;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adddebt);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(true);

		// Instantiate Fragments
		mEnterAmount = new EnterAmountFragment();
		mSummary = new DebtSummaryFragment();

		if (getIntent().hasExtra(DEBT) && getIntent().hasExtra(FRIEND))
		{
			mDebt = (Debt) getIntent().getExtras().get(DEBT);
			mFriend = (Friend)  getIntent().getExtras().get(FRIEND);
		}

		mFrame = R.id.activity_adddebt_framelayout;

		// Show fragment
		getFragmentManager().beginTransaction().replace(mFrame, mEnterAmount).commit();
	}

	public void onNextButtonClick(View v){
		switch (v.getId()) {
		case R.id.fragment_enterdebtamount_donebtn:
			mEnterAmount.saveFields();
			if (getDebtBuilder().getAmount().compareTo(BigDecimal.ZERO) > 0)
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
		}
	}

	@Override
	public void save()
	{
		mDB.updateDebt(mDebt);
		mDB.updateFriendRecord(mFriend);
		finish();
	}
}
