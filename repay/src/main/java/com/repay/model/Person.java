package com.repay.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class Person implements Comparable<Person>, Serializable
{
	private String name, repayID;
	private BigDecimal debt;
	private String lookupURI;
	private List<Debt> debts;

	public Person(String repayID, String lookupURI, String name, BigDecimal debt)
	{
		super();
		this.lookupURI = lookupURI;
		this.name = name;
		this.repayID = repayID;
		this.debt = debt;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getRepayID()
	{
		return repayID;
	}

	public String getLookupURI()
	{
		return lookupURI;
	}

	public BigDecimal getDebt()
	{
		return debt;
	}

	public void setDebt(BigDecimal debt)
	{
		this.debt = debt;
	}

	public List<Debt> getDebts()
	{
		return debts;
	}

	public void setDebts(List<Debt> debts)
	{
		this.debts = debts;
	}

	@Override
	public int compareTo(Person another)
	{
		return another.getDebt().compareTo(debt);
	}

	@Override
	public boolean equals(Object o)
	{
		return (o.getClass() == Person.class && ((Person) o).getRepayID().equals(repayID));
	}
}
