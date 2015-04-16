package com.repay.android.overview.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.repay.android.R;
import com.repay.controller.adapter.DebtListAdapter;
import com.repay.controller.adapter.OnItemLongClickListener;
import com.repay.android.debtwizard.DebtActivity;
import com.repay.android.debtwizard.EditDebtActivity;
import com.repay.model.Debt;
import com.repay.model.Person;
import com.repay.android.overview.FriendActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 */

public class FriendHistoryFragment extends FriendFragment implements OnItemLongClickListener<Debt>
{
	private static final String TAG = FriendHistoryFragment.class.getName();

	private RecyclerView mList;
	private TextView mNoDebtsMsg;
	private ProgressBar mProgressBar;
	private DebtListAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_debthistory, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		mAdapter = new DebtListAdapter();
		mAdapter.setOnItemLongClickListener(this);
		mList = (RecyclerView) getView().findViewById(R.id.list);
		mList.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
		mList.setAdapter(mAdapter);
		mNoDebtsMsg = (TextView) getView().findViewById(R.id.empty);
		mProgressBar = (ProgressBar) getView().findViewById(R.id.progress);
		mProgressBar.setVisibility(ProgressBar.GONE);
		new GetDebtsFromDB().execute(((FriendActivity) getActivity()).getFriend());
	}

	private void deleteDebt(final Debt debt)
	{
		AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
		deleteDialog.setTitle(R.string.delete);
		deleteDialog.setMessage(R.string.confirm_remove_debt);
		deleteDialog.setPositiveButton(R.string.delete, new AlertDialog.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				deleteDebtFromDatabase(debt);
				((FriendActivity) getActivity()).updateFriend();
			}
		});

		deleteDialog.setNegativeButton(R.string.cancel, null);
		deleteDialog.show();
	}

	@Override
	public void onFriendUpdated(Person person)
	{
		new GetDebtsFromDB().execute(person);
	}

	/**
	 * Remove a debt from the database and update the amount stored
	 * in the friends table
	 *
	 * @param debtToDelete The debt that is to be removed
	 */
	public void deleteDebtFromDatabase(Debt debtToDelete)
	{
		try
		{
			((FriendActivity) getActivity()).getDB().removeDebt(debtToDelete.getDebtID());
			ArrayList<Debt> allDebts = ((FriendActivity) getActivity()).getDB().getDebtsByRepayID(((FriendActivity) getActivity()).getFriend().getRepayID());
			BigDecimal newAmount = new BigDecimal("0");
			if (allDebts != null && allDebts.size() > 0)
			{
				for (int i = 0; i <= allDebts.size() - 1; i++)
				{
					newAmount = newAmount.add(allDebts.get(i).getAmount());
				}
			}

			((FriendActivity) getActivity()).getFriend().setDebt(newAmount);
			((FriendActivity) getActivity()).getDB().updateFriendRecord(((FriendActivity) getActivity()).getFriend());
		} catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(getActivity(), "Could not remove debt from database", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onItemLongClick(final Debt obj, int position)
	{
		AlertDialog.Builder chooseDialog = new AlertDialog.Builder(getActivity());
		chooseDialog.setTitle(R.string.manage_debt);
		chooseDialog.setItems(R.array.debtselected_items, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (which == 0)
				{
					Intent i = new Intent(getActivity(), EditDebtActivity.class);
					i.putExtra(DebtActivity.DEBT, obj);
					i.putExtra(DebtActivity.FRIEND, ((FriendActivity) getActivity()).getFriend());
					getActivity().startActivity(i);
				} else if (which == 1)
				{
					deleteDebt(obj);
				}
			}
		});
		chooseDialog.show();
	}

	private class GetDebtsFromDB extends AsyncTask<Person, Void, ArrayList<Debt>>
	{
		@Override
		protected void onPreExecute()
		{
			mList.setVisibility(ListView.GONE);
			mNoDebtsMsg.setVisibility(TextView.GONE);
			mProgressBar.setVisibility(ProgressBar.VISIBLE);
		}

		@Override
		protected ArrayList<Debt> doInBackground(Person... params)
		{
			try
			{
				ArrayList<Debt> debts = ((FriendActivity) getActivity()).getDB().getDebtsByRepayID(params[0].getRepayID());
				Collections.sort(debts);
				return debts;
			} catch (Throwable e)
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Debt> result)
		{
			mProgressBar.setVisibility(ProgressBar.GONE);
			mAdapter.setItems(result);
			if (result == null || result.size() == 0)
			{
				mNoDebtsMsg.setVisibility(TextView.VISIBLE);
			} else
			{
				mList.setVisibility(ListView.VISIBLE);
			}
		}
	}
}
