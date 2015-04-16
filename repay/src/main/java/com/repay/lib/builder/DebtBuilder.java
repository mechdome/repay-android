package com.repay.lib.builder;

import android.text.TextUtils;

import com.repay.model.Debt;
import com.repay.model.Person;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
	private ArrayList<Person> mSelectedPersons;
	private BigDecimal mAmount;
	private Date mDate;

	public DebtBuilder()
	{
		mSelectedPersons = new ArrayList<Person>();
	}

	public String getDescription()
	{
		return mDescription;
	}

	public void setDescription(String description)
	{
		mDescription = description;
	}

	public boolean isInDebtToMe()
	{
		return owesMe;
	}

	public void setInDebtToMe(boolean owesMe)
	{
		this.owesMe = owesMe;
	}

	public boolean isDistributedEvenly()
	{
		return distributeEvenly;
	}

	public void setDistributedEvenly(boolean distributeEvenly)
	{
		this.distributeEvenly = distributeEvenly;
	}

	public Date getDate()
	{
		if (mDate == null)
		{
			mDate = new Date();
		}
		return mDate;
	}

	public void setDate(Date date)
	{
		mDate = date;
	}

	public boolean isIncludingMe()
	{
		return includingMe;
	}

	public void setIncludingMe(boolean includingMe)
	{
		this.includingMe = includingMe;
	}

	public ArrayList<Person> getSelectedFriends()
	{
		return mSelectedPersons;
	}

	public void setSelectedFriends(ArrayList<Person> selectedPersons)
	{
		this.mSelectedPersons = selectedPersons;
	}

	public void addSelectedFriend(Person person)
	{
		if (!mSelectedPersons.contains(person))
		{
			mSelectedPersons.add(person);
		}
	}

	public void removeSelectedFriend(Person person)
	{
		mSelectedPersons.remove(person);
	}

	public BigDecimal getAmount()
	{
		return mAmount;
	}

	public void setAmount(BigDecimal amount)
	{
		this.mAmount = amount;
	}

	public ArrayList<Person> getFriendsWithImages()
	{
		if (mSelectedPersons != null && mSelectedPersons.size() > 0)
		{
			ArrayList<Person> uris = new ArrayList<Person>();
			for (Person person : mSelectedPersons)
			{
				if (!TextUtils.isEmpty(person.getLookupURI()))
				{
					uris.add(person);
				}
			}
			return uris;
		}
		return null;
	}

	public String getNamesList(boolean shortened)
	{
		String name = "";
		if (mSelectedPersons.size() < 3)
		{
			for (Person person : mSelectedPersons)
			{
				name += person.getName() + "\n";
			}
		} else if (mSelectedPersons.size() > 2)
		{
			if (shortened)
			{
				for (int i = 0; i <= 2; i++)
				{
					name += mSelectedPersons.get(i).getName() + "\n";
				}
				name += "and more...";
			} else
			{
				for (int i = 0; i <= 5; i++)
				{
					name += mSelectedPersons.get(i).getName() + "\n";
				}
				if (mSelectedPersons.size() > 5)
				{
					name += "and more...";
				}
			}
		}
		return name;
	}

	/**
	 * Create a list of all new debts to add into the database
	 *
	 * @return A list of debts that can be iterated through
	 */
	public List<Debt> getNewDebts()
	{
		BigDecimal debtAmount = getAmountToApply();

		List<Debt> debts = new ArrayList<Debt>();
		for (Person person : mSelectedPersons)
		{
			debts.add(new Debt(0, person.getRepayID(), getDate(), debtAmount, mDescription));
		}
		return debts;
	}

	/**
	 * Quickly get the amount that will be applied to each person
	 *
	 * @return {@link java.math.BigDecimal} representation of the amount
	 */
	public BigDecimal getAmountToApply()
	{
		BigDecimal debtAmount;
		if (distributeEvenly)
		{
			if (includingMe)
			{
				debtAmount = mAmount.divide(new BigDecimal(mSelectedPersons.size() + 1), RoundingMode.CEILING);
			} else
			{
				debtAmount = mAmount.divide(new BigDecimal(mSelectedPersons.size()), BigDecimal.ROUND_CEILING);
			}
		} else
		{
			debtAmount = mAmount;
		}
		if (!owesMe)
		{
			debtAmount = debtAmount.negate();
		}
		return debtAmount;
	}

	public List<Person> getUpdatedFriends()
	{
		BigDecimal debtAmount = getAmountToApply();
		ArrayList<Person> updated = mSelectedPersons;
		for (Person person : updated)
		{
			person.setDebt(person.getDebt().add(debtAmount));
		}
		return updated;
	}
}
