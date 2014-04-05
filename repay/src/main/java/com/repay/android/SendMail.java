package com.repay.android;

import java.math.BigDecimal;

import com.repay.android.settings.SettingsFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 */

public class SendMail {
	
	private static final String TAG = SendMail.class.getName();
	
	/**
	 * Start intent for sending an email to my email with a set subject.
	 * @param c The Context to run in
	 */
	public static void sendFeedback(Context c){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mattallen092@gmail.com"});
		intent.putExtra(Intent.EXTRA_SUBJECT, "Repay Feedback");
		Log.i(TAG, "Opening feedback email intent...");
		c.startActivity(Intent.createChooser(intent, "Send Email"));
		Log.i(TAG, "Successful.");
	}
	
	/**
	 * Send an email to a person telling them the state of the debt
	 * 
	 * @param c Context to run in
	 * @param amount Negate if you owe them
	 * @param email Address to send to
	 */
	public static void emailFriend(Context c, BigDecimal amount, String email){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
		intent.putExtra(Intent.EXTRA_SUBJECT, SettingsFragment.getCurrencySymbol(c)+SettingsFragment.getFormattedAmount(amount));
		String emailBody;
		if(amount.compareTo(BigDecimal.ZERO)>0){
			// Positive number
			emailBody = "Hey, it seems you owe me "+ SettingsFragment.getCurrencySymbol(c) + SettingsFragment.getFormattedAmount(amount);
		} else {
			// Negative number
			emailBody = "Hey, I owe you " + SettingsFragment.getCurrencySymbol(c) + SettingsFragment.getFormattedAmount(amount.negate()) + ", I'll pay you back soon";
		}
		intent.putExtra(Intent.EXTRA_TEXT, emailBody);
		Log.i(TAG, "Opening feedback email intent...");
		c.startActivity(Intent.createChooser(intent, "Send Email"));
		Log.i(TAG, "Successful.");
	}
	
	public static void smsFriend(Context c, String phoneNumber, BigDecimal amount){
		// Set the body of the text
		String smsBody;
		Log.d(TAG, "Begin sending SMS...");
		if(amount.compareTo(BigDecimal.ZERO)>0){
			// Positive number
			smsBody = "Hey, it seems you owe me "+ SettingsFragment.getCurrencySymbol(c) + SettingsFragment.getFormattedAmount(amount);
		} else {
			// Negative number
			smsBody = "Hey, I owe you " + SettingsFragment.getCurrencySymbol(c) + SettingsFragment.getFormattedAmount(amount.negate()) + ", I'll pay you back soon";
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("sms:"+phoneNumber));
		intent.putExtra("sms_body", smsBody);
		c.startActivity(intent);
		Log.d(TAG, "SMS sent");
	}
}
