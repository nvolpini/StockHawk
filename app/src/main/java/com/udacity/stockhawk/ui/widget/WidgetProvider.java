package com.udacity.stockhawk.ui.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
								int appWidgetId) {
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
		// There may be multiple widgets active, so update all of them
		for (int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		// Enter relevant functionality for when the first widget is created
	}

	@Override
	public void onDisabled(Context context) {
		// Enter relevant functionality for when the last widget is disabled
	}

	//@Override
	public void onUpdateX(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int widgetId : appWidgetIds) {
			RemoteViews mView = initViews(context, appWidgetManager, widgetId);
			appWidgetManager.updateAppWidget(widgetId, mView);
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private RemoteViews initViews(Context context,
								  AppWidgetManager widgetManager, int widgetId) {

		RemoteViews mView = new RemoteViews(context.getPackageName(),
				R.layout.widget_provider_layout);

		Intent intent = new Intent(context, WidgetService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		mView.setRemoteAdapter(widgetId, R.id.widgetCollectionList, intent);

		return mView;
	}

}

