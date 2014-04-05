package com.repay.android.adddebt;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.repay.android.Friend;
import com.repay.android.R;
import com.repay.android.RetrieveContactPhoto;
import com.repay.android.RoundedImageView;
import com.repay.android.settings.SettingsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class DebtSummaryFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {

	private static final String 			TAG = DebtSummaryFragment.class.getName();

	private ArrayList<Friend> 				mSelectedFriends;
	private RoundedImageView				mHeaderPic;
	private BigDecimal 						mAmount;
	private TextView 						mNamesTxt, mIOweThem, mTheyOweMe, mAmountTxt;
	private EditText						mDescriptionField;
	private ProgressBar 					mProgress;
	private CheckBox 						mSplitEvenly, mInclMe;
	private boolean 						isNegative = false;
	private int 							mTheyOweMeColour, mIOweThemColour;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_debtsummary, container, false);
	}

	@Override
	public void onStart(){
		super.onStart();
		mProgress = (ProgressBar)getView().findViewById(R.id.fragment_debtsummary_headerLoading);
		mDescriptionField = (EditText)getView().findViewById(R.id.fragment_debtsummary_description);
		mSplitEvenly = (CheckBox)getView().findViewById(R.id.fragment_debtsummary_splitAmount);
		mIOweThem = (TextView)getView().findViewById(R.id.fragment_debtsummary_iOweThem);
		mTheyOweMe = (TextView)getView().findViewById(R.id.fragment_debtsummary_theyOweMe);
		mAmountTxt = (TextView)getView().findViewById(R.id.fragment_debtsummary_amount);
		mHeaderPic = (RoundedImageView)getView().findViewById(R.id.fragment_debtsummary_headerPic);
		mNamesTxt = (TextView)getView().findViewById(R.id.fragment_debtsummary_headerName);
		mInclMe = (CheckBox)getView().findViewById(R.id.fragment_debtsummary_inclMe);

		mProgress.setVisibility(ProgressBar.INVISIBLE); // Not needed yet
		mInclMe.setVisibility(CheckBox.INVISIBLE); 		// Hide all of the things
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

		if(getActivity().getClass()==AddDebtActivity.class){
			setAmount(((AddDebtActivity)getActivity()).getAmount());
			setSelectedFriends(((AddDebtActivity)getActivity()).getSelectedFriends());
		} else {
			setAmount(((EditDebtActivity)getActivity()).getAmount());
			setSelectedFriends(((EditDebtActivity)getActivity()).getSelectedFriends());
			mSplitEvenly.setVisibility(CheckBox.INVISIBLE);
		}
	}

	private void setOweStatusColour(boolean isNegative){
		if(!isNegative){
			mTheyOweMe.setBackgroundColor(getActivity().getResources().getColor(mTheyOweMeColour));
			mIOweThem.setBackgroundColor(getActivity().getResources().getColor(R.color.main_background_slightlyDarker));
		} else {
			mTheyOweMe.setBackgroundColor(getActivity().getResources().getColor(R.color.main_background_slightlyDarker));
			mIOweThem.setBackgroundColor(getActivity().getResources().getColor(mIOweThemColour));
		}
	}

	public void setAmount(BigDecimal amount){
		this.mAmount = amount;
		if(mAmount.compareTo(BigDecimal.ZERO)<0){
			isNegative = true;
		} else {
			isNegative = false;
		}
		setOweStatusColour(isNegative);
		mAmountTxt.setText(SettingsFragment.getCurrencySymbol(getActivity())+mAmount.toString());
	}

	public void setSelectedFriends(ArrayList<Friend> friends){
		this.mSelectedFriends = friends;
		if(mSelectedFriends!=null && mSelectedFriends.size()>=1){
			new RetrieveContactPhoto(mSelectedFriends.get(0).getLookupURI(), mHeaderPic, 
					getActivity(), R.drawable.friend_image_dark).execute();
			if(mSelectedFriends.size()==1){
				mNamesTxt.setText(mSelectedFriends.get(0).getName());
				mSplitEvenly.setText("Split amount evenly between us");
			}
			else if(mSelectedFriends.size()==2){
				mSplitEvenly.setVisibility(CheckBox.VISIBLE);
				mNamesTxt.setText(mSelectedFriends.get(0).getName()+
						",\n"+mSelectedFriends.get(1).getName());
				mSplitEvenly.setText("Split amount evenly between everyone");
			} else {
				mSplitEvenly.setVisibility(CheckBox.VISIBLE);
				mNamesTxt.setText(mSelectedFriends.get(0).getName()+
						",\nand "+Integer.toString(mSelectedFriends.size()-1)+" more");
				mSplitEvenly.setText("Split amount evenly between everyone");
			}
		}
	}

	public boolean isSplitEvenly(){
		return mSplitEvenly.isChecked();
	}

	public boolean isIncludingMe(){
		return mInclMe.isChecked();
	}

	/**
	 * @return true if the user specifies that they owe the other party(s)
	 */
	public boolean isNegative(){
		return isNegative;
	}

	public String getDescription(){
		return mDescriptionField.getText().toString();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_debtsummary_iOweThem:
			isNegative = true;
			Log.i(TAG,"I-Owe-Them button pressed");
			setOweStatusColour(isNegative);
			break;

		case R.id.fragment_debtsummary_theyOweMe:
			isNegative = false;
			Log.i(TAG,"They-Owe-Me button pressed");
			setOweStatusColour(isNegative);
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView.getId()==R.id.fragment_debtsummary_splitAmount && mSelectedFriends.size()>1){
			if(isChecked){
				mInclMe.setVisibility(CheckBox.VISIBLE);
			} else {
				mInclMe.setVisibility(CheckBox.INVISIBLE);
			}
		}
	}
}
