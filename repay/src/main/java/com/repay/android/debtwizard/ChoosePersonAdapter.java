package com.repay.android.debtwizard;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.repay.android.MainApplication;
import com.repay.android.R;
import com.repay.model.Person;

import java.util.ArrayList;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class ChoosePersonAdapter extends ArrayAdapter<Person>
{
	public static final int SELECTED_COLOUR = Color.parseColor("#FFC3BB"); // Same as "Selected Tint" under colors.xml
	public static final int DESELECTED_COLOUR = Color.parseColor("#00FFFFFF"); // Invisible
	private int mLayoutID;
	private ArrayList<Person> mPersons, mSelectedPersons;

	public ChoosePersonAdapter(Context context, int layoutId, ArrayList<Person> persons, ArrayList<Person> selectedPersons)
	{
		super(context, layoutId, persons);
		this.mLayoutID = layoutId;
		this.mPersons = persons;
		this.mSelectedPersons = selectedPersons;
	}

	public void setSelectedFriends(ArrayList<Person> selected)
	{
		mSelectedPersons = selected;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;

		if (v == null)
		{
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(mLayoutID, null);
		}
		Person person = mPersons.get(position);
		TextView name = (TextView) v.findViewById(R.id.name);
		final ImageView pic = (ImageView) v.findViewById(R.id.image);
		if (person != null)
		{
			v.setTag(person); // Stored as a tag to be retrieved later for OnItemClickListener

			// Colour the list item based on whether it is in the 'selected' list
			if (mSelectedPersons.contains(person))
			{
				v.setBackgroundColor(SELECTED_COLOUR);
			} else
			{
				v.setBackgroundColor(DESELECTED_COLOUR);
			}
		}
		ImageLoader.getInstance().displayImage(person.getLookupURI(), pic, MainApplication.getImageOptions());
		name.setText(person.getName());

		return v;
	}
}
