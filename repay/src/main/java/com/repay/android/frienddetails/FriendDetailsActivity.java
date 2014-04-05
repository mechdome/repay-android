package com.repay.android.frienddetails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.repay.android.Friend;
import com.repay.android.R;
import com.repay.android.adddebt.*;
import com.repay.android.database.DatabaseHandler;
import com.repay.android.settings.SettingsActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ActionBar.Tab;
import android.app.backup.BackupManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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

public class FriendDetailsActivity extends FragmentActivity implements View.OnClickListener {

	public static final String 		TAG = "FriendDetailsActivity";
	public static final int			AMOUNT_ENTER_REQUEST = 15;
	private static final int 		PICK_CONTACT_REQUEST = 1;
	private Friend 					mFriend;
	private ViewPager 				mTabView;
	private boolean 				mUseTabs=true;
	private TabViewAdapter 			mTabAdapter;
	private List<Fragment> 			mFragments;
	private ActionBar 				mActionBar;
	private DatabaseHandler 		mDB;
	private FragmentTransaction		mFragMan;
	private Fragment 				mOverViewFrag, mDebtHistoryFrag;
	private int 					mInfoMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// If the available screen size is that of an average tablet (as defined
		// in the Android documentation) then allow the screen to rotate
		if(getResources().getBoolean(R.bool.lock_orientation)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frienddetails);
		mDB = new DatabaseHandler(this);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Bundle bundle = getIntent().getExtras();
		try{
			if (bundle != null) {
				mFriend = new Friend(bundle.getString(Friend.REPAYID),
						Uri.parse(bundle.getString(Friend.LOOKUPURI)), bundle.getString(Friend.NAME),
						new BigDecimal(bundle.getString(Friend.AMOUNT)));
			}
		} catch (NullPointerException e){
			Log.i(TAG, "Person added by name, not from contacts list");
			if (bundle != null) {
				mFriend = new Friend(bundle.getString(Friend.REPAYID),
						null, bundle.getString(Friend.NAME),
						new BigDecimal(bundle.getString(Friend.AMOUNT)));
			}
		}

		// Set the message for the info dialog
		mInfoMessage = R.string.activity_debtHistoryInfoDialog_message_debtHistoryInfo;

		// See if we need to use a tab view or just a 2 fragment layout,
		// this boolean is stored in XML, this way I can use the SDK
		// size qualifiers to determine phone or tablet
		mUseTabs = getResources().getBoolean(R.bool.use_tabView);

