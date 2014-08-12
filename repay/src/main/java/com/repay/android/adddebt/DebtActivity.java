package com.repay.android.adddebt;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.repay.android.database.DatabaseHandler;
import com.repay.android.model.Debt;
import com.repay.android.model.DebtBuilder;
import com.repay.android.model.Friend;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public abstract class DebtActivity extends Activity
{
	public static final String		FRIEND = "friend";
	public static final String		DEBT = "debt";
	public static final String		DEBT_REPAID_TEXT = "Repaid";
	private static final String		DEBT_BUILDER = "builder";

	protected DatabaseHandler mDB;

	protected DebtBuilder mBuilder;

	protected boolean isEditing = false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Do some instantiation here

		if (savedInstanceState != null && savedInstanceState.get(DEBT_BUILDER) != null)
		{
			mBuilder = (DebtBuilder) savedInstanceState.get(DEBT_BUILDER);
		}
		else
		{
			mBuilder = new DebtBuilder();

			if (getIntent().getExtras() != null && getIntent().getExtras().get(FRIEND) != null)
			{
				mBuilder.addSelectedFriend((Friend) getIntent().getExtras().get(FRIEND));
			}

			if (getIntent().getExtras() != null && getIntent().getExtras().get(DEBT) != null)
			{
				Debt debt = (Debt) getIntent().getExtras().get(DEBT);
				mBuilder.setAmount(debt.getAmount());
				mBuilder.setDescription(debt.getDescription());
				mBuilder.setDate(debt.getDate());
			}
		}

		mDB = new DatabaseHandler(this);
	}

	public DebtBuilder getDebtBuilder()
	{
		return mBuilder;
	}

	public DatabaseHandler getDBHandler()
	{
		return mDB;
	}

	public void save()
	{
		if (isEditing)
		{
			// TODO Implement editing of debt
		}
		else
		{
			// Add the debts into the DB
			for (Debt debt : mBuilder.getNewDebts())
			{
				mDB.addDebt(debt.getRepayID(), debt.getAmount(), debt.getDescription());
			}
			// Then update the friend objects
			for (Friend friend : mBuilder.getUpdatedFriends())
			{
				mDB.updateFriendRecord(friend);
			}
		}

		finish(); // Return to friend overview
	}

	public abstract void onNextButtonClick(View v);

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putSerializable(DEBT_BUILDER, mBuilder);
	}
}
