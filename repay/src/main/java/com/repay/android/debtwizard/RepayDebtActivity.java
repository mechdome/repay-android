package com.repay.android.debtwizard;

import android.os.Bundle;
import android.view.View;

import com.repay.android.R;
import com.repay.android.debtwizard.fragment.DebtFragment;
import com.repay.android.debtwizard.fragment.EnterAmountFragment;

import java.math.BigDecimal;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 * <p/>
 * This class will only show the EnterAmountFragment so that
 * I can get the partial amount the person has repaid. Calling
 * the other fragment is unnecessary.
 */

public class RepayDebtActivity extends DebtActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fragmentholder);

		mBuilder.setDescription(DEBT_REPAID_TEXT);

		getFragmentManager().beginTransaction().replace(R.id.fragment, new EnterAmountFragment()).commit();
	}

	@Override
	public void onNextButtonClick(View v)
	{
		switch (v.getId())
		{
			case R.id.fragment_enterdebtamount_donebtn:
				((DebtFragment)getFragmentManager().findFragmentById(R.id.fragment)).saveFields();
				save();
				break;

			default:
				break;
		}
	}

	@Override
	public void save()
	{
		if (mBuilder.getSelectedFriends().get(0).getDebt().compareTo(BigDecimal.ZERO) > 0)
		{
			mBuilder.setInDebtToMe(false); // Negate because I don't want add with a negative
		}
		super.save();
	}
}
