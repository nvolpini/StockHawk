package com.udacity.stockhawk.ui.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 *
 * https://android.googlesource.com/platform/development/+/master/samples/WeatherListWidget/src/com/example/android/weatherlistwidget/WeatherWidgetProvider.java
 */
public class WidgetProvider extends AppWidgetProvider {

	public static String REFRESH_ACTION = "com.udacity.stockhawk.ui.widget.REFRESH";

	private static WidgetDataProviderObserver sDataObserver;
	private static HandlerThread sWorkerThread;
	private static Handler sWorkerQueue;

	public WidgetProvider() {
		// Start the worker thread
		sWorkerThread = new HandlerThread("StockWidgetProvider-worker");
		sWorkerThread.start();
		sWorkerQueue = new Handler(sWorkerThread.getLooper());
	}

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
								int appWidgetId) {

		Timber.d("updateAppWidget(%s)", appWidgetId);

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider_layout);
//        views.setTextViewText(R.id.appwidget_text, widgetText);


		Intent intent = new Intent(context, WidgetService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

		// Set up the collection
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			setRemoteAdapter(context, views, intent);
		} else {
			setRemoteAdapterV11(context, views, intent);
		}


		// Bind the click intent for the refresh button on the widget
		/*final Intent refreshIntent = new Intent(context, WidgetProvider.class);
		refreshIntent.setAction(WidgetProvider.REFRESH_ACTION);
		final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,
				refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.refresh, refreshPendingIntent);
		*/

		// Instruct the widget manager to update the widget
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

	/**
	 * Sets the remote adapter used to fill in the list items
	 *
	 * @param views RemoteViews to set the RemoteAdapter
	 * @param intent
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views, Intent intent) {
		views.setRemoteAdapter(R.id.widgetCollectionList, intent);
	}

	/**
	 * Sets the remote adapter used to fill in the list items
	 *
	 * @param views RemoteViews to set the RemoteAdapter
	 * @param intent
	 */
	@SuppressWarnings("deprecation")
	private static void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views, Intent intent) {
		views.setRemoteAdapter(0, R.id.widgetCollectionList, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Timber.d("onUpdate");

		// There may be multiple widgets active, so update all of them
		for (int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		// Register for external updates to the data to trigger an update of the widget.  When using
		// content providers, the data is often updated via a background service, or in response to
		// user interaction in the main app.  To ensure that the widget always reflects the current
		// state of the data, we must listen for changes and update ourselves accordingly.
		final ContentResolver r = context.getContentResolver();
		if (sDataObserver == null) {
			final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
			final ComponentName cn = new ComponentName(context, WidgetProvider.class);
			sDataObserver = new WidgetDataProviderObserver(mgr, cn, sWorkerQueue);
			//TODO nao tem uri geral pois o controle da relacao de stock e nas prefs
			r.registerContentObserver(Contract.Quote.uri, true, sDataObserver);
			Timber.d("setting observer...");
		}
	}

	@Override
	public void onDisabled(Context context) {
		// Enter relevant functionality for when the last widget is disabled
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		Timber.d("onReceive() - action: %s", action);

		super.onReceive(context, intent);
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
										  int appWidgetId, Bundle newOptions) {
		int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
		int maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
		int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
		int maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
		/*RemoteViews layout;
		if (minHeight < 100) {
			mIsLargeLayout = false;
		} else {
			mIsLargeLayout = true;
		}
		layout = buildLayout(context, appWidgetId, mIsLargeLayout);
		appWidgetManager.updateAppWidget(appWidgetId, layout);*/
		Timber.d("onAppWidgetOptionsChanged");

		updateAppWidget(context, appWidgetManager, appWidgetId);

	}
}