		// Determine whether this is a tablet or not so
		// we know if we need the tab view or just a couple
		// of FrameLayouts
		if(!mUseTabs){
			mFragMan = getSupportFragmentManager().beginTransaction();
			mOverViewFrag = new FriendOverviewFragment();
			mDebtHistoryFrag = new DebtHistoryFragment();
			mFragMan.add(R.id.activity_frienddetails_frame1, mOverViewFrag);
			mFragMan.add(R.id.activity_frienddetails_frame2, mDebtHistoryFrag);
			mFragMan.commit();

		} else {
			mTabView = (ViewPager)findViewById(R.id.activity_frienddetails_tabView);
			// Get the friend from the bundle passed in the intent

			// Produce the Tab bar
			mActionBar = getActionBar();
			if (mActionBar != null) {
				mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			}
			ViewPager.SimpleOnPageChangeListener mViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					super.onPageSelected(position);
					// Find the ViewPager Position
					mActionBar.setSelectedNavigationItem(position);
				}
			};
			mTabView.setOnPageChangeListener(mViewPagerListener);
			// Set the ViewPager animation
			mTabView.setPageTransformer(true, new DepthPageTransformer());
			// Capture tab button clicks
			ActionBar.TabListener tabListener = new ActionBar.TabListener() {

				@Override
				public void onTabReselected(Tab tab,
						android.app.FragmentTransaction ft) {
					// Not needed
				}

				@Override
				public void onTabSelected(Tab tab,
						android.app.FragmentTransaction ft) {
					mTabView.setCurrentItem(tab.getPosition());
				}

				@Override
				public void onTabUnselected(Tab tab,
						android.app.FragmentTransaction ft) {
					// Not needed
				}
			};

			// Create first Tab
			Tab mTab1 = mActionBar.newTab().setText(mFriend.getName()).setTabListener(tabListener);
			mActionBar.addTab(mTab1);

			// Create second Tab
			Tab mTab2 = mActionBar.newTab().setText("Debts").setTabListener(tabListener);
			mActionBar.addTab(mTab2);

			// Add Tabs to ViewPager
			mFragments = new ArrayList<Fragment>();
			mFragments.add(FriendOverviewFragment.newInstance("Friend Details"));
			mFragments.add(DebtHistoryFragment.newInstance("Debt History"));

			// Instantiate adapter and add it to ViewPager
			mTabAdapter = new TabViewAdapter(getSupportFragmentManager(), mFragments);
			mTabView.setAdapter(mTabAdapter);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mFriend = mDB.getFriendByRepayID(mFriend.getRepayID());
	}

	protected Friend getFriend(){
		return mFriend;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.frienddetails, menu);
		return true;
	}

	private void deleteFriend(){
		try{
			if(mDB!=null){
				mDB.removeFriend(mFriend.getRepayID());
				finish();
			}
		} catch (Throwable e){
			e.printStackTrace();
		}
	}

	public void onDebtTotalUpdated(Friend friend) {
		this.mFriend = friend;
		if(mUseTabs){
			mOverViewFrag = new FriendOverviewFragment();
			mTabAdapter.setItem(0, mOverViewFrag);
		} else {
			((FriendOverviewFragment)mOverViewFrag).updateFriendInfo();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.action_settings:
			Intent intent = new Intent();
			intent.setClass(this, SettingsActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_delete:
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.activity_friendoverview_deletedialog_title);
			dialog.setMessage(R.string.activity_friendoverview_deletedialog_message);

			dialog.setPositiveButton(R.string.activity_friendoverview_deletedialog_yes, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteFriend();
				}
			});

			dialog.setNegativeButton(R.string.activity_friendoverview_deletedialog_no, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog.show();
			return true;

		case R.id.action_info:
			AlertDialog.Builder infoDialog = new AlertDialog.Builder(this);
			infoDialog.setTitle(R.string.activity_debtHistoryInfoDialog_title);
			infoDialog.setMessage(mInfoMessage);
			infoDialog.setPositiveButton(R.string.activity_debtHistoryInfoDialog_okayBtn, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			infoDialog.show();
			return true;

		case R.id.action_addDebt:
			Intent i = new Intent();
			i.setClass(this, AddDebtActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(AddDebtActivity.NO_OF_FRIENDS_SELECTED, 1);
			bundle.putString(AddDebtActivity.REPAY_ID+"1", mFriend.getRepayID());
			i.putExtras(bundle);
			startActivity(i);
			return true;
			
		case R.id.action_reLinkContact:
				Intent pickContactIntent = new Intent(Intent.ACTION_GET_CONTENT);
				pickContactIntent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
				startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
			return true;
			
		case R.id.action_unLinkContact:
			mFriend = new Friend(mFriend.getRepayID(), Uri.parse(""), mFriend.getName(), mFriend.getDebt());
			mDB.updateFriendRecord(mFriend);
			return true;
		}

		return false;
	}
	
	private void clearAllDebts(){
		AlertDialog.Builder clearDebtDialog = new AlertDialog.Builder(this);
		clearDebtDialog.setTitle(R.string.activity_friendoverview_clearDebtDialog_title);
		clearDebtDialog.setMessage(R.string.activity_friendoverview_clearDebtDialog_message);
		clearDebtDialog.setPositiveButton(R.string.activity_friendoverview_clearDebtDialog_yes, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					BigDecimal debtRepayed = mFriend.getDebt().negate();
					mDB.addDebt(mFriend.getRepayID(), debtRepayed, "Repaid");
					mFriend.setDebt(mFriend.getDebt().add(debtRepayed));
					mDB.updateFriendRecord(mFriend);
					Toast.makeText(getApplicationContext(), "Debt of \u00A3"+debtRepayed.toString()+" cleared", Toast.LENGTH_SHORT).show();
					requestBackup();
					finish();
				} catch (Throwable e){
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}
		});
		clearDebtDialog.setNegativeButton(R.string.activity_friendoverview_clearDebtDialog_no, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		clearDebtDialog.show();
	}
	
	private void clearPartialDebt(){
		Intent i = new Intent(this, RepayDebtActivity.class);
		Bundle b = new Bundle();
		b.putString(AddDebtActivity.REPAY_ID, mFriend.getRepayID());
		i.putExtras(b);
		startActivity(i);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(data!=null&&requestCode==PICK_CONTACT_REQUEST){
			try{
				Uri contactUri = data.getData();
				String[] cols = {ContactsContract.Contacts.DISPLAY_NAME};
				Cursor cursor = getContentResolver().query(contactUri, cols, null, null, null);
				cursor.moveToFirst();
				String result = cursor.getString(0).replaceAll("[-+.^:,']","");
				Friend pickerResult = new Friend(mFriend.getRepayID(), contactUri, result, mFriend.getDebt());
				mDB.updateFriendRecord(pickerResult);
				requestBackup();
			} catch (IndexOutOfBoundsException e){
				Toast.makeText(this, "Problem in getting result from your contacts", Toast.LENGTH_SHORT).show();
			} catch (SQLException e){
				e.printStackTrace();
				Toast.makeText(this, "Unable to add this person to the database", Toast.LENGTH_SHORT).show();
			}
		}
		else if (data!=null&&requestCode==AMOUNT_ENTER_REQUEST){
			try{
				Bundle b = data.getExtras();
				// Get the amount sent back from the activity
				BigDecimal amount = new BigDecimal(b.getString(AddDebtActivity.AMOUNT));
				// Make the amount negative if their debt is negative
				if(mFriend.getDebt().compareTo(BigDecimal.ZERO)>0){
					amount = amount.negate();
				}
				mDB.addDebt(mFriend.getRepayID(), amount, "Repaid");
				mFriend.setDebt(mFriend.getDebt().add(amount));
				mDB.updateFriendRecord(mFriend);
				requestBackup();
			} catch (Exception e){
				
			}
		}
	}

	public class DepthPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.5f;

		@Override
		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 0) { // [-1,0]
				// Use the default slide transition when moving to the left page
				view.setAlpha(1);
				view.setTranslationX(0);
				view.setScaleX(1);
				view.setScaleY(1);

			} else if (position <= 1) { // (0,1]
				// Fade the page out.
				view.setAlpha(1 - position);

				// Counteract the default slide transition
				view.setTranslationX(pageWidth * -position);

				// Scale the page down (between MIN_SCALE and 1)
				float scaleFactor = MIN_SCALE
						+ (1 - MIN_SCALE) * (1 - Math.abs(position));
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}
	
	public void requestBackup() {
		BackupManager bm = new BackupManager(this);
		bm.dataChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_frienddetails_repaid:
			if(mFriend.getDebt().compareTo(BigDecimal.ZERO)!=0){
				AlertDialog.Builder clearDebtDialog = new AlertDialog.Builder(this);
				clearDebtDialog.setTitle(R.string.fragment_friendoverview_repayDebtDialog_title);
				clearDebtDialog.setItems(R.array.fragment_friendoverview_repayDebtDialog_items, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which==0){
							clearAllDebts();
						}
						else if(which==1){
							clearPartialDebt();
						}
					}
				});
				clearDebtDialog.show();
			} else {
				Toast.makeText(this, "No debts to clear", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
		
	}
}
