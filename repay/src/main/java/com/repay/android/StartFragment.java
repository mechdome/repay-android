package com.repay.android;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import com.repay.android.database.DatabaseHandler;
import com.repay.android.frienddetails.FriendActivity;
import com.repay.android.model.Debt;
import com.repay.android.model.Friend;
import com.repay.android.settings.SettingsFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

public class StartFragment extends Fragment implements OnItemClickListener {

	public static final String TAG = StartFragment.class.getName();

	private DatabaseHandler 		mDB;
	private GridView 				mGrid;
	private RelativeLayout			mEmptyState;
	private StartFragmentAdapter 	mAdapter;
	private ArrayList<Friend> 		mFriends;
	private ProgressBar 			mProgressBar;
	private int 					mListItem = R.layout.fragment_start_friendslist_item, mSortOrder;
	private Context 				mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_start, container, false);
		return view;
	}

	protected void setFriendsArray(ArrayList<Friend> friends){
		this.mFriends = friends;
	}

	@Override
	public void onStart(){
		super.onStart();
		mGrid = (GridView)getView().findViewById(R.id.fragment_start_friendsgrid);
		mContext = getActivity();
		mEmptyState = (RelativeLayout)getView().findViewById(R.id.fragment_start_emptystate);
		mProgressBar = (ProgressBar)getView().findViewById(R.id.fragment_start_progress);
		mProgressBar.setVisibility(ProgressBar.GONE);
		mDB = new DatabaseHandler(getActivity());
		mGrid.setOnItemClickListener(this); // THE GRID. A DIGITAL FRONTIER.
		mSortOrder = SettingsFragment.getSortOrder(getActivity());
	}

	@Override
	public void onResume() {
		super.onResume();
		updateList();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// Retrieve Friend object and continue with navigation
		Friend selectedFriend = (Friend)arg1.getTag();
		Intent overview = new Intent(getActivity(), FriendActivity.class);
		overview.putExtra(FriendActivity.FRIEND, selectedFriend);
		startActivity(overview);
	}

	public void updateList(){
		new GetFriendsFromDB().execute(mDB);
	}

	private BigDecimal calculateTotalDebt(){
		if(mFriends!=null){
			BigDecimal total = new BigDecimal("0");
			for(int i=0;i<=mFriends.size()-1;i++){
				total = total.add(mFriends.get(i).getDebt());
			}
			return total;
		}
		else return new BigDecimal("0");
	}

	private class GetFriendsFromDB extends AsyncTask<DatabaseHandler, Integer, ArrayList<Friend>> {

		@Override
		protected void onPreExecute() {
			mGrid.setAdapter(null);
			mGrid.setVisibility(ListView.GONE);
			mEmptyState.setVisibility(RelativeLayout.GONE);
			mProgressBar.setVisibility(ProgressBar.VISIBLE);
		}

		@Override
		protected ArrayList<Friend> doInBackground(DatabaseHandler... params) {
			try{
				ArrayList<Friend> friends = mDB.getAllFriends();
				Collections.sort(friends);
				if(SettingsFragment.getSortOrder(getActivity()) == SettingsFragment.SORTORDER_OWETHEM){
					Collections.reverse(friends);
				}
				return friends;
			} catch (CursorIndexOutOfBoundsException e){
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Friend> result){
			if (result!=null) {
				Log.d(TAG, Integer.toString(result.size())+ " friends found");
				setFriendsArray(result);
				mAdapter = new StartFragmentAdapter(mContext, mListItem, result);
				mGrid.setVisibility(ListView.VISIBLE);
				mGrid.setAdapter(mAdapter);
			} else {
				mEmptyState.setVisibility(RelativeLayout.VISIBLE);
			}
			mProgressBar.setVisibility(ProgressBar.GONE);
		}
	}

	public void showTotalDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.action_total);
		TextView txt = new TextView(getActivity());
		txt.setText(SettingsFragment.getCurrencySymbol(getActivity())+calculateTotalDebt().toString());
		dialog.setView(txt);
		txt.setTextAppearance(getActivity(), R.style.TotalDialog);
		txt.setGravity(Gravity.CENTER);
		dialog.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private class RecalculateTotalDebts extends AsyncTask<DatabaseHandler, Integer, ArrayList<Friend>> {

		@Override
		protected void onPreExecute() {
			mGrid.setAdapter(null);
			mGrid.setVisibility(ListView.GONE);
			mEmptyState.setVisibility(RelativeLayout.GONE);
			mProgressBar.setVisibility(ProgressBar.VISIBLE);
		}

		private BigDecimal totalAllDebts(ArrayList<Debt> debts){
			BigDecimal amount = new BigDecimal("0");
			if(debts!=null && debts.size()>0){
				for (int i=0;i<=debts.size()-1;i++){
					amount = amount.add(debts.get(i).getAmount());
				}
			}
			return amount;
		}

		@Override
		protected ArrayList<Friend> doInBackground(DatabaseHandler... params){
			try{
				ArrayList<Friend> friends = params[0].getAllFriends();
				if(friends!=null){
					for (int i=0;i<=friends.size()-1;i++){
						BigDecimal newAmount;
						try{
							newAmount = totalAllDebts(params[0].getDebtsByRepayID(friends.get(i).getRepayID()));
						} catch (Exception e){
							Log.i(TAG, "Looks like the ArrayList returned 0 because there are no debts.");
							// No debts, set new amount as 0 and push to this muthaflippin database. yo.
							newAmount = new BigDecimal("0");
						}
						try{
							friends.get(i).setDebt(newAmount);
							params[0].updateFriendRecord(friends.get(i));
						} catch (Exception e){
							e.printStackTrace();
							Log.e(TAG, e.getMessage());
							Log.e(TAG, "SQL error in sending new amount to database. Tell the user and feel no regret!");
						}
					}
					Collections.sort(friends);
					if(mSortOrder==SettingsFragment.SORTORDER_OWETHEM){
						Collections.reverse(friends);
					}
					return friends;
				}
			} catch (CursorIndexOutOfBoundsException e){
				Log.i(TAG, "No friends in DB. Nothing to recalculate");
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Friend> result){
			if (result!=null) {
				setFriendsArray(result);
				mAdapter = new StartFragmentAdapter(mContext, mListItem, result);
				mGrid.setVisibility(ListView.VISIBLE);
				mGrid.setAdapter(mAdapter);
			} else {
				mEmptyState.setVisibility(RelativeLayout.VISIBLE);
			}
			mProgressBar.setVisibility(ProgressBar.GONE);
		}
	}
}
