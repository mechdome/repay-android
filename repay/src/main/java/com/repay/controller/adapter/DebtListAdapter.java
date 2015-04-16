package com.repay.controller.adapter;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.repay.android.R;
import com.repay.model.Debt;
import com.repay.view.holder.DebtViewHolder;

import java.util.ArrayList;

/**
 * // TODO: Add class description
 *
 * @author Matt Allen
 */
public class DebtListAdapter extends Adapter<DebtViewHolder>
{
	private ArrayList<Debt> mDebts;
	private OnItemLongClickListener<Debt> mListener;

	@Override
	public DebtViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.debt_list_item, parent, false);
		return new DebtViewHolder(view);
	}

	@Override
	public void onBindViewHolder(DebtViewHolder holder, final int position)
	{
		holder.populateView(mDebts.get(position));
		if (mListener != null)
		{
			holder.setOnLongClick(new OnLongClickListener()
			{
				@Override
				public boolean onLongClick(View v)
				{
					mListener.onItemLongClick(mDebts.get(position), position);
					return true;
				}
			});
		}
	}

	public void setItems(ArrayList<Debt> debts)
	{
		mDebts = debts;
		notifyDataSetChanged();
	}

	public void setOnItemLongClickListener(OnItemLongClickListener<Debt> listener)
	{
		mListener = listener;
	}

	@Override
	public int getItemCount()
	{
		return getDebts().size();
	}

	public ArrayList<Debt> getDebts()
	{
		if (mDebts == null)
		{
			mDebts = new ArrayList<Debt>();
		}
		return mDebts;
	}
}
