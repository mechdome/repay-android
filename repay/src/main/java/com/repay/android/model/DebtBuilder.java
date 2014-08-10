package com.repay.android.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Matt Allen
 * http://mattallensoftware.co.uk
 * mattallen092@gmail.co.uk
 * <p/>
 * 07/08/2014.
 */
public class DebtBuilder implements Serializable
{
	private String mDescription;
	private boolean owesMe = true, distributeEvenly = false, includingMe = false;
	private ArrayList<Friend> mSelectedFriends;
	private BigDecimal mAmount;
	private Date mDate;

	public DebtBuilder()
	{
		mSelectedFriends = new ArrayList<Friend>();
	}

	public String getDescription()
	{
		return mDescription;
	}

	public void setDescription(String description)
	{
		mDescription = description;
	}

	public boolean isInDebtToMe() {
		return owesMe;
	}

	public void setInDebtToMe(boolean owesMe) {
		this.owesMe = owesMe;
	}

	public boolean isDistributedEvenly() {
		return distributeEvenly;
	}

	public void setDistributedEvenly(boolean distributeEvenly) {
		this.distributeEvenly = distributeEvenly;
	}

	public Date getDate()
	{
		if (mDate == null) mDate = new Date();
		return mDate;
	}

	public void setDate(Date date)
	{
		mDate = date;
	}

	public boolean isIncludingMe() {
		return includingMe;
	}

	public void setIncludingMe(boolean includingMe) {
		this.includingMe = includingMe;
	}

	public ArrayList<Friend> getSelectedFriends() {
		return mSelectedFriends;
	}

	public void setSelectedFriends(ArrayList<Friend> selectedFriends) {
		this.mSelectedFriends = selectedFriends;
	}

	public void addSelectedFriend(Friend friend)
	{
		if (!mSelectedFriends.contains(friend)) mSelectedFriends.add(friend);
	}

	public void removeSelectedFriend(Friend friend)
	{
		mSelectedFriends.remove(friend);
	}

	public BigDecimal getAmount() {
		return mAmount;
	}

	public void setAmount(BigDecimal amount) {
		this.mAmount = amount;
	}

	/**
	 * If there are friends selected, choose the first one in the list and retrieve a usuable image Uri
	 * @return The LookupUri for the user to passed to
	 * {@link com.nostra13.universalimageloader.core.ImageLoader#displayImage(String, android.widget.ImageView)}
	 */
	public String getImageUri()
	{
		if (mSelectedFriends != null && mSelectedFriends.size() > 0)
		{
			for (Friend friend : mSelectedFriends)
			{
				if (!TextUtils.isEmpty(friend.getLookupURI()))
				{
					return friend.getLookupURI();
				}
			}
		}
		return null;
	}

	public String getNamesList(boolean shortened)
	{
		String name = "";
		if (mSelectedFriends.size() < 3)
		{
			for (Friend friend : mSelectedFriends)
			{
				name += friend.getName() + "\n";
			}
		}
		else if (mSelectedFriends.size() > 2)
		{
			if (shortened)
			{
				for (int i=0; i <= 2; i++)
				{
					name += mSelectedFriends.get(i).getName() + "\n";
				}
				name += "and more...";
			}
			else
			{
				for (int i=0; i <= 7; i++)
				{
					name += mSelectedFriends.get(i).getName() + "\n";
				}
				if (mSelectedFriends.size() > 7)
				{
					name += "and more...";
				}
			}
		}
		return name;
	}

	/**
	 * Create a list of all new debts to add into the database
	 * @return A list of debts that can be iterated through
	 */
	public List<Debt> getNewDebts()
	{
		BigDecimal debtAmount = getAmountToApply();

		List<Debt> debts = new ArrayList<Debt>();
		for (Friend friend : mSelectedFriends)
		{
			debts.add(new Debt(0, friend.getRepayID(), getDate(), debtAmount, mDescription));
		}
		return debts;
	}

	/**
	 * Quickly get the amount that will be applied to each person
	 * @return {@link java.math.BigDecimal} representation of the amount
	 */
	private BigDecimal getAmountToApply()
	{
		BigDecimal debtAmount;
		if (distributeEvenly)
		{
			if (includingMe)
			{
				debtAmount = mAmount.divide(new BigDecimal(mSelectedFriends.size() + 1));
			}
			else
			{
				debtAmount = mAmount.divide(new BigDecimal(mSelectedFriends.size()));
			}
		}
		else
		{
			debtAmount = mAmount;
		}
		if (!owesMe) debtAmount = debtAmount.negate();
		return debtAmount;
	}

	public List<Friend> getUpdatedFriends()
	{
		BigDecimal debtAmount = getAmountToApply();
		ArrayList<Friend> updated = mSelectedFriends;
		for (Friend friend : updated)
		{
			friend.setDebt(friend.getDebt().add(debtAmount));
		}
		return updated;
	}
}
