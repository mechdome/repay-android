package com.repay.android.adddebt;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.repay.android.model.Friend;
import com.repay.android.R;
import com.repay.android.database.DatabaseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

public class ChoosePersonFragment extends DebtFragment implements OnItemClickListener, OnClickListener {

	private static final String 		TAG = ChoosePersonFragment.class.getName();
	public static final int 			PICK_CONTACT_REQUEST = 1;
	
	private ArrayAdapter<Friend> 		mAdapter;
	private int 						mListResource = R.layout.fragment_adddebt_friendslist_item;
	private ListView 					mListView;
	private RelativeLayout 				mEmptyState;
	private ArrayList<Friend> 			mSelectedFriends;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); // Tell the activity that we have ActionBar items
	}
	
	/*
	 * Here we add the extra menu items needed into the ActionBar. Even with
	 * implementing this method, we still need to tell the Activity that we
	 * have menu items to add
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inf){
		super.onCreateOptionsMenu(menu, inf);
        if(menu.size()<=1){
            // Stops Activity from receiving duplicate MenuItems
            // Solves GitHub bug #2
            inf.inflate(R.menu.chooseperson, menu);
        }
	}
	
	private void showAddFriendDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.fragment_addfriend_dialogtitle);
		dialog.setItems(new CharSequence[]{"Add From Contacts", "Add A Name"}, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which==0){
					Intent pickContactIntent = new Intent(Intent.ACTION_GET_CONTENT);
					pickContactIntent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
					getActivity().startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
				}
				else if(which==1){
					addFriendByName();
				}
			}
		});
		dialog.show();
	}
	
	public void addFriendByName(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.fragment_addfriend_dialogtitle);
		final EditText nameEntry = new EditText(getActivity());
		dialog.setView(nameEntry);
		dialog.setPositiveButton(R.string.fragment_addfriend_dialogokay, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = nameEntry.getText().toString();
				try {
					if(!TextUtils.isEmpty(nameEntry.getText().toString())){
						Friend newFriend = new Friend(DatabaseHandler.generateRepayID(), null, name, new BigDecimal("0"));
						((DebtActivity) getActivity()).getDBHandler().addFriend(newFriend);
					}
				} catch (SQLException e) {
					Toast.makeText(getActivity(), "Friend could not be added", Toast.LENGTH_SHORT).show();
				}
			}
		});
		dialog.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && requestCode == Activity.RESULT_OK){
			try{
				Log.i(TAG,"Closing contact picker");
				Uri contactUri = data.getData();

				String[] cols = {ContactsContract.Contacts.DISPLAY_NAME};
				Cursor cursor = getActivity().getContentResolver().query(contactUri, cols, null, null, null);
				cursor.moveToFirst();

				String result = cursor.getString(0).replaceAll("[-+.^:,']","");
				Friend pickerResult = new Friend(DatabaseHandler.generateRepayID(), contactUri.toString(), result, new BigDecimal("0"));

				((DebtActivity) getActivity()).getDBHandler().addFriend(pickerResult);
			}
			catch (IndexOutOfBoundsException e)
			{
				Toast.makeText(getActivity(), "Problem in getting result from your contacts", Toast.LENGTH_SHORT).show();
			}
			catch (SQLException e)
			{
				// TODO Change this. It's pretty crap.
				e.printStackTrace();
				AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
				alert.setMessage("This person already exists in Repay");
				alert.setTitle("Person Already Exists");
				Log.i(TAG, "Person already exists within app database");
				alert.show();
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_addfriend:
			showAddFriendDialog();
			return true;

		default:
			return true;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_friendchooser, container, false);
		mSelectedFriends = new ArrayList<Friend>();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		mListView = (ListView)getView().findViewById(R.id.activity_friendchooser_list);
		mListView.setOnItemClickListener(this);

		mEmptyState = (RelativeLayout)getView().findViewById(R.id.activity_friendchooser_emptystate);

		(getView().findViewById(R.id.activity_friendchooser_helpbtn)).setOnClickListener(this);

		new GetFriendsFromDB().execute();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Friend selectedFriend = (Friend)arg1.getTag();
		Log.i(TAG, selectedFriend.getName()+ " selected ("+ selectedFriend.getRepayID() +")");
		if(((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends().contains(selectedFriend)){
			((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends().remove(selectedFriend);
			arg1.setBackgroundColor(ChoosePersonAdapter.DESELECTED_COLOUR);
		} else {
			((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends().add(selectedFriend);
			arg1.setBackgroundColor(ChoosePersonAdapter.SELECTED_COLOUR);
		}
	}

	@Override
	public void saveFields() {
		// No need to do anything here. This fragment uses the DebtBuilder object as storage.
	}

	private class GetFriendsFromDB extends AsyncTask<DatabaseHandler, Integer, ArrayList<Friend>> {

		@Override
		protected void onPreExecute() {
			mListView.setVisibility(ListView.INVISIBLE);
			mEmptyState.setVisibility(RelativeLayout.INVISIBLE);
		}

		@Override
		protected ArrayList<Friend> doInBackground(DatabaseHandler... params) {
			try{
				ArrayList<Friend> friends = ((DebtActivity) getActivity()).getDBHandler().getAllFriends();
				return friends;
			} catch (Throwable e){
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Friend> result){
			if (result != null) {
				mListView.setVisibility(ListView.VISIBLE);
				mAdapter = new ChoosePersonAdapter(getActivity(), mListResource, result, mSelectedFriends);
				mListView.setAdapter(mAdapter);
			} else {
				mEmptyState.setVisibility(RelativeLayout.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId()==R.id.activity_friendchooser_helpbtn)
		{
			showAddFriendDialog();
		}
	}
}
