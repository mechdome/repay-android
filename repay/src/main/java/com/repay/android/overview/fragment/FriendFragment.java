package com.repay.android.overview.fragment;

import android.app.Fragment;
import android.os.Bundle;

import com.repay.android.R;
import com.repay.android.fragment.SettingsFragment;
import com.repay.model.Person;
import com.repay.android.overview.OnFriendUpdatedListener;

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
			mTheyOweMeColour = getActivity().getResources().getColor(R.color.debt_green);
			mIOweThemColour = getActivity().getResources().getColor(R.color.debt_red);
		} else
		{
			mTheyOweMeColour = getActivity().getResources().getColor(R.color.debt_green);
			mIOweThemColour = getActivity().getResources().getColor(R.color.debt_blue);
		}
		mNeutralColour = getActivity().getResources().getColor(R.color.debt_neutral);
	}

	public abstract void onFriendUpdated(Person person);
}
