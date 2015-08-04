package com.zhack.poskasir.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by zunaidi.chandra on 04/08/2015.
 */
public class Utils {

    public static String convertRp(int num) {
        DecimalFormat currencyIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        currencyIndonesia.setMaximumFractionDigits(0);
        currencyIndonesia.setDecimalFormatSymbols(formatRp);

        return currencyIndonesia.format(num);
    }
}
