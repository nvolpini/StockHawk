package com.udacity.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.udacity.stockhawk.R;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class PrefUtils {

    private PrefUtils() {
    }

    public static Set<String> getStocks(Context context) {
        String stocksKey = context.getString(R.string.pref_stocks_key);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		Set<String> currentPrefs = prefs.getStringSet(stocksKey, new HashSet<String>());

/*        String initializedKey = context.getString(R.string.pref_stocks_initialized_key);
        String[] defaultStocksList = context.getResources().getStringArray(R.array.default_stocks);

        HashSet<String> defaultStocks = new HashSet<>(Arrays.asList(defaultStocksList));

        boolean initialized = prefs.getBoolean(initializedKey, false);

        if (currentPrefs.size() == 0 && !initialized) {
			Timber.d("initializing default stocks...");

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(initializedKey, true);
            editor.putStringSet(stocksKey, defaultStocks);
			currentPrefs = defaultStocks;
            editor.apply();
            return defaultStocks;
        }*/
        return currentPrefs;

    }

    public static void editStockPref(Context context, String symbol, Boolean add) {
        String key = context.getString(R.string.pref_stocks_key);
        Set<String> stocks = getStocks(context);

        if (add) {
            stocks.add(symbol);
        } else {
            stocks.remove(symbol);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, stocks);
        //editor.apply();
		editor.commit();
    }

    public static void addStock(Context context, String symbol) {
        editStockPref(context, symbol, true);
    }

    public static void removeStock(Context context, String symbol) {
        editStockPref(context, symbol, false);
    }

    public static boolean stockExists(Context context, String symbol) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		return getStocks(context).contains(symbol);

    }

	public static boolean validateSymbol(Context context, String symbol) {

		//return QuoteSyncJob.getStock(context, symbol) != null;

		return true;
	}


    public static String getDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static void toggleDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String absoluteKey = context.getString(R.string.pref_display_mode_absolute_key);
        String percentageKey = context.getString(R.string.pref_display_mode_percentage_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String displayMode = getDisplayMode(context);

        SharedPreferences.Editor editor = prefs.edit();

        if (displayMode.equals(absoluteKey)) {
            editor.putString(key, percentageKey);
        } else {
            editor.putString(key, absoluteKey);
        }

        editor.apply();
    }

	private static DateFormat SHORT_DATE_FORMAT = DateFormat.getDateInstance(DateFormat.SHORT);

	private static NumberFormat DECIMAL_FORMAT = DecimalFormat.getInstance();



	public static String format(Float number) {

		return dollarFormat.format(new BigDecimal(number.toString()));

	}
	private static DecimalFormat dollarFormatWithPlus;
	private static DecimalFormat dollarFormat;
	private static DecimalFormat percentageFormat;

	static {
		dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
		dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
		dollarFormatWithPlus.setPositivePrefix("+$");
		percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
		percentageFormat.setMaximumFractionDigits(2);
		percentageFormat.setMinimumFractionDigits(2);
		percentageFormat.setPositivePrefix("+");
	}

	public static String format(Date date) {

		return date == null ? null :  SHORT_DATE_FORMAT.format(date);

	}


	public static String getCurrencySymbol() {
		return null;
	}

	public static String dollarFormatWithPlus(float rawAbsoluteChange) {
		return dollarFormatWithPlus.format(rawAbsoluteChange);

	}

	public static String percentageFormat(float v) {
		return percentageFormat.format(v);
	}
}
