package com.chrslee.csgopedia.app;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;

/**
 * Formats currency (eg: USD should be X.XX, JPY should be X, etc).
 */
public class CurrencyFormatter {
    // https://www.codota.com/android/scenarios/52fcbcbbda0a2bf89c4a97c7/java.util.Currency?tag=dragonfly&fullSource=1
    public static String formatCurrencyValue(BigDecimal amount, Currency currency) {
        NumberFormat currencyFormatter = NumberFormat.getInstance();
        if (currencyFormatter == null) {
            currencyFormatter = NumberFormat.getInstance();
        }
        currencyFormatter.setGroupingUsed(false);
        currencyFormatter.setMaximumFractionDigits(currency
                .getDefaultFractionDigits());
        return currencyFormatter
                .format(amount.setScale(currency.getDefaultFractionDigits(),
                        BigDecimal.ROUND_HALF_EVEN));
    }
}
