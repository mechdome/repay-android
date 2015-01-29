package com.repay.android.overview.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.repay.android.Application;
import com.repay.android.R;
import com.repay.android.fragment.SettingsFragment;
import com.repay.android.model.Friend;
import com.repay.android.overview.FriendActivity;
import com.repay.android.overview.ShareDialog;
import com.repay.android.view.RoundedImageView;

import java.math.BigDecimal;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 */

public class FriendOverviewFragment extends FriendFragment implements OnClickListener
{
	private RoundedImageView mFriendPic;
	private TextView mTotalOwed, mTotalOwedPrefix;
	private LinearLayout mHeaderBg;
	private boolean mUseNeutralColour;
	private FriendHistoryFragment mHistory;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_frienddetails, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		mHistory = new FriendHistoryFragment();
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.history, mHistory)
				.commit();

		mFriendPic = (RoundedImageView) getView().findViewById(R.id.friend_image);
		mTotalOwed = (TextView) getView().findViewById(R.id.amount);
		mTotalOwedPrefix = (TextView) getView().findViewById(R.id.owe_status);
		mHeaderBg = (LinearLayout) getView().findViewById((R.id.top_background));

		mUseNeutralColour = SettingsFragment.isUsingNeutralColour(getActivity());

		onFriendUpdated(((FriendActivity) getActivity()).getFriend());
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.share:
				if (((FriendActivity) getActivity()).getFriend().getDebt().compareTo(BigDecimal.ZERO) != 0)
				{
					AlertDialog.Builder shareDialog = new ShareDialog(getActivity(), ((FriendActivity) getActivity()).getFriend());
					shareDialog.show();
				} else
				{
					Toast.makeText(getActivity(), "There's no debt between you", Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}

	@Override
	public void onFriendUpdated(Friend friend)
	{
		ImageLoader.getInstance().displayImage(friend.getLookupURI(), mFriendPic, Application.getImageOptions());

		if (friend.getDebt().compareTo(BigDecimal.ZERO) == 0)
		{
			mTotalOwedPrefix.setText(R.string.even_debt);
			mTotalOwed.setText(SettingsFragment.getCurrencySymbol(getActivity()) + "0");
			if (mUseNeutralColour)
			{
				mFriendPic.setOuterColor(mNeutralColour);
				mHeaderBg.setBackgroundColor(mNeutralColour);
			} else
			{
				mFriendPic.setOuterColor(mTheyOweMeColour);
				mHeaderBg.setBackgroundColor(mTheyOweMeColour);
			}
		} else if (friend.getDebt().compareTo(BigDecimal.ZERO) < 0)
		{
			mTotalOwedPrefix.setText(R.string.i_owe);
			String amount = SettingsFragment.getFormattedAmount(friend.getDebt().negate());
			mTotalOwed.setText(SettingsFragment.getCurrencySymbol(getActivity()) + amount);
			mFriendPic.setOuterColor(mIOweThemColour);
			mHeaderBg.setBackgroundColor(mIOweThemColour);
		} else if (friend.getDebt().compareTo(BigDecimal.ZERO) > 0)
		{
			mTotalOwedPrefix.setText(R.string.they_owe);
			String amount = SettingsFragment.getFormattedAmount(friend.getDebt());
			mTotalOwed.setText(SettingsFragment.getCurrencySymbol(getActivity()) + amount);
			mFriendPic.setOuterColor(mTheyOweMeColour);
			mHeaderBg.setBackgroundColor(mTheyOweMeColour);
		}

		mHistory.onFriendUpdated(friend);
	}
}
