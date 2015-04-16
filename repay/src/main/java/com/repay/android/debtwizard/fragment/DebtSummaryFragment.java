package com.repay.android.debtwizard.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.repay.android.MainApplication;
import com.repay.android.R;
import com.repay.android.debtwizard.DebtActivity;
import com.repay.android.fragment.SettingsFragment;
import com.repay.view.RoundedImageView;

import java.math.BigDecimal;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class DebtSummaryFragment extends DebtFragment implements OnClickListener, OnCheckedChangeListener
{
	private TextView mTheyOweMe;
	private TextView mIOweThem;
	private EditText mDescription;
	private CheckBox mInclMe;
	private int mTheyOweMeColour, mIOweThemColour, mInactiveColor, mNeutralColor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		return inflater.inflate(R.layout.fragment_debtsummary, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		getActivity().setTitle(R.string.summary);

		mDescription = (EditText) getView().findViewById(R.id.description);
		CheckBox mSplitEvenly = (CheckBox) getView().findViewById(R.id.split_amount);
		mIOweThem = (TextView) getView().findViewById(R.id.i_owe_them);
		mTheyOweMe = (TextView) getView().findViewById(R.id.they_owe_me);
		TextView mAmountTxt = (TextView) getView().findViewById(R.id.amount);
		RoundedImageView mHeaderPic = (RoundedImageView) getView().findViewById(R.id.header_pic);
		RoundedImageView mHeaderPic2 = (RoundedImageView) getView().findViewById(R.id.header_pic2);
		RoundedImageView mHeaderPic3 = (RoundedImageView) getView().findViewById(R.id.header_pic3);
		TextView mNamesTxt = (TextView) getView().findViewById(R.id.header_names);
		mInclMe = (CheckBox) getView().findViewById(R.id.incl_me);
		TextView mOverflowText = (TextView) getView().findViewById(R.id.overflow_text);

		mInclMe.setVisibility(CheckBox.INVISIBLE);
		mSplitEvenly.setOnCheckedChangeListener(this);
		mInclMe.setOnCheckedChangeListener(this);
		mTheyOweMe.setOnClickListener(this);
		mIOweThem.setOnClickListener(this);

		mTheyOweMeColour = SettingsFragment.getPositiveDebtColourPreference(getActivity());
		mIOweThemColour = SettingsFragment.getNegativeDebtColourPreference(getActivity());
		mInactiveColor = getActivity().getResources().getColor(R.color.inactive);
		mNeutralColor = SettingsFragment.getNeutralDebtColourPreference(getActivity());

		int numberOfPeople = ((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends().size();
		if (numberOfPeople < 2)
		{
			mSplitEvenly.setVisibility(CheckBox.INVISIBLE);
		}
		if (mNamesTxt != null)
		{
			mNamesTxt.setText(((DebtActivity) getActivity()).getDebtBuilder().getNamesList(false).trim());
		}

		ImageLoader.getInstance().displayImage(((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends().get(0).getLookupURI(), mHeaderPic, MainApplication.getImageOptions());
		setOuterColor(mHeaderPic, ((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends().get(0).getDebt());

		if (numberOfPeople >= 2)
		{
			mHeaderPic2.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends().get(1).getLookupURI(), mHeaderPic2, MainApplication.getImageOptions());
			setOuterColor(mHeaderPic2, ((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends().get(1).getDebt());
		}
		if (numberOfPeople >= 3)
		{
			mHeaderPic3.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends().get(2).getLookupURI(), mHeaderPic3, MainApplication.getImageOptions());
			setOuterColor(mHeaderPic3, ((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends().get(2).getDebt());
		}
		if (numberOfPeople > 3)
		{
			mOverflowText.setVisibility(View.VISIBLE);
			mOverflowText.setText("+" + Integer.toString(numberOfPeople - 3) + "\nmore");
		}

		mAmountTxt.setText(SettingsFragment.getCurrencySymbol(getActivity()) + ((DebtActivity) getActivity()).getDebtBuilder().getAmount().toString());
		setOweStatusColour(((DebtActivity) getActivity()).getDebtBuilder().isInDebtToMe());
	}

	private void setOweStatusColour(boolean isInDebtToMe)
	{
		if (isInDebtToMe)
		{
			mTheyOweMe.setBackgroundColor(mTheyOweMeColour);
			mIOweThem.setBackgroundColor(mInactiveColor);
		} else
		{
			mTheyOweMe.setBackgroundColor(mInactiveColor);
			mIOweThem.setBackgroundColor(mIOweThemColour);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.i_owe_them:
				((DebtActivity) getActivity()).getDebtBuilder().setInDebtToMe(false);
				setOweStatusColour(false);
				break;

			case R.id.they_owe_me:
				((DebtActivity) getActivity()).getDebtBuilder().setInDebtToMe(true);
				setOweStatusColour(true);
				break;

			default:
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if (buttonView.getId() == R.id.split_amount)
		{
			((DebtActivity) getActivity()).getDebtBuilder().setDistributedEvenly(isChecked);
			if (isChecked)
			{
				mInclMe.setVisibility(CheckBox.VISIBLE);
			} else
			{
				mInclMe.setVisibility(CheckBox.INVISIBLE);
			}
		} else if (buttonView.getId() == R.id.incl_me)
		{
			((DebtActivity) getActivity()).getDebtBuilder().setIncludingMe(isChecked);
		}
	}

	@Override
	public void saveFields()
	{
		((DebtActivity) getActivity()).getDebtBuilder().setDescription(mDescription.getText().toString());
	}

	private void setOuterColor(RoundedImageView view, BigDecimal amount)
	{
		boolean usingNeutral = SettingsFragment.isUsingNeutralColour(getActivity());
		if (amount.compareTo(BigDecimal.ZERO) == 1 || amount.compareTo(BigDecimal.ZERO) == 0)
		{
			view.setOuterColor(mTheyOweMeColour);
		} else if (amount.compareTo(BigDecimal.ZERO) == -1)
		{
			view.setOuterColor(mIOweThemColour);
		}
		if (usingNeutral && amount.compareTo(BigDecimal.ZERO) == 0)
		{
			view.setOuterColor(mNeutralColor);
		}
	}
}
