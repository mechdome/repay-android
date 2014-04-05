package com.repay.android.adddebt;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.app.ActionBar;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.repay.android.Friend;
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

public class RepayDebtActivity extends DebtActivity {
	
	private static final String			TAG = RepayDebtActivity.class.getName();
	
	private BigDecimal					mAmount;
	private final String				mDescription = "Repaid";
	private Friend						mFriend;
	private DatabaseHandler				mDB;
	private FragmentTransaction			mFragMan;
	private Fragment					mEnterAmount;
	private ActionBar					mActionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adddebt);
		mDB = new DatabaseHandler(this);
		// If the available screen size is that of an average tablet (as defined
		// in the Android documentation) then allow the screen to rotate
		if(getResources().getBoolean(R.bool.lock_orientation)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		if(getIntent().getExtras()!=null){
			Bundle bundle = getIntent().getExtras();
			try{
				mFriend = mDB.getFriendByRepayID(bundle.getString(AddDebtActivity.REPAY_ID));
			} catch(Exception e){
				Log.e(TAG,"Exception occurred when trying to retrieve friend details. Ending activity");
				e.printStackTrace();
				finish();
			}
		}
		
		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setSubtitle(R.string.fragment_enteramount_subtitle);
		mActionBar.setTitle(R.string.fragment_enteramount_title);
		// Instantiate fragment
		mEnterAmount = new EnterAmountFragment();
		
		mFragMan = getSupportFragmentManager().beginTransaction();
		mFragMan.add(R.id.activity_adddebt_framelayout, mEnterAmount);
		mFragMan.commit();
	}

	@Override
	public BigDecimal getAmount() {
		return mAmount;
	}

	@Override
	public void onNextBtn(View v) {
		switch (v.getId()) {
		case R.id.fragment_enterdebtamount_donebtn:
			mAmount = ((EnterAmountFragment)mEnterAmount).getAmount();
			submitToDB();
			break;

		default:
			break;
		}
	}

	@Override
	public ArrayList<Friend> getSelectedFriends() {
		ArrayList<Friend> friends = new ArrayList<Friend>();
		friends.add(mFriend);
		return friends;
	}

	@Override
	protected void submitToDB() {
		if(mFriend.getDebt().compareTo(BigDecimal.ZERO)>0){
			mAmount = mAmount.negate(); // Negate because I don't want add with a negative
		}
		mDB.addDebt(mFriend.getRepayID(), mAmount, mDescription);
		mFriend.setDebt(mFriend.getDebt().add(mAmount)); // Add amount entered to what's stored
		mDB.updateFriendRecord(mFriend); // Push changes to database
		finish(); // Return to friend overview
	}

	@Override
	public void setAmount(BigDecimal amount) {
		mAmount = amount;
	}

	@Override
	public void setSelectedFriends(ArrayList<Friend> friends) {
		Log.e(TAG,"setSelectedFriends() called. This should not be used in this activity");
	}

}
