package com.repay.android.adddebt;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.repay.android.model.Debt;
import com.repay.android.model.Friend;
import com.repay.android.R;
import com.repay.android.database.DatabaseHandler;

import android.app.ActionBar;
import android.content.pm.ActivityInfo;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 */

public class EditDebtActivity extends DebtActivity {

	private static final String			TAG = EditDebtActivity.class.getName();

	public static final String 			DEBT_ID = "debt-id";
	public static final String 			REPAY_ID = "repay_id";
	public static final String 			IOWETHEM_FIELD = "iOweThem";

	private FragmentTransaction 		mFragMan;
	private Fragment 					mEnterAmount, mSummary;
	private int 						mFrameLayoutRes;
	private ActionBar					mActionBar;
	private Debt						mDebt;
	private DatabaseHandler				mDB;
	private boolean						mNegate = false;
	private String						mDescription;
	private Friend						mFriend;
	private BigDecimal					mNewAmount;

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
		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setSubtitle(R.string.fragment_enteramount_subtitle);
		mActionBar.setTitle(R.string.fragment_enteramount_title);

		// Instantiate Fragments
		mEnterAmount = new EnterAmountFragment();
		mSummary = new DebtSummaryFragment();

		if(getIntent().getExtras()!=null){
			Bundle bundle = getIntent().getExtras();
			int debtID = bundle.getInt(DEBT_ID);
			try {
				mDebt = mDB.getDebtByIDs(debtID);
				mFriend = mDB.getFriendByRepayID(mDebt.getRepayID());
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG,"Error occured in retreiving debt from sqlite db. Finishing activity");
				finish();
			}
		}

		mFragMan = getSupportFragmentManager().beginTransaction();
		mFrameLayoutRes = R.id.activity_adddebt_framelayout;
		mFragMan.add(mFrameLayoutRes, mEnterAmount);
		mFragMan.commit();
	}

	public BigDecimal getAmount(){
		if(mNewAmount==null){
			return mDebt.getAmount();
		} else {
			return mNewAmount;
		}
	}

	public void onNextBtn(View v){
		switch (v.getId()) {
		case R.id.fragment_enterdebtamount_donebtn:
			mNewAmount = ((EnterAmountFragment)mEnterAmount).getAmount(); // Update Amount
			mFragMan = getSupportFragmentManager().beginTransaction(); // Change to summary fragment
			mFragMan.replace(mFrameLayoutRes, mSummary);
			mFragMan.addToBackStack("summary");
			mFragMan.commit();
			mActionBar.setSubtitle(null);
			mActionBar.setTitle(R.string.fragment_debtsummary_title);
			break;

		case R.id.fragment_debtsummary_donebtn:
			mDescription = ((DebtSummaryFragment)mSummary).getDescription();
			mNegate = ((DebtSummaryFragment)mSummary).isNegative();
			submitToDB();
			break;
		}
	}

	protected void submitToDB(){
		if(mNegate){ // Make negative if owed
			mNewAmount = mNewAmount.negate();
		}
		mDebt.setDescription(mDescription);
		try{
			mFriend.setDebt(mFriend.getDebt().subtract(mDebt.getAmount()));
			mFriend.setDebt(mFriend.getDebt().add(mNewAmount));
			mDebt.setAmount(mNewAmount);
			mDB.updateDebt(mDebt); // Aaand update
			mDB.updateFriendRecord(mFriend); // Update cached total
		} catch (NullPointerException e){
			e.printStackTrace();
		} catch (SQLException e){
			e.printStackTrace();
		} finally {
			finish();
		}
	}

	@Override
	public ArrayList<Friend> getSelectedFriends() {
		ArrayList<Friend> friends = new ArrayList<Friend>();
		friends.add(mFriend);
		return friends;
	}

	@Override
	public void setAmount(BigDecimal amount) {
		mDebt.setAmount(amount);
	}

	@Override
	public void setSelectedFriends(ArrayList<Friend> friends) {
		Log.e(TAG, "Trying to set selected friend in edit mode. Not allowed");
	}
}
