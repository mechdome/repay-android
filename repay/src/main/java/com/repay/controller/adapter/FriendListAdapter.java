package com.repay.controller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.repay.android.R;
import com.repay.model.Person;
import com.repay.view.holder.FriendViewHolder;

import java.util.ArrayList;

/**
 * {@link android.support.v7.widget.RecyclerView.Adapter} for showing friends in a list or grid.
 * Whether amounts are to be shown is toggleable.
 * <p/>
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 */

public class FriendListAdapter extends Adapter<FriendViewHolder>
{
	public static final int VIEW_LIST = 10;
	public static final int VIEW_GRID = 20;
	private static final String TAG = FriendListAdapter.class.getName();

	private ArrayList<Person> mPersons;
	private int mSelectedView;
	private Context mContext;
	private OnItemClickListener<Person> mItemClickListener;
	private boolean mShowingAmounts = true, mMultiSelect = false;
	private ArrayList<Person> mSelectedPersons;

	public FriendListAdapter(Context context, ArrayList<Person> persons, int viewStyle)
	{
		super();
		mSelectedView = viewStyle;
		mContext = context;
		if (persons != null)
		{
			mPersons = persons;
		}
	}

	public FriendListAdapter(Context context, int viewStyle)
	{
		super();
		mSelectedView = viewStyle;
		mContext = context;
	}

	public void setShowingAmounts(boolean state)
	{
		mShowingAmounts = state;
	}

	public boolean isShowingAmounts()
	{
		return mShowingAmounts;
	}

	public void setItems(ArrayList<Person> persons)
	{
		if (persons != null)
		{
			mPersons = persons;
			notifyDataSetChanged();
		}
	}

	public void setItemsWithSelected(ArrayList<Person> persons, ArrayList<Person> selected)
	{
		if (persons != null)
		{
			mPersons = persons;
			if (selected != null)
			{
				mSelectedPersons = selected;
			}
			notifyDataSetChanged();
		}
	}

	@Override
	public FriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
	{
		View view = null;
		switch (mSelectedView)
		{
			case VIEW_LIST:
				view = LayoutInflater.from(mContext).inflate(R.layout.friend_list_item, viewGroup, false);
				break;

			case VIEW_GRID:
				view = LayoutInflater.from(mContext).inflate(R.layout.friend_grid_item, viewGroup, false);
				break;

			default:
				break;
		}
		return new FriendViewHolder(view);
	}

	@Override
	public void onBindViewHolder(FriendViewHolder vh, final int i)
	{
		vh.populateView(mContext, mPersons.get(i), mShowingAmounts);
		vh.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (mItemClickListener != null)
				{
					mItemClickListener.onItemClicked(mPersons.get(i), i);
				}
			}
		});
		if (isMultiSelect())
		{
			if (mSelectedPersons.contains(mPersons.get(i)))
			{
				vh.setSelected(true);
			} else
			{
				vh.setSelected(false);
			}
		}
	}

	@Override
	public int getItemCount()
	{
		return mPersons != null ? mPersons.size() : 0;
	}

	public void setOnItemClickListener(OnItemClickListener listener)
	{
		mItemClickListener = listener;
	}

	public void setSelectedFriends(ArrayList<Person> persons)
	{
		mSelectedPersons = persons;
		notifyDataSetChanged();
	}

	public void setMultiSelect(boolean state)
	{
		mMultiSelect = state;
	}

	public boolean isMultiSelect()
	{
		return mMultiSelect;
	}

	public ArrayList<Person> getSelectedFriends()
	{
		if (mSelectedPersons == null)
		{
			mSelectedPersons = new ArrayList<Person>();
		}
		return mSelectedPersons;
	}
}
