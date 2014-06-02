package com.repay.android.adddebt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import com.repay.android.Friend;
import com.repay.android.R;
import com.repay.android.database.DatabaseHandler;
import com.repay.android.settings.SettingsActivity;

import android.content.Intent;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class AddDebtActivity extends FragmentActivity {

	private static final String 		TAG = AddDebtActivity.class.getName();

	private FragmentTransaction 		mFragMan;
	private Fragment 					mChoosePerson, mEnterAmount, mSummary;
	private int 						mFrameLayoutRes;
	private ArrayList<Friend> 			mSelectedFriends;
	private DatabaseHandler 			mDB;
	private String						mDescription;
	private BigDecimal 					mAmount;
	private boolean 					mNegativeDebt = false, 
			mSplitAmount = false, mInclMe = false;
	private ActionBar					mActionBar;

	// Constants for SavedState
	public static final String REPAY_ID = "repay_id";
	public static final String NO_OF_FRIENDS_SELECTED = "friendsSelected";
	public static final String AMOUNT = "amount";
	public static final String DESC_FIELD = "description";
	public static final String IOWETHEM_FIELD = "iOweThem";
	public static final String CURRENT_FRAGMENT = "fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// If the available screen size is that of an average tablet (as defined
		// in the Android documentation) then allow the screen to rotate
		if(getResources().getBoolean(R.bool.lock_orientation)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setSubtitle(R.string.fragment_choosefriend_subtitle);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adddebt);
		mSelectedFriends = new ArrayList<Friend>();
		mDB = new DatabaseHandler(this);

		// Instantiate Fragments
		mChoosePerson = new ChoosePersonFragment();
		mEnterAmount = new EnterAmountFragment();
		mSummary = new DebtSummaryFragment();
		mFrameLayoutRes = R.id.activity_adddebt_framelayout;

		if(getIntent().getExtras()!=null){
			Bundle bundle = getIntent().getExtras();
			if(bundle.containsKey(NO_OF_FRIENDS_SELECTED)){
				try{
					int count = bundle.getInt(NO_OF_FRIENDS_SELECTED);
					mSelectedFriends = new ArrayList<Friend>();
					for(int i=1;i<=count;i++){
						mSelectedFriends.add(mDB.getFriendByRepayID(bundle.getString(REPAY_ID+Integer.toString(i))));
					}
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
			Log.i(TAG, "Friend data from FriendDetailsActivity, not selected from the list");
		}
		else if(savedInstanceState!=null && savedInstanceState.containsKey(NO_OF_FRIENDS_SELECTED)){
			int count = savedInstanceState.getInt(NO_OF_FRIENDS_SELECTED);
			mSelectedFriends = new ArrayList<Friend>();
			for(int i=1;i<=count;i++){
				mSelectedFriends.add(mDB.getFriendByRepayID(savedInstanceState.getString(REPAY_ID+Integer.toString(i))));
			}
		}
		if(savedInstanceState!=null && savedInstanceState.containsKey(AddDebtActivity.AMOUNT)){
			mAmount = new BigDecimal(savedInstanceState.getString(AddDebtActivity.AMOUNT));
		}
		// Show the first fragment
		mFragMan = getSupportFragmentManager().beginTransaction();
        if(savedInstanceState!=null){
            // If there's a saved state then it's an orientation
            // change and replace needs to be called rather than
            // add to stop the fragments from overlapping.
            mFragMan.replace(mFrameLayoutRes, mChoosePerson);
        } else {
            mFragMan.add(mFrameLayoutRes, mChoosePerson);
        }
		mFragMan.commit();
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		if (mSelectedFriends.size()>0) {
			outState.putInt(NO_OF_FRIENDS_SELECTED, mSelectedFriends.size());
			for(int i=0;i<=mSelectedFriends.size()-1;i++){
				outState.putString(REPAY_ID+Integer.toString(i+1), mSelectedFriends.get(i).getRepayID());
			}
		}
		if(mAmount!=null){
			outState.putString(AMOUNT, mAmount.toString());
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(data!=null&&requestCode==1){
			try{
				Log.i(TAG,"Closing contact picker");
				Uri contactUri = data.getData();
				String[] cols = {ContactsContract.Contacts.DISPLAY_NAME};
				Cursor cursor = getContentResolver().query(contactUri, cols, null, null, null);
				cursor.moveToFirst();
				String result = cursor.getString(0).replaceAll("[-+.^:,']","");
				Friend pickerResult = new Friend(DatabaseHandler.generateRepayID(), contactUri, result, new BigDecimal("0"));
				mDB.addFriend(pickerResult);
				((ChoosePersonFragment)mChoosePerson).dataSetChanged();
			} catch (IndexOutOfBoundsException e){
				Toast.makeText(this, "Problem in getting result from your contacts", Toast.LENGTH_SHORT).show();
			} catch (SQLException e){
				e.printStackTrace();
				AlertDialog alert = new AlertDialog.Builder(this).create();
				alert.setMessage("This person already exists in Repay");
				alert.setTitle("Person Already Exists");
				Log.i(TAG, "Person already exists within app database");
				alert.show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.adddebt, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_settings:
			Intent intent = new Intent();
			intent.setClass(this, SettingsActivity.class);
			startActivity(intent);
			return true;

		default:
			break;
		}
		return false;
	}

	public void onNextBtn(View v){
		switch (v.getId()) {
		case R.id.activity_friendchooser_donebtn:
			mSelectedFriends = ((ChoosePersonFragment)mChoosePerson).getSelectedFriends(); // Get the friends that were selected
			if(mSelectedFriends!=null && mSelectedFriends.size()>=1){
				Log.i(TAG, Integer.toString(mSelectedFriends.size())+" people selected");
				mFragMan = getSupportFragmentManager().beginTransaction();
				mFragMan.replace(mFrameLayoutRes, mEnterAmount);
				mFragMan.addToBackStack("friends");
				mFragMan.commit();
				mActionBar.setSubtitle(R.string.fragment_enteramount_subtitle);
				mActionBar.setTitle(R.string.fragment_enteramount_title);
			} else {
				Toast.makeText(this, "Please choose 1 or more people first", Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.fragment_enterdebtamount_donebtn:
			mAmount = ((EnterAmountFragment)mEnterAmount).getAmount(); // Get the amount entered
			if(mAmount.compareTo(BigDecimal.ZERO)>0){
				Log.i(TAG,"Entered amount: "+mAmount.toString());
				mFragMan = getSupportFragmentManager().beginTransaction();
				mFragMan.replace(mFrameLayoutRes, mSummary);
				mFragMan.addToBackStack("amount");
				mFragMan.commit();
				mActionBar.setSubtitle(null);
				mActionBar.setTitle(R.string.fragment_debtsummary_title);
			} else {
				Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.fragment_debtsummary_donebtn:
			mDescription = ((DebtSummaryFragment)mSummary).getDescription();
			mNegativeDebt = ((DebtSummaryFragment)mSummary).isNegative();
			mSplitAmount = ((DebtSummaryFragment)mSummary).isSplitEvenly();
			mInclMe = ((DebtSummaryFragment)mSummary).isIncludingMe();
			submitToDB();
			break;

		default:
			break;
		}
	}

	/**
	 * @return Pass database manager to fragments that may need it
	 */
	public DatabaseHandler getDB(){
		if(mDB==null){
			mDB = new DatabaseHandler(this);
		}
		return mDB;
	}

	public BigDecimal getAmount(){
		return mAmount;
	}

	public ArrayList<Friend> getSelectedFriends(){
		return mSelectedFriends;
	}

	private void submitToDB(){
		try {
			if(mSelectedFriends.size()==0){
				throw new NullPointerException();

			} else if (mSelectedFriends.size()==1){
				BigDecimal latestAmount = new BigDecimal(mAmount.toString());
				if(mNegativeDebt){
					latestAmount = latestAmount.negate();
				}
				if(mSplitAmount){
					latestAmount = latestAmount.divide(BigDecimal.valueOf(2));
				}
				mDB.addDebt(mSelectedFriends.get(0).getRepayID(), 
						latestAmount, mDescription);
				mSelectedFriends.get(0).setDebt(mSelectedFriends.get(0).getDebt().add(latestAmount));
				mDB.updateFriendRecord(mSelectedFriends.get(0));

			} else if(mSelectedFriends.size()>1){
				BigDecimal debtPerPerson;
				if(mSplitAmount){
					int numOfPeeps = mSelectedFriends.size();
					Log.i(TAG,Integer.toString(numOfPeeps)+" people selected");
					numOfPeeps+=(mInclMe)?1:0; // If is including me then add me into this
					Log.i(TAG,"isIncludingMe="+Boolean.toString(mInclMe));
					debtPerPerson = new BigDecimal(mAmount.toString());
					debtPerPerson = debtPerPerson.divide(new BigDecimal(numOfPeeps), RoundingMode.CEILING);
				} else {
					debtPerPerson = new BigDecimal(mAmount.toString());
				}
				if(mNegativeDebt){
					debtPerPerson = debtPerPerson.negate();
				}
				for(int i=0;i<=mSelectedFriends.size()-1;i++){
					mDB.addDebt(mSelectedFriends.get(i).getRepayID(), 
							debtPerPerson, mDescription);
					mSelectedFriends.get(i).setDebt(mSelectedFriends.get(i).getDebt().add(debtPerPerson));
					mDB.updateFriendRecord(mSelectedFriends.get(i));
				}
			}
			requestBackup();
			finish();
		} catch (SQLException e) {
			Toast.makeText(this, "Database Error", Toast.LENGTH_SHORT).show();
		} catch (NullPointerException e){
			Toast.makeText(this, "Select a person and enter an amount first!", Toast.LENGTH_SHORT).show();
		} catch (NumberFormatException e){
			Toast.makeText(this, "Please enter a valid amount!", Toast.LENGTH_SHORT).show();
		} catch (ArithmeticException e){
			Toast.makeText(this, "You can't divide this number between this many people", 
					Toast.LENGTH_SHORT).show();
		}
	}

	public void requestBackup() {
		BackupManager bm = new BackupManager(this);
		bm.dataChanged();
	}
}
