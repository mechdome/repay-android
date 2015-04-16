package com.repay.view.holder;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

import com.repay.android.R;
import com.repay.android.fragment.SettingsFragment;
import com.repay.model.Debt;

import java.math.BigDecimal;

/**
 * @author Matt Allen
 * @project Repay
 */
public class DebtViewHolder extends ViewHolder
{
	private TextView mAmount;
	private TextView mDescription;
	private TextView mDate;

	public DebtViewHolder(View itemView)
	{
		super(itemView);
		mAmount = (TextView) itemView.findViewById(R.id.amount);
		mDescription = (TextView) itemView.findViewById(R.id.description);
		mDate = (TextView) itemView.findViewById(R.id.date);
	}

	public void populateView(Debt model)
	{
		if (model.getAmount().compareTo(BigDecimal.ZERO) == 1)
		{
			mAmount.setTextColor(SettingsFragment.getPositiveDebtColourPreference(mAmount.getContext()));
			mAmount.setText(SettingsFragment.getCurrencySymbol(mAmount.getContext()) + SettingsFragment.getFormattedAmount(model.getAmount()));
		} else if (model.getAmount().compareTo(BigDecimal.ZERO) == -1)
		{
			mAmount.setTextColor(SettingsFragment.getNegativeDebtColourPreference(mAmount.getContext()));
			mAmount.setText(SettingsFragment.getCurrencySymbol(mAmount.getContext()) + SettingsFragment.getFormattedAmount(model.getAmount().negate()));
		} else
		{
			mAmount.setTextColor(mAmount.getContext().getResources().getColor(R.color.primary_text));
			mAmount.setText(SettingsFragment.getCurrencySymbol(mAmount.getContext()) + SettingsFragment.getFormattedAmount(model.getAmount()));
		}

		if (!TextUtils.isEmpty(model.getDescription()))
		{
			mDescription.setText(model.getDescription());
		} else
		{
			mDescription.setText("No Description");
		}
		mDate.setText(SettingsFragment.getFormattedDate(mDate.getContext(), model.getDate()));
	}

	public void setOnLongClick(OnLongClickListener listener)
	{
		itemView.setOnLongClickListener(listener);
	}
}
