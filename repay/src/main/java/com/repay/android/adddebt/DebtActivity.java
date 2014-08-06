package com.repay.android.adddebt;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.repay.android.model.Friend;

import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 */

public abstract class DebtActivity extends FragmentActivity {

	public abstract BigDecimal getAmount();
	
	public abstract void onNextBtn(View v);
	
	public abstract ArrayList<Friend> getSelectedFriends();
	
	protected abstract void submitToDB();
	
	public abstract void setAmount(BigDecimal amount);
	
	public abstract void setSelectedFriends(ArrayList<Friend> friends);
}
