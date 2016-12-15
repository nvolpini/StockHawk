package com.udacity.stockhawk;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;

import timber.log.Timber;

public class StockHawkApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());

			initializeStetho(this);

		}
    }
	private void initializeStetho(final Context context) {

		Stetho.initialize(Stetho.newInitializerBuilder(context)
				.enableDumpapp(new DumperPluginsProvider() {
					@Override
					public Iterable<DumperPlugin> get() {
						return new Stetho.DefaultDumperPluginsBuilder(context)
								//.provide(new MyDumperPlugin())
								.finish();
					}
				})
				.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
				.build());
		/*
		new OkHttpClient.Builder()
				.addNetworkInterceptor(new StethoInterceptor())
				.build();*/

	}
}
