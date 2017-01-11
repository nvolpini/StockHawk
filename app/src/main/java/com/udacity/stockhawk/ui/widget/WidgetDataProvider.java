package com.udacity.stockhawk.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neimar on 10/01/17.
 */

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

	List<String> mCollections = new ArrayList<>();

	Context mContext = null;

	public WidgetDataProvider(Context context, Intent intent) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return mCollections.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		RemoteViews mView = new RemoteViews(mContext.getPackageName(),
				android.R.layout.simple_list_item_1);
		mView.setTextViewText(android.R.id.text1, mCollections.get(position));

		mView.setTextColor(android.R.id.text1, Color.BLACK);
		return mView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
		initData();
	}

	@Override
	public void onDataSetChanged() {
		initData();
	}

	private void initData() {
		mCollections.clear();
		for (int i = 1; i <= 10; i++) {
			mCollections.add("ListView item " + i);
		}
	}

	@Override
	public void onDestroy() {

	}

}
