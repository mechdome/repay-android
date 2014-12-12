package com.repay.android.view.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.repay.android.Application;
import com.repay.android.R;
import com.repay.android.fragment.SettingsFragment;
import com.repay.android.model.Friend;
import com.repay.android.view.RoundedImageView;

import java.math.BigDecimal;

/**
 * @author Matt Allen
 * @project Repay
 */
public class FriendViewHolder extends ViewHolder implements OnClickListener
{
	/**
	 * Store the friend object here to later pass with the onClick
	 */
	private Friend mFriend;
	private RoundedImageView mImage;
	private TextView mName;
	private TextView mAmount;

	public FriendViewHolder(View itemView)
	{
		super(itemView);
		mImage = (RoundedImageView)itemView.findViewById(R.id.image);
		mName = (TextView)itemView.findViewById(R.id.name);
		try
		{
			mAmount = (TextView)itemView.findViewById(R.id.amount);
		}
		catch (Exception e)
		{
			// Using list, not grid
		}
	}

	public void populateView(Context context, Friend friend)
	{
		ImageLoader.getInstance().displayImage(friend.getLookupURI(), mImage, Application.getImageOptions());
		mName.setText(friend.getName());
		if (friend.getDebt().compareTo(BigDecimal.ZERO) < 0)
		{
			mImage.setOuterColor(SettingsFragment.getNegativeDebtColourPreference(context));
			if (mAmount != null) mAmount.setText(SettingsFragment.getCurrencySymbol(context) + SettingsFragment.getFormattedAmount(friend.getDebt().negate()));
		}
		else
		{
			mImage.setOuterColor(SettingsFragment.getPositiveDebtColourPreference(context));
			if (mAmount != null) mAmount.setText(SettingsFragment.getCurrencySymbol(context) + SettingsFragment.getFormattedAmount(friend.getDebt()));
		}
		if (friend.getDebt().compareTo(BigDecimal.ZERO) == 0 && SettingsFragment.isUsingNeutralColour(context))
		{
			mImage.setOuterColor(SettingsFragment.getNeutralDebtColourPreference(context));
		}
	}

	@Override public void onClick(View v)
	{
		
	}
}
