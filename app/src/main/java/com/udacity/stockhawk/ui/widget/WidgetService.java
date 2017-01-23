package com.udacity.stockhawk.ui.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by neimar on 10/01/17.
 */

public class WidgetService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {

		return new StackRemoteViewsFactory(getApplicationContext(), intent);

	}

}
