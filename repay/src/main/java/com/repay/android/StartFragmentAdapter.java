package com.repay.android;

import java.io.IOError;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.repay.android.model.Friend;
import com.repay.android.settings.SettingsFragment;
import com.repay.android.view.RoundedImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ArrayAdapter;
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

public class StartFragmentAdapter extends ArrayAdapter<Friend> {

	private static final String TAG = StartFragmentAdapter.class.getName();
	private int layoutId;
	private ArrayList<Friend> friends;
	private Context mContext;
	private int mTheyOweMeColour, mIOweThemColour;

	public StartFragmentAdapter(Context context, int layoutId, ArrayList<Friend> friends) {
		super(context, layoutId, friends);
		this.layoutId = layoutId;
		this.friends = friends;
		this.mContext = context;
		if(SettingsFragment.getDebtHistoryColourPreference(mContext)==SettingsFragment.DEBTHISTORY_GREEN_RED){
			mTheyOweMeColour = R.drawable.debt_ind_green;
			mIOweThemColour = R.drawable.debt_ind_red;
		} else {
			mTheyOweMeColour = R.drawable.debt_ind_green;
			mIOweThemColour = R.drawable.debt_ind_blue;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(v==null){
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(layoutId, null);
		}
		Friend friend = friends.get(position);
		if(friend!=null){
			// TODO Implement View holder pattern
			TextView name = (TextView)v.findViewById(R.id.fragment_start_friendslist_item_name);
			TextView amount = (TextView)v.findViewById(R.id.fragment_start_friendslist_item_amount);
			final RoundedImageView pic = (RoundedImageView)v.findViewById(R.id.fragment_start_friendslist_item_pic);
			RoundedImageView indicator = (RoundedImageView)v.findViewById(R.id.fragment_start_friendslist_item_bg);

			v.setTag(friend); // Stored as a tag to be retrieved later for OnItemClickListener

			Log.i(TAG,"Now retrieving contact image");
			ImageLoader.getInstance().displayImage(friend.getLookupURI().toString(), pic, Application.getImageOptions());

			name.setText(friend.getName());
			// Determine the number of decimal places
			amount.setText(SettingsFragment.getCurrencySymbol(mContext)+
					SettingsFragment.getFormattedAmount(friend.getDebt()));

			if (friend.getDebt().compareTo(BigDecimal.ZERO)<0){
				indicator.setImageResource(mIOweThemColour);
			} else {
				indicator.setImageResource(mTheyOweMeColour);
			}
		}

		v.setScaleX(0f);
		v.setScaleY(0f);
		v.setAlpha(0f);
		// Finally, animate the View
		ViewPropertyAnimator animate = v.animate();
		// animate.setStartDelay(position*60);
		// This will cause a problem when scrolling down a long 
		// list on the friends list. Best to not include it in prod
		animate.scaleX(1f);
		animate.scaleY(1f);
		animate.alpha(1f);
		animate.start();
		return v;
	}
}
