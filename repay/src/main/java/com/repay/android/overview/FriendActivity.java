package com.repay.android.overview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.repay.android.R;
import com.repay.android.SettingsActivity;
import com.repay.android.debtwizard.AddDebtActivity;
import com.repay.android.debtwizard.DebtActivity;
import com.repay.android.debtwizard.RepayDebtActivity;
import com.repay.android.fragment.SettingsFragment;
import com.repay.android.helper.ContactsContractHelper;
import com.repay.android.manager.DatabaseManager;
import com.repay.android.model.Friend;
import com.repay.android.overview.fragment.FriendFragment;
import com.repay.android.overview.fragment.FriendHistoryFragment;

import java.math.BigDecimal;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class FriendActivity extends ActionBarActivity implements View.OnClickListener
{
	public static final String FRIEND = "friend";
	private static final int PICK_CONTACT_REQUEST = 1;
	private Friend mFriend;
	private DatabaseManager mDB;
	private FriendFragment mOverViewFrag, mDebtHistoryFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frienddetails);
		mDB = new DatabaseManager(this);
		mFriend = (Friend)getIntent().getExtras().get(FRIEND);
		mDebtHistoryFrag = new FriendHistoryFragment();
		getFragmentManager()
			.beginTransaction()
			.replace(R.id.history, mDebtHistoryFrag)
			.commit();
	}

	public DatabaseManager getDB()
	{
		return mDB;
	}

	private void updateFragments()
	{
		if (mOverViewFrag != null) mOverViewFrag.onFriendUpdated(mFriend);
		if (mDebtHistoryFrag != null) mDebtHistoryFrag.onFriendUpdated(mFriend);
	}

	public Friend getFriend()
	{
		return mFriend;
	}

	public void updateFriend()
	{
		mFriend = mDB.getFriendByRepayID(mFriend.getRepayID());
		updateFragments();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.frienddetails, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_settings:
				Intent intent = new Intent();
				intent.setClass(this, SettingsActivity.class);
				startActivity(intent);
				return true;

			case R.id.action_delete:
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle(R.string.delete);
				dialog.setMessage(R.string.confirm_remove_person);

				dialog.setPositiveButton(R.string.delete, new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						try
						{
							if (mDB != null)
							{
								mDB.removeFriend(mFriend.getRepayID());
								finish();
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});

				dialog.setNegativeButton(R.string.cancel, new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});
				dialog.show();
				return true;

			case R.id.action_info:
				new AlertDialog.Builder(this).setTitle(R.string.info).setMessage(R.string.info_dialog_text).setPositiveButton(R.string.okay, new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				}).show();
				return true;

			case R.id.action_addDebt:
				Intent i = new Intent(this, AddDebtActivity.class);
				i.putExtra(DebtActivity.FRIEND, mFriend);
				startActivity(i);
				return true;

			case R.id.action_reLinkContact:
				Intent getContact = new Intent(Intent.ACTION_PICK);
				getContact.setType(ContactsContract.Contacts.CONTENT_TYPE);
				startActivityForResult(getContact, PICK_CONTACT_REQUEST);
				return true;

			case R.id.action_unLinkContact:
				mFriend = new Friend(mFriend.getRepayID(), null, mFriend.getName(), mFriend.getDebt());
				mDB.updateFriendRecord(mFriend);
				return true;

			default:
				return false;
		}
	}

	private void clearAllDebts()
	{
		AlertDialog.Builder clearDebtDialog = new AlertDialog.Builder(this);
		clearDebtDialog.setTitle(R.string.clear_debt);
		clearDebtDialog.setMessage(R.string.are_you_sure);
		clearDebtDialog.setPositiveButton(R.string.clear_debt, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				try
				{
					BigDecimal debtRepayed = mFriend.getDebt().negate();
					mDB.addDebt(mFriend.getRepayID(), debtRepayed, "Repaid");
					mFriend.setDebt(mFriend.getDebt().add(debtRepayed));
					mDB.updateFriendRecord(mFriend);
					Toast.makeText(getApplicationContext(), "Debt of " + SettingsFragment.getCurrencySymbol(getApplicationContext()) + debtRepayed.toString() + " cleared", Toast.LENGTH_SHORT).show();
					finish();
				}
				catch (Throwable e)
				{
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		});
		clearDebtDialog.setNegativeButton(R.string.cancel, new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		clearDebtDialog.show();
	}

	private void clearPartialDebt()
	{
		Intent i = new Intent(this, RepayDebtActivity.class);
		i.putExtra(DebtActivity.FRIEND, mFriend);
		startActivity(i);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && requestCode == PICK_CONTACT_REQUEST)
		{
			try
			{
				String contactUri = data.getData().toString();
				String displayName = ContactsContractHelper.getNameForContact(this, contactUri);

				Friend pickerResult = new Friend(mFriend.getRepayID(), contactUri, displayName, mFriend.getDebt());

				mDB.updateFriendRecord(pickerResult);
			}
			catch (IndexOutOfBoundsException e)
			{
				e.printStackTrace();
				Toast.makeText(this, "Problem in getting result from your contacts", Toast.LENGTH_SHORT).show();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				Toast.makeText(this, "Unable to add this person to the database", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.repaid:
				if (mFriend.getDebt().compareTo(BigDecimal.ZERO) != 0)
				{
					AlertDialog.Builder clearDebtDialog = new AlertDialog.Builder(this);
					clearDebtDialog.setTitle(R.string.debt_repaid_title);
					clearDebtDialog.setItems(R.array.debt_repaid_items, new OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							if (which == 0)
							{
								clearAllDebts();
							}
							else if (which == 1)
							{
								clearPartialDebt();
							}
						}
					});
					clearDebtDialog.show();
				}
				else
				{
					Toast.makeText(this, "No debts to clear", Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
		}
	}
}
