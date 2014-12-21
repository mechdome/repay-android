package com.repay.android.debtwizard.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.repay.android.R;
import com.repay.android.adapter.FriendListAdapter;
import com.repay.android.adapter.OnItemClickListener;
import com.repay.android.debtwizard.DebtActivity;
import com.repay.android.helper.ContactsContractHelper;
import com.repay.android.manager.DatabaseManager;
import com.repay.android.model.Friend;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class ChoosePersonFragment extends DebtFragment implements OnItemClickListener<Friend>
{
	public static final int PICK_CONTACT_REQUEST = 1;
	private static final String TAG = ChoosePersonFragment.class.getName();
	private RecyclerView mListView;
	private RelativeLayout mEmptyState;
	private FriendListAdapter mAdapter;
	private ArrayList<Friend> mFriends;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); // Tell the activity that we have ActionBar items
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inf)
	{
		super.onCreateOptionsMenu(menu, inf);
		if (menu.size() <= 1)
		{
			inf.inflate(R.menu.chooseperson, menu);
		}
	}

	private void showAddFriendDialog()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.enter_friends_name);
		dialog.setItems(new CharSequence[]{"Add From Contacts", "Add A Name"}, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (which == 0)
				{
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
					startActivityForResult(intent, PICK_CONTACT_REQUEST);
				}
				else if (which == 1)
				{
					addFriendByName();
				}
			}
		});
		dialog.show();
	}

	public void addFriendByName()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.enter_friends_name);
		final View v = LayoutInflater.from(getActivity()).inflate(R.layout.add_friend_by_name, null);
		dialog.setView(v);
		dialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String name = ((EditText)v.findViewById(R.id.name_entry)).getText().toString();
				try
				{
					if (!TextUtils.isEmpty(name))
					{
						Friend newFriend = new Friend(DatabaseManager.generateRepayID(), null, name, new BigDecimal("0"));
						((DebtActivity)getActivity()).getDBHandler().addFriend(newFriend);
						new GetFriendsFromDB().execute();
					}
					else
					{
						((EditText)v.findViewById(R.id.name_entry)).setError(getActivity().getResources().getString(R.string.please_enter_name));
					}
				}
				catch (SQLException e)
				{
					Toast.makeText(getActivity(), R.string.friend_could_not_be_added, Toast.LENGTH_SHORT).show();
				}
			}
		});
		dialog.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST)
		{
			try
			{
				String contactUri = data.getData().toString();
				String displayName = ContactsContractHelper.getNameForContact(getActivity(), contactUri);

				Friend pickerResult = new Friend(DatabaseManager.generateRepayID(), contactUri, displayName, new BigDecimal("0"));
				((DebtActivity)getActivity()).getDBHandler().addFriend(pickerResult);

				new GetFriendsFromDB().execute();
			}
			catch (IndexOutOfBoundsException e)
			{
				e.printStackTrace();
				Toast.makeText(getActivity(), R.string.problem_getting_from_contacts, Toast.LENGTH_LONG).show();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				Toast.makeText(getActivity(), R.string.problem_adding_to_repay, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_addfriend:
				showAddFriendDialog();
				return true;

			default:
				return false;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_friendchooser, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		getActivity().setTitle(R.string.title_activity_add_debt);
		mListView = (RecyclerView)getView().findViewById(R.id.list);
		mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
		mAdapter = new FriendListAdapter(getActivity(), mFriends, FriendListAdapter.VIEW_LIST);
		mListView.setAdapter(mAdapter);
		mAdapter.setOnItemClickListener(this);
		mAdapter.setShowingAmounts(false);
		mAdapter.setMultiSelect(true);
		new GetFriendsFromDB().execute();
	}

	@Override
	public void saveFields()
	{
		// No need to do anything here. This fragment uses the DebtBuilder object as storage.
	}

	@Override public void onItemClicked(Friend obj, int position)
	{
		if (((DebtActivity)getActivity()).getDebtBuilder().getSelectedFriends().contains(obj))
		{
			((DebtActivity)getActivity()).getDebtBuilder().removeSelectedFriend(obj);
			mAdapter.setSelectedFriends(((DebtActivity)getActivity()).getDebtBuilder().getSelectedFriends());
		}
		else
		{
			((DebtActivity)getActivity()).getDebtBuilder().getSelectedFriends().add(obj);
			mAdapter.setSelectedFriends(((DebtActivity)getActivity()).getDebtBuilder().getSelectedFriends());
		}
	}

	private class GetFriendsFromDB extends AsyncTask<DatabaseManager, Integer, ArrayList<Friend>>
	{
		@Override
		protected ArrayList<Friend> doInBackground(DatabaseManager... params)
		{
			try
			{
				return ((DebtActivity)getActivity()).getDBHandler().getAllFriends();
			}
			catch (Throwable e)
			{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Friend> result)
		{
			mFriends = result;
			updateList();
		}
	}

	private void updateList()
	{
		mAdapter.setItemsWithSelected(mFriends, ((DebtActivity)getActivity()).getDebtBuilder().getSelectedFriends());
	}
}
