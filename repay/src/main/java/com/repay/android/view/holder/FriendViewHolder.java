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
public class FriendViewHolder extends ViewHolder
{
	/**
	 * Store the friend object here to later pass with the onClick
	 */
	private Friend mFriend;
	private RoundedImageView mImage;
	private RoundedImageView mSelected;
	private TextView mName;
	private TextView mAmount;

	public FriendViewHolder(View itemView)
	{
		super(itemView);
		mImage = (RoundedImageView)itemView.findViewById(R.id.image);
		mName = (TextView)itemView.findViewById(R.id.name);
		mAmount = (TextView)itemView.findViewById(R.id.amount);
		mSelected = (RoundedImageView)itemView.findViewById(R.id.selected);
		mSelected.setVisibility(View.INVISIBLE);
	}

	public void populateView(Context context, Friend friend, boolean showingAmount)
	{
		ImageLoader.getInstance().displayImage(friend.getLookupURI(), mImage, Application.getImageOptions());
		mName.setText(friend.getName());
		if (!showingAmount)
		{
			mAmount.setVisibility(View.GONE);
		}
		if (friend.getDebt().compareTo(BigDecimal.ZERO) < 0)
		{
			mImage.setOuterColor(SettingsFragment.getNegativeDebtColourPreference(context));
			mSelected.setOuterColor(SettingsFragment.getNegativeDebtColourPreference(context));
			mAmount.setText(SettingsFragment.getCurrencySymbol(context) + SettingsFragment.getFormattedAmount(friend.getDebt().negate()));
		}
		else
		{
			mImage.setOuterColor(SettingsFragment.getPositiveDebtColourPreference(context));
			mSelected.setOuterColor(SettingsFragment.getPositiveDebtColourPreference(context));
			mAmount.setText(SettingsFragment.getCurrencySymbol(context) + SettingsFragment.getFormattedAmount(friend.getDebt()));
		}
		if (friend.getDebt().compareTo(BigDecimal.ZERO) == 0 && SettingsFragment.isUsingNeutralColour(context))
		{
			mImage.setOuterColor(SettingsFragment.getNeutralDebtColourPreference(context));
			mSelected.setOuterColor(SettingsFragment.getNeutralDebtColourPreference(context));
			mAmount.setText(SettingsFragment.getCurrencySymbol(context) + "0.00");
		}
	}

	public void setOnClickListener(OnClickListener listener)
	{
		this.itemView.setOnClickListener(listener);
	}

	public void setSelected(boolean state)
	{
		if (state)
		{
			mSelected.setScaleX(0f);
			mSelected.setScaleY(0f);
			mSelected.animate()
				.setDuration(RoundedImageView.ANIMATION_LENGTH)
				.scaleX(1f)
				.scaleY(1f)
				.start();
			mSelected.setVisibility(View.VISIBLE);
		}
	}
}
