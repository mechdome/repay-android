package com.repay.android.frienddetails;

import com.repay.android.model.Friend;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 */

public interface AmountUpdatedCallback {
    /**
     * Call when a debt amount is changed and the total in the other
     * fragment needs to be updated
     * @param friend Friend object with updated amount
     */
    public void onDebtTotalUpdated(Friend friend);
}
