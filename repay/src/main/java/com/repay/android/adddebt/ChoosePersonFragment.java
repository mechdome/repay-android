package com.repay.android.adddebt;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.repay.android.model.Friend;
import com.repay.android.R;
import com.repay.android.database.DatabaseHandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
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
import android.widget.Button;
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

public class ChoosePersonFragment extends Fragment implements OnItemClickListener, OnClickListener {

	private static final String 		TAG = ChoosePersonFragment.class.getName();
	public static final int 			PICK_CONTACT_REQUEST = 1;
	
	private ArrayAdapter<Friend> 		mAdapter;
	private int 						mListResource = R.layout.fragment_adddebt_friendslist_item;
	private ListView 					mListView;
	private RelativeLayout 				mEmptyState;
	private Context 					mContext;
	private ArrayList<Friend> 			mSelectedFriends;
	private DatabaseHandler 			mDB;
	
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
					if(name==null || name.equals("")){
						throw new NullPointerException();
					}
					Friend newFriend = new Friend(DatabaseHandler.generateRepayID(), null, name, new BigDecimal("0"));
					mDB.addFriend(newFriend);
					dataSetChanged();
				} catch (SQLException e) {
					Toast.makeText(getActivity(), "Friend could not be added", Toast.LENGTH_SHORT).show();
				} catch (NullPointerException e){
					Toast.makeText(getActivity(), "No name entered, please try again", Toast.LENGTH_SHORT).show();
				}
			}
		});
		dialog.show();
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
	public void onStart(){
		super.onStart();
		mListView = (ListView)getView().findViewById(R.id.activity_friendchooser_list);
		mListView.setOnItemClickListener(this);
		mContext = getActivity();
		mEmptyState = (RelativeLayout)getView().findViewById(R.id.activity_friendchooser_emptystate);
		Button emptyBtn = (Button)getView().findViewById(R.id.activity_friendchooser_helpbtn);
		emptyBtn.setOnClickListener(this);
		mDB = ((AddDebtActivity)getActivity()).getDB();
		new GetFriendsFromDB().execute(mDB);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Friend selectedFriend = (Friend)arg1.getTag();
		Log.i(TAG, selectedFriend.getName()+ " selected ("+ selectedFriend.getRepayID() +")");
		if(mSelectedFriends.contains(selectedFriend)){
			mSelectedFriends.remove(selectedFriend);
			arg1.setBackgroundColor(ChoosePersonAdapter.DESELECTED_COLOUR);
		} else {
			mSelectedFriends.add(selectedFriend);
			arg1.setBackgroundColor(ChoosePersonAdapter.SELECTED_COLOUR);
		}
	}
	
	public void onResume(){
		super.onResume();
		// Empty all the previously selected people - otherwise they'll stack up
		mSelectedFriends = new ArrayList<Friend>();
	}

	public void dataSetChanged(){
		new GetFriendsFromDB().execute(mDB);
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
				ArrayList<Friend> friends = params[0].getAllFriends();
				return friends;
			} catch (Throwable e){
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Friend> result){
			if (result!=null) {
				mListView.setVisibility(ListView.VISIBLE);
				mAdapter = new ChoosePersonAdapter(mContext, mListResource, result, mSelectedFriends);
				mListView.setAdapter(mAdapter);
			} else {
				mEmptyState.setVisibility(RelativeLayout.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.activity_friendchooser_donebtn || v.getId()==R.id.activity_friendchooser_helpbtn){
			showAddFriendDialog();
		}
	}

	public ArrayList<Friend> getSelectedFriends(){
		return mSelectedFriends;
	}
}
