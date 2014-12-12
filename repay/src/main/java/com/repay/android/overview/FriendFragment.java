package com.repay.android.overview;

import android.app.Fragment;
import android.os.Bundle;

import com.repay.android.R;
import com.repay.android.model.Friend;
import com.repay.android.fragment.SettingsFragment;

/**
 * Created by Matt Allen
 * http://mattallensoftware.co.uk
 * mattallen092@gmail.co.uk
 * <p/>
 * 10/08/2014.
 */
public abstract class FriendFragment extends Fragment implements OnFriendUpdatedListener
{
	protected int mTheyOweMeColour, mIOweThemColour, mNeutralColour;

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		if (SettingsFragment.getDebtHistoryColourPreference(getActivity()) == SettingsFragment.DEBTHISTORY_GREEN_RED)
		{
			mTheyOweMeColour = getActivity().getResources().getColor(R.color.green_debt);
			mIOweThemColour = getActivity().getResources().getColor(R.color.red_debt);
		}
		else
		{
			mTheyOweMeColour = getActivity().getResources().getColor(R.color.green_debt);
			mIOweThemColour = getActivity().getResources().getColor(R.color.blue_debt);
		}
		mNeutralColour = getActivity().getResources().getColor(R.color.neutral_debt);
	}

	public abstract void onFriendUpdated(Friend friend);
}
