package com.repay.android.adddebt;

import java.util.ArrayList;

import com.repay.android.model.Friend;
import com.repay.android.R;
import com.repay.android.RetrieveContactPhoto;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

public class ChoosePersonAdapter extends ArrayAdapter<Friend> {

	private static final String 		TAG = ChoosePersonAdapter.class.getName();
	private int 						mLayoutID;
	private ArrayList<Friend> 			mFriends, mSelectedFriends;
	private Context 					mContext;
	public static final int 			SELECTED_COLOUR = Color.parseColor("#FFC3BB"); // Same as "Selected Tint" under colors.xml
	public static final int 			DESELECTED_COLOUR = Color.parseColor("#00FFFFFF"); // Invisible

	public ChoosePersonAdapter(Context context, int layoutId, 
			ArrayList<Friend> friends, ArrayList<Friend> selectedFriends) {
		super(context, layoutId, friends);
		this.mLayoutID = layoutId;
		this.mFriends = friends;
		this.mContext = context;
		this.mSelectedFriends = selectedFriends;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(v==null){
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(mLayoutID, null);
		}
		Friend friend = mFriends.get(position);
		TextView name = (TextView)v.findViewById(R.id.activity_friendchooser_name);
		ImageView pic = (ImageView)v.findViewById(R.id.activity_friendchooser_pic);
		if(friend!=null){
			v.setTag(friend); // Stored as a tag to be retrieved later for OnItemClickListener

			// Colour the list item based on whether it is in the map
			if (mSelectedFriends.contains(friend)) {
				v.setBackgroundColor(SELECTED_COLOUR);
				Log.i(TAG,"Highlighting "+friend.getName());
			}
		}
		new RetrieveContactPhoto(friend.getLookupURI(), pic, mContext, R.drawable.friend_image_dark).execute();
		name.setText(friend.getName());
		return v;
	}
}
