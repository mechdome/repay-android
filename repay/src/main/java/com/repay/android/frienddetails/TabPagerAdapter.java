package com.repay.android.frienddetails;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public class TabPagerAdapter extends FragmentPagerAdapter {

	private Fragment[] mFragments;

	public TabPagerAdapter(FragmentManager fm, Fragment[] fragments)
	{
    	super(fm);
		mFragments = fragments;
    }

    @Override
    public int getCount()
	{
        return mFragments.length;
    }

    @Override
    public Fragment getItem(int position)
	{
        return mFragments[position];
    }
}