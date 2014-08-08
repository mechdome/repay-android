package com.repay.android.adddebt;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.repay.android.Application;
import com.repay.android.model.Friend;
import com.repay.android.R;
import com.repay.android.view.RoundedImageView;
import com.repay.android.settings.SettingsFragment;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 */

public class DebtSummaryFragment extends DebtFragment implements OnClickListener, OnCheckedChangeListener
{
	private RoundedImageView				mHeaderPic;
	private TextView 						mNamesTxt, mAmountTxt, mTheyOweMe, mIOweThem;
	private EditText						mDescription;
	private CheckBox 						mSplitEvenly, mInclMe;
	private int 							mTheyOweMeColour, mIOweThemColour;

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

		getActivity().getActionBar().setTitle(R.string.fragment_debtsummary_title);
		getActivity().getActionBar().setSubtitle("");

		mDescription = (EditText)getView().findViewById(R.id.fragment_debtsummary_description);
		mSplitEvenly = (CheckBox)getView().findViewById(R.id.fragment_debtsummary_splitAmount);
		mIOweThem = (TextView)getView().findViewById(R.id.fragment_debtsummary_iOweThem);
		mTheyOweMe = (TextView)getView().findViewById(R.id.fragment_debtsummary_theyOweMe);
		mAmountTxt = (TextView)getView().findViewById(R.id.fragment_debtsummary_amount);
		mHeaderPic = (RoundedImageView)getView().findViewById(R.id.fragment_debtsummary_headerPic);
		mNamesTxt = (TextView)getView().findViewById(R.id.fragment_debtsummary_headerName);
		mInclMe = (CheckBox)getView().findViewById(R.id.fragment_debtsummary_inclMe);
		mInclMe.setVisibility(CheckBox.INVISIBLE);

		mSplitEvenly.setOnCheckedChangeListener(this);
		mTheyOweMe.setOnClickListener(this);
		mIOweThem.setOnClickListener(this);

		if(SettingsFragment.getDebtHistoryColourPreference(getActivity())==SettingsFragment.DEBTHISTORY_GREEN_RED){
			mTheyOweMeColour = R.color.green_debt;
			mIOweThemColour = R.color.darkred_debt;
		} else {
			mTheyOweMeColour = R.color.green_debt;
			mIOweThemColour = R.color.blue_debt;
		}

		if (((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends().size() < 2)
		{
			mSplitEvenly.setVisibility(CheckBox.INVISIBLE);
		}
		// TODO Change this to the bool stored in resources
		mNamesTxt.setText(((DebtActivity) getActivity()).getDebtBuilder().getNamesList(true));
		ImageLoader.getInstance().displayImage(((DebtActivity) getActivity()).getDebtBuilder().getImageUri(), mHeaderPic, Application.getImageOptions());
		mAmountTxt.setText(SettingsFragment.getCurrencySymbol(getActivity()) + ((DebtActivity) getActivity()).getDebtBuilder().getAmount().toString());
		setOweStatusColour(((DebtActivity) getActivity()).getDebtBuilder().isInDebtToMe());
	}

	private void setOweStatusColour(boolean isInDebtToMe){
		if(isInDebtToMe){
			mTheyOweMe.setBackgroundColor(getActivity().getResources().getColor(mTheyOweMeColour));
			mIOweThem.setBackgroundColor(getActivity().getResources().getColor(R.color.main_background_slightlyDarker));
		} else {
			mTheyOweMe.setBackgroundColor(getActivity().getResources().getColor(R.color.main_background_slightlyDarker));
			mIOweThem.setBackgroundColor(getActivity().getResources().getColor(mIOweThemColour));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.fragment_debtsummary_iOweThem:
				((DebtActivity) getActivity()).getDebtBuilder().setInDebtToMe(false);
				setOweStatusColour(false);
				break;

			case R.id.fragment_debtsummary_theyOweMe:
				((DebtActivity) getActivity()).getDebtBuilder().setInDebtToMe(true);
				setOweStatusColour(true);
				break;

			default:
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView.getId() == R.id.fragment_debtsummary_splitAmount)
		{
			((DebtActivity) getActivity()).getDebtBuilder().setDistributedEvenly(isChecked);
			if(isChecked){
				mInclMe.setVisibility(CheckBox.VISIBLE);
			} else {
				mInclMe.setVisibility(CheckBox.INVISIBLE);
			}
		}
		else if (buttonView.getId() == R.id.fragment_debtsummary_inclMe)
		{
			((DebtActivity) getActivity()).getDebtBuilder().setIncludingMe(isChecked);
		}
	}

	@Override
	public void saveFields() {
		((DebtActivity) getActivity()).getDebtBuilder().setDescription(mDescription.getText().toString());
	}
}
