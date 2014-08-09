package com.repay.android.frienddetails;

import java.math.BigDecimal;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.repay.android.Application;
import com.repay.android.ContactLookup;
import com.repay.android.model.Friend;
import com.repay.android.R;
import com.repay.android.view.RoundedImageView;
import com.repay.android.settings.SettingsFragment;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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

public class FriendOverviewFragment extends Fragment implements OnClickListener
{
	private RoundedImageView 	mFriendPic;
	private TextView 			mTotalOwed, mTotalOwedPrefix;
	private Friend				mFriend;
	private int 				mTheyOweMeColour, mIOweThemColour;
	private Button 				mShareBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_frienddetails, container, false);
		return view;
	}

	@Override
	public void onStart(){
		super.onStart();
		mShareBtn = (Button)getView().findViewById(R.id.share);
		mFriendPic = (RoundedImageView)getView().findViewById(R.id.friend_image);
		mTotalOwed = (TextView)getView().findViewById(R.id.amount);
		mTotalOwedPrefix = (TextView)getView().findViewById(R.id.owe_status);

		mShareBtn.setOnClickListener(this);

		if(SettingsFragment.getDebtHistoryColourPreference(getActivity())==SettingsFragment.DEBTHISTORY_GREEN_RED){
			mTheyOweMeColour = getActivity().getResources().getColor(R.color.green_debt);
			mIOweThemColour = getActivity().getResources().getColor(R.color.red_debt);
		} else {
			mTheyOweMeColour = getActivity().getResources().getColor(R.color.green_debt);
			mIOweThemColour = getActivity().getResources().getColor(R.color.blue_debt);
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		mFriend = ((FriendActivity)getActivity()).getFriend();
		showFriend();
		if (mFriend.getLookupURI() != null) {
			if (!ContactLookup.hasContactData(getActivity(), Uri.parse(mFriend.getLookupURI()).getLastPathSegment())) {
				mShareBtn.setEnabled(false);
			}
		} else {
			mShareBtn.setEnabled(false);
		}
	}

	private void showFriend(){
		// Get image
		ImageLoader.getInstance().displayImage(mFriend.getLookupURI(), mFriendPic, Application.getImageOptions());


		if(mFriend.getDebt().compareTo(BigDecimal.ZERO) == 0){
			mTotalOwedPrefix.setText(""); // Set it null
			mTotalOwed.setText(SettingsFragment.getCurrencySymbol(getActivity())+"0");
			mFriendPic.setOuterColor(mTheyOweMeColour);
		} else if (mFriend.getDebt().compareTo(BigDecimal.ZERO)<0){
			mTotalOwedPrefix.setText(R.string.i_owe);
			String amount = SettingsFragment.getFormattedAmount(mFriend.getDebt().negate());
			mTotalOwed.setText(SettingsFragment.getCurrencySymbol(getActivity())+amount);
			mFriendPic.setOuterColor(mIOweThemColour);
		} else if(mFriend.getDebt().compareTo(BigDecimal.ZERO)>0){
			mTotalOwedPrefix.setText(R.string.they_owe);
			String amount = SettingsFragment.getFormattedAmount(mFriend.getDebt());
			mTotalOwed.setText(SettingsFragment.getCurrencySymbol(getActivity())+amount);
			mFriendPic.setOuterColor(mTheyOweMeColour);
		}
	}

	public void updateFriendInfo(){
		mFriend = ((FriendActivity) getActivity()).getFriend();
		showFriend();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share:
			if(mFriend.getDebt().compareTo(BigDecimal.ZERO)!=0){
				AlertDialog.Builder shareDialog = new ShareDialog(getActivity(), mFriend);
				shareDialog.show();
			} else {
				Toast.makeText(getActivity(), "There's no debt between you?", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
}
