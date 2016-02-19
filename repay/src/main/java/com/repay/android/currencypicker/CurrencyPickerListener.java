package com.repay.android.currencypicker;

/**
 * Inform the client which country has been selected
 *
 */
public interface CurrencyPickerListener {
	public void onSelectCountry(String name, String currencySymbol);
}

