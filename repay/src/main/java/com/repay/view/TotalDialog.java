package com.repay.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.repay.android.R;
import com.repay.android.fragment.SettingsFragment;
import com.repay.model.Person;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * // TODO: Add class description
 *
 * @author Matt Allen
 * @project Repay
 */
public class TotalDialog extends DialogFragment
{
	private ArrayList<Person> mPersons;
	private Context mContext;

	public TotalDialog(Context context, ArrayList<Person> friendsList)
	{
		mContext = context;
		mPersons = friendsList;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		BigDecimal theyOwe = getTheyOweTotal();
		BigDecimal iOwe = getIOweTotal();
		View view = LayoutInflater.from(mContext).inflate(R.layout.total_dialog, null, false);
		((TextView) view.findViewById(R.id.they_owe_amount)).setText(SettingsFragment.getCurrencySymbol(mContext) + SettingsFragment.getFormattedAmount(theyOwe));
		((TextView) view.findViewById(R.id.i_owe_amount)).setText(SettingsFragment.getCurrencySymbol(mContext) + SettingsFragment.getFormattedAmount(iOwe.negate()));
		((TextView) view.findViewById(R.id.total_amount)).setText(SettingsFragment.getCurrencySymbol(mContext) + SettingsFragment.getFormattedAmount(theyOwe.add(iOwe)));
		return new AlertDialog.Builder(mContext).setPositiveButton("Close", null).setView(view).create();
	}

	public BigDecimal getTheyOweTotal()
	{
		BigDecimal amount = new BigDecimal("0");
		for (Person mPerson : mPersons)
		{
			if (mPerson.getDebt().compareTo(BigDecimal.ZERO) == 1)
				amount = amount.add(mPerson.getDebt());
		}
		return amount;
	}

	public BigDecimal getIOweTotal()
	{
		BigDecimal amount = new BigDecimal("-0");
		for (Person mPerson : mPersons)
		{
			if (mPerson.getDebt().compareTo(BigDecimal.ZERO) == -1)
				amount = amount.add(mPerson.getDebt());
		}
		return amount;
	}
}
