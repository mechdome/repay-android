package com.repay.android;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.repay.android.model.Friend;

import java.util.ArrayList;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class FriendListAdapter extends Adapter<FriendViewHolder>
{
	public static final int VIEW_LIST = 10;
	public static final int VIEW_GRID = 20;
	private static final String TAG = FriendListAdapter.class.getName();

	private ArrayList<Friend> mFriends;
	private int mSelectedView;
	private Context mContext;

	public FriendListAdapter(Context context, ArrayList<Friend> friends, int viewStyle)
	{
		super();
		mSelectedView = viewStyle;
		mContext = context;
		mFriends = friends;
	}

	public FriendListAdapter(Context context, int viewStyle)
	{
		super();
		mSelectedView = viewStyle;
		mContext = context;
	}

	public void setItems(ArrayList<Friend> friends)
	{
		mFriends = friends;
		notifyDataSetChanged();
	}

	@Override public FriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
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

	@Override public void onBindViewHolder(FriendViewHolder vh, int i)
	{
		vh.populateView(mContext, mFriends.get(i));
	}

	@Override public int getItemCount()
	{
		return mFriends.size();
	}
}
