package com.repay.android.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.repay.android.MainActivity;
import com.repay.android.R;
import com.repay.android.adapter.FriendListAdapter;
import com.repay.android.adapter.OnItemClickListener;
import com.repay.android.debtwizard.AddDebtActivity;
import com.repay.android.manager.DatabaseManager;
import com.repay.android.model.Debt;
import com.repay.android.model.Friend;
import com.repay.android.overview.FriendActivity;
import com.repay.android.view.TotalDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class FriendsListFragment extends Fragment implements OnItemClickListener<Friend>, OnClickListener
{

	public static final String TAG = FriendsListFragment.class.getName();

	private RecyclerView mList;
	private FriendListAdapter mAdapter;
	private ProgressBar mProgressBar;
	private int mSortOrder;
	private TextView mEmpty;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_friendslist, container, false);
		mList = (RecyclerView)view.findViewById(R.id.list);
		mProgressBar = (ProgressBar)view.findViewById(R.id.progress);
		mProgressBar.setVisibility(ProgressBar.GONE);
		((FloatingActionButton)view.findViewById(R.id.add)).attachToRecyclerView(mList);
		((FloatingActionButton)view.findViewById(R.id.add)).setOnClickListener(this);
		mEmpty = (TextView)view.findViewById(R.id.empty);
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		updateList();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		mList.setLayoutManager(new StaggeredGridLayoutManager(getResources().getInteger(R.integer.mainactivity_cols), StaggeredGridLayoutManager.VERTICAL));
		mAdapter = new FriendListAdapter(getActivity(), ((MainActivity)getActivity()).getFriends(), FriendListAdapter.VIEW_GRID);
		mAdapter.setOnItemClickListener(this);
		mList.setAdapter(mAdapter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getItemId())
		{
			case R.id.action_total:
				showTotalDialog();
				return true;

			case R.id.action_recalculateTotals:
				new RecalculateTotalDebts().execute(((MainActivity)getActivity()).getDB());

			default:
				return true;
		}
	}

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
		inf.inflate(R.menu.friendslist, menu);
	}

	public void updateList()
	{
		mList.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(ProgressBar.GONE);
		mAdapter.setItems(((MainActivity)getActivity()).getFriends());
		if (((MainActivity)getActivity()).getFriends() != null &&
			((MainActivity)getActivity()).getFriends().size() > 0)
		{
			mEmpty.setVisibility(View.GONE);
		}
		else
		{
			mEmpty.setVisibility(View.VISIBLE);
		}
	}

	private BigDecimal calculateTotalDebt()
	{
		if (((MainActivity)getActivity()).getFriends() != null)
		{
			BigDecimal total = new BigDecimal("0");
			for (Friend friend : ((MainActivity)getActivity()).getFriends())
			{
				total = total.add(friend.getDebt());
			}
			return total;
		}
		else
		{
			return new BigDecimal("0");
		}
	}

	public void showTotalDialog()
	{
		new TotalDialog(getActivity(), ((MainActivity)getActivity()).getFriends()).show(getFragmentManager(), null);
	}

	@Override
	public void onItemClicked(Friend obj, int position)
	{
		Intent i = new Intent(getActivity(), FriendActivity.class);
		i.putExtra(FriendActivity.FRIEND, obj);
		startActivity(i);
	}

	@Override public void onClick(View v)
	{
		Intent intent = new Intent();
		intent.setClass(getActivity(), AddDebtActivity.class);
		startActivity(intent);
	}

	private class RecalculateTotalDebts extends AsyncTask<DatabaseManager, Integer, ArrayList<Friend>>
	{

		@Override
		protected void onPreExecute()
		{
			mList.setVisibility(ListView.GONE);
			mProgressBar.setVisibility(ProgressBar.VISIBLE);
		}

		private BigDecimal totalAllDebts(ArrayList<Debt> debts)
		{
			BigDecimal amount = new BigDecimal("0");
			if (debts != null && debts.size() > 0)
			{
				for (int i = 0; i <= debts.size() - 1; i++)
				{
					amount = amount.add(debts.get(i).getAmount());
				}
			}
			return amount;
		}

		@Override
		protected ArrayList<Friend> doInBackground(DatabaseManager... params)
		{
			try
			{
				ArrayList<Friend> friends = params[0].getAllFriends();
				if (friends != null)
				{
					for (int i = 0; i <= friends.size() - 1; i++)
					{
						BigDecimal newAmount;
						try
						{
							newAmount = totalAllDebts(params[0].getDebtsByRepayID(friends.get(i).getRepayID()));
						}
						catch (Exception e)
						{
							e.printStackTrace();
							newAmount = new BigDecimal("0");
						}
						try
						{
							friends.get(i).setDebt(newAmount);
							params[0].updateFriendRecord(friends.get(i));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}

					Collections.sort(friends);
					if (mSortOrder == SettingsFragment.SORTORDER_OWETHEM)
					{
						Collections.reverse(friends);
					}
					return friends;
				}
			}
			catch (CursorIndexOutOfBoundsException e)
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Friend> result)
		{
			if (result != null)
			{
				((MainActivity)getActivity()).setFriends(result);
				updateList();
			}

			mProgressBar.setVisibility(ProgressBar.GONE);
		}
	}
}
