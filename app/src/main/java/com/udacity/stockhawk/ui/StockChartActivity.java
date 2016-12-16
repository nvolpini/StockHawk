package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;

import timber.log.Timber;

public class StockChartActivity extends AppCompatActivity implements StockChartFragment.OnFragmentInteractionListener {

	private static final String SYMBOL_KEY = StockChartActivity.class.getName().concat(".symbol");

	public static Intent newIntent(Context context, String symbol) {
		Intent intent = new Intent(context, StockChartActivity.class);
		intent.putExtra(SYMBOL_KEY, symbol);
		return intent;
	}

	private String getSymbol() {
		return getIntent().getStringExtra(SYMBOL_KEY);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_chart);

		if (savedInstanceState == null) {

			Timber.d("setting StockChartFragment args... ");

			StockChartFragment f = StockChartFragment.newInstance(getSymbol());

			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, f)
					.commit();
		}

	}

	@Override
	public void onFragmentInteraction(Uri uri) {

	}
}
