package com.repay.android.settings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import com.repay.android.R;
import com.repay.android.SendMail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 */

public class SettingsFragment extends PreferenceFragment {
	
	private static final String PREF_KEY_CURRENCY = "currencies_list";
	private static final String PREF_KEY_DATEFORMAT = "dateformat_list";
	private static final String PREF_KEY_USE_GRID_VIEW = "useGridView";
    private static final String PREF_KEY_DEBTHISTORY_COLOURS = "debthistoryColours";
    private static final String PREF_KEY_SORTORDER = "sortOrder";
    
    public static final int DEBTHISTORY_GREEN_RED = 1;
    public static final int DEBTHISTORY_GREEN_BLUE = 2;
    
    public static final int SORTORDER_OWEME = 1;
    public static final int SORTORDER_OWETHEM = 2;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);
        // Response for when the feedback option is used
        findPreference("feedback").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				SendMail.sendFeedback(getActivity());
				return true;
			}
		});
    }
	
	/**
	 * @param c The Context to operate in
	 * @return The currency symbol based on the preference set by the user. If not preference has been set, the GBP symbol will be returned
	 */
	public static String getCurrencySymbol(Context c){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		String currencyPref = prefs.getString(PREF_KEY_CURRENCY, Currency.getInstance(Locale.getDefault()).getSymbol());
		if(currencyPref.equals("1")){
			Currency currency = Currency.getInstance(Locale.UK);
			return currency.getSymbol();
		}
		else if(currencyPref.equals("2")){
			Currency currency = Currency.getInstance(Locale.US);
			return currency.getSymbol();
		}
		else if(currencyPref.equals("3")){
			Currency currency = Currency.getInstance(Locale.GERMANY);
			return currency.getSymbol();
		}
		else if(currencyPref.equals("4")){
			Currency currency = Currency.getInstance(Locale.getDefault());
			return currency.getSymbol();
		}
		return currencyPref;
	}
	
	/**
	 * @param c The Context to operate in
	 * @param date The date as returned from the database
	 * @return The date, formatted as the user preference specifies
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getFormattedDate(Context c, Date date){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// Add 1 to the month because it returns one less than it should
		String dateString = cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);
		String currencyPref = prefs.getString(PREF_KEY_DATEFORMAT, dateString);
		if(currencyPref.equals("2")){
			dateString = (cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DATE)+"/"+cal.get(Calendar.YEAR);
		}
		else if(currencyPref.equals("3")){
			SimpleDateFormat format = new SimpleDateFormat("EEE d MMM yyyy");
			dateString = format.format(date);
		}
		return dateString;
	}
	
	/**
	 * @param amount The amount returned from the database
	 * @return The amount specified, in a consistent format
	 */
	public static String getFormattedAmount(BigDecimal amount){
		String str;
		str = amount.setScale(2, RoundingMode.CEILING).toString();
		return str;
	}
	
	/**
	 * @param c The Context to operate in
	 * @return True if the GridView is to be used
     * @deprecated GridView is now the only way of showing data
	 */
    @Deprecated
	public static boolean useGridViewOnPhone(Context c){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		return prefs.getBoolean(PREF_KEY_USE_GRID_VIEW, true);
	}
    
    /**
     * Get preference on colours used in DebtHistoryFragment
     * @param c Context to run in
     * @return Integer representation of preference. Use constants to reveal setting
     */
    public static int getDebtHistoryColourPreference(Context c){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return Integer.parseInt(prefs.getString(PREF_KEY_DEBTHISTORY_COLOURS, "1"));
    }
    
    /**
     * @param c
     * @return The order repesented by an int
     */
    public static int getSortOrder(Context c){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
    	
        return Integer.parseInt(prefs.getString(PREF_KEY_SORTORDER, "1"));
    }
}
