package com.udacity.stockhawk.ui.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.MainActivity;

import timber.log.Timber;

/**
 * Created by neimar on 10/01/17.
 */

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

			private Context mContext;
	private Cursor mCursor;
	private int mAppWidgetId;

	public StackRemoteViewsFactory(Context context, Intent intent) {
		mContext = context;
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	public void onCreate() {
		// Since we reload the cursor in onDataSetChanged() which gets called immediately after
		// onCreate(), we do nothing here.
	}

	public void onDestroy() {
		if (mCursor != null) {
			mCursor.close();
		}
	}

	public int getCount() {
		return mCursor.getCount();
	}

	public RemoteViews getViewAt(int position) {
		// Get the data for this position from the content provider
		String day = "Unknown Day";
		float temp = 0;
		if (mCursor.moveToPosition(position)) {
			final int dayColIndex = mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
			final int tempColIndex = mCursor.getColumnIndex(Contract.Quote.COLUMN_PRICE);
			day = mCursor.getString(dayColIndex);
			temp = mCursor.getFloat(tempColIndex);
		}

		Timber.d("getViewAt(%s) = %s", position, day);
		// Retrieve all of the Today widget ids: these are the widgets we need to update
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(mContext,
				WidgetProvider.class));



		// Find the correct layout based on the widget's width
		int widgetWidth = getWidgetWidth(appWidgetManager, mAppWidgetId);
		int defaultWidth = mContext.getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
		int largeWidth =  mContext.getResources().getDimensionPixelSize(R.dimen.widget_today_large_width);


		int layoutId;
		if (widgetWidth >= largeWidth) {
			layoutId = R.layout.list_item_quote;
		} else if (widgetWidth >= defaultWidth) {
			layoutId = R.layout.list_item_quote_medium;
		} else {
			layoutId = R.layout.list_item_quote_small;
		}

		Timber.d("widget width: %s, layoutId: %s", widgetWidth, layoutId);

		RemoteViews rv = new RemoteViews(mContext.getPackageName()
				//, android.R.layout.simple_list_item_1
				//, R.layout.list_item_quote
				,layoutId

			);

		//rv.setTextViewText(android.R.id.text1, day);
		rv.setTextViewText(R.id.symbol, mCursor.getString(Contract.Quote.POSITION_SYMBOL));
		rv.setTextViewText(R.id.price, PrefUtils.format(mCursor.getFloat(Contract.Quote.POSITION_PRICE)));



		float rawAbsoluteChange = mCursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
		float percentageChange = mCursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

		if (rawAbsoluteChange > 0) {
			rv.setInt(R.id.change,"setBackgroundResource", R.drawable.percent_change_pill_green);
			//holder.change.setBackgroundResource(R.drawable.percent_change_pill_green);
		} else {
			rv.setInt(R.id.change,"setBackgroundResource", R.drawable.percent_change_pill_red);
			//holder.change.setBackgroundResource(R.drawable.percent_change_pill_red);
		}

		String change = PrefUtils.dollarFormatWithPlus(rawAbsoluteChange);
		String percentage = PrefUtils.percentageFormat(percentageChange / 100);

		if (PrefUtils.getDisplayMode(mContext)
				.equals(mContext.getString(R.string.pref_display_mode_absolute_key))) {
			rv.setTextViewText(R.id.change,change);
		} else {
			//holder.change.setText(percentage);
			rv.setTextViewText(R.id.change,percentage);
		}

		//TODO handle click

		// Create an Intent to launch MainActivity
		Intent intent = new Intent(mContext, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
		rv.setOnClickPendingIntent(mAppWidgetId, pendingIntent);

		/*
		// Set the click intent so that we can handle it and show a toast message
		final Intent fillInIntent = new Intent();
		final Bundle extras = new Bundle();
		extras.putString(WeatherWidgetProvider.EXTRA_DAY_ID, day);
		fillInIntent.putExtras(extras);
		rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
		*/
		return rv;
	}

	private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
		// Prior to Jelly Bean, widgets were always their default size
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			return mContext.getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
		}
		// For Jelly Bean and higher devices, widgets can be resized - the current size can be
		// retrieved from the newly added App Widget Options
		return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
		Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
		if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
			int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
			// The width returned is in dp, but we'll convert it to pixels to match the other widths
			DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
			return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
					displayMetrics);
		}
		return  mContext.getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	private void setRemoteContentDescription(RemoteViews views, String description) {
		//views.setContentDescription(R.id.widget_icon, description);
	}
	public RemoteViews getLoadingView() {
		// We aren't going to return a default loading view in this sample
		return null;
	}

	public int getViewTypeCount() {
		// Technically, we have two types of views (the dark and light background views)
		return 2;
	}

	public long getItemId(int position) {
		return position;
	}

	public boolean hasStableIds() {
		return true;
	}

	public void onDataSetChanged() {
		// Refresh the cursor
		if (mCursor != null) {
			mCursor.close();
		}
		Timber.d("onDataSetChanged");

		mCursor = mContext.getContentResolver().query(Contract.Quote.uri, null, null, null, null);
	}
}