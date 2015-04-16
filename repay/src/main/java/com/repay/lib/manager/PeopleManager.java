package com.repay.lib.manager;

import com.repay.model.Person;

import java.util.List;

/**
 * Created by Matt Allen
 * 16/04/15
 * mattallen092@gmail.com
 */
public class PeopleManager
{
	private static final String FILE_PEOPLE = "people.json";

	private static PeopleManager instance;

	public static PeopleManager getInstance()
	{
		if (instance == null)
		{
			synchronized (PeopleManager.class)
			{
				if (instance == null)
				{
					instance = new PeopleManager();
				}
			}
		}
		return instance;
	}

	public List<Person> getPeople()
	{
		return null;
	}
}
