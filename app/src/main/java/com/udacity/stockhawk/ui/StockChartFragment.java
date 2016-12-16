package com.udacity.stockhawk.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 */
public class StockChartFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String ARG_SYMBOL = "symbol";
	private static final int STOCK_LOADER = 0;

	private String symbol;

	private OnFragmentInteractionListener mListener;
	private LineChart chart;

	public StockChartFragment() {
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param symbol Stock symbol
	 * @return A new instance of fragment StockChartFragment.
	 */
	public static StockChartFragment newInstance(String symbol) {
		StockChartFragment fragment = new StockChartFragment();
		Bundle args = new Bundle();
		args.putString(ARG_SYMBOL, symbol);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			symbol = getArguments().getString(ARG_SYMBOL);
			getLoaderManager().initLoader(STOCK_LOADER, null, this);

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.fragment_stock_chart, container, false);

		chart = (LineChart) view.findViewById(R.id.chart);



		return view;
	}


	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		Timber.d("onCreateLoader, symbol: %s", symbol);

		return new CursorLoader(getActivity(),
				Contract.Quote.makeUriForStock(symbol),
				Contract.Quote.QUOTE_COLUMNS,
				null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (data.getCount() != 0) {
			updateChart(data);
		} else {
			chart.setData(null);
			chart.invalidate(); // refresh
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		chart.setData(null);
		chart.invalidate(); // refresh
	}

	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(Uri uri);
	}




	@NonNull
	private void updateChart(Cursor cursor) {

		Timber.d("creating chart data for symbol: %s", symbol);

		if (cursor.isBeforeFirst()) {
			cursor.moveToFirst();
		}

		String historyString = cursor.getString(Contract.Quote.POSITION_HISTORY);

		Timber.d("quote history for %s = %s",symbol, historyString);

		List<Entry> priceEntries = new ArrayList<Entry>();

		String[] lines = historyString.split("\n");

		Calendar cal = Calendar.getInstance();

		final ArrayList<Date> days = new ArrayList<>(lines.length);

		for (int i=0; i<lines.length;i++) {

			String line = lines[i];

			String[] parts = line.split(", ");

			if (parts.length==2) {

				cal.setTimeInMillis(Long.valueOf(parts[0]));
				Date date = cal.getTime();

				days.add(date);

				Float price = Float.parseFloat(parts[1]);

				Timber.d("Quote %s: %s = %s", i, date, price);

				priceEntries.add(new Entry(Float.valueOf(i), price));
			}

		}

		/*
		priceEntries.add(new Entry(0f, 100f));
		priceEntries.add(new Entry(1f, 90f));
		priceEntries.add(new Entry(2f, 80f));
		priceEntries.add(new Entry(3f, 85f));*/

		LineDataSet setComp1 = new LineDataSet(priceEntries, symbol);
		setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

		List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
		dataSets.add(setComp1);

		LineData chartData = new LineData(dataSets);



		IAxisValueFormatter formatter = new IAxisValueFormatter() {

			@Override
			public String getFormattedValue(float value, AxisBase axis) {
				Date date = days.get((int) value);
				return PrefUtils.format(date);
			}

		};

		XAxis xAxis = chart.getXAxis();
		xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
		xAxis.setValueFormatter(formatter);

		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setDrawGridLines(false);


		YAxis leftAxis = chart.getAxisLeft();
		leftAxis.setDrawLabels(false);

		leftAxis.setDrawGridLines(false);

		YAxis rightAxis = chart.getAxisRight();
		rightAxis.setEnabled(false);


		chart.setData(chartData);

		chart.setAutoScaleMinMaxEnabled(true);
		chart.setDrawBorders(false);

		chart.setDrawGridBackground(false);


		chart.invalidate(); // refresh

	}
}
