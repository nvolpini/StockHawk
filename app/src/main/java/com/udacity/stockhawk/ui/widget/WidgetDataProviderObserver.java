package com.udacity.stockhawk.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.database.ContentObserver;
import android.os.Handler;

import com.udacity.stockhawk.R;

import timber.log.Timber;

/**
 * Created by neimar on 10/01/17.
 *
 * https://android.googlesource.com/platform/development/+/master/samples/WeatherListWidget/src/com/example/android/weatherlistwidget
 *
 */

public class WidgetDataProviderObserver extends ContentObserver {
	private AppWidgetManager mAppWidgetManager;
	private ComponentName mComponentName;
	WidgetDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
		super(h);
		mAppWidgetManager = mgr;
		mComponentName = cn;
	}
	@Override
	public void onChange(boolean selfChange) {

		Timber.d("onChange");

		// The data has changed, so notify the widget that the collection view needs to be updated.
		// In response, the factory's onDataSetChanged() will be called which will requery the
		// cursor for the new data.
		mAppWidgetManager.notifyAppWidgetViewDataChanged(
				mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.widgetCollectionList);
	}
}
