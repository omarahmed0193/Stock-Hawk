package com.udacity.stockhawk.graph;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.beans.GraphData;
import com.udacity.stockhawk.beans.Quote;
import com.udacity.stockhawk.beans.StockGraphQuery;
import com.udacity.stockhawk.retrofit.ServiceGenerator;
import com.udacity.stockhawk.retrofit.StockApiService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.udacity.stockhawk.R.id.chart;

public class GraphActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(chart)
    LineChart mChart;

    private MaterialDialog mProgressDialog;

    public static final String EXTRA_QUOTE = "quote";
    private Quote mQuote;
    private ArrayList<GraphData> mGraphDataList;
    private static final String ARGS_GRAPH_DATA = "graph_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mQuote = getIntent().getParcelableExtra(EXTRA_QUOTE);
        getSupportActionBar().setTitle(mQuote.getName());

        initGraph();
        if (savedInstanceState == null) {
            fetchChartData();
        } else {
            mGraphDataList = savedInstanceState.getParcelableArrayList(ARGS_GRAPH_DATA);
            setGraphChartData();
        }

        mProgressDialog = new MaterialDialog.Builder(this)
                .content(R.string.fetching_data)
                .progress(true, 0)
                .cancelable(false)
                .build();
        mProgressDialog.show();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(ARGS_GRAPH_DATA, mGraphDataList);
        super.onSaveInstanceState(outState);
    }

    private void initGraph() {
        Description description = new Description();
        description.setText("Past 30 days stocks graph");
        description.setTextColor(Color.WHITE);
        mChart.setDescription(description);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(false);
        mChart.getLegend().setEnabled(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getAxisLeft().setTextColor(Color.WHITE);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisRight().setEnabled(false);
        mChart.animateXY(1000, 1000);
    }

    private void fetchChartData() {
        final Calendar calendar = Calendar.getInstance();
        String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime());
        calendar.add(Calendar.MONTH, -1);
        String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime());

        final String query = "select * from yahoo.finance.historicaldata where symbol=\"" +
                mQuote.getSymbol() +
                "\" and startDate=\"" + startDate + "\" and endDate=\"" + endDate + "\"";


        StockApiService stockApiService = ServiceGenerator.createService(StockApiService.class);
        stockApiService.getStocksWithDateSpan(query).enqueue(new Callback<StockGraphQuery>() {
            @Override
            public void onResponse(Call<StockGraphQuery> call, Response<StockGraphQuery> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    mGraphDataList = new ArrayList<>();
                    for (StockGraphQuery.Quote quote : response.body().getQuotes()) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Calendar cal = Calendar.getInstance();
                        try {
                            cal.setTime(simpleDateFormat.parse(quote.getDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        mGraphDataList.add(new GraphData(cal.get(Calendar.DAY_OF_MONTH), Float.valueOf(quote.getHigh())));
                    }
                    Collections.sort(mGraphDataList, new Comparator<GraphData>() {
                        @Override
                        public int compare(GraphData graphData, GraphData t1) {
                            return graphData.getDayOfTheMonth().compareTo(t1.getDayOfTheMonth());
                        }
                    });
                    setGraphChartData();
                }
            }

            @Override
            public void onFailure(Call<StockGraphQuery> call, Throwable t) {
                mProgressDialog.dismiss();
            }
        });
    }

    private void setGraphChartData() {
        List<Entry> entries = new ArrayList<>();
        for (GraphData data : mGraphDataList) {
            entries.add(new Entry(data.getDayOfTheMonth(), data.getStockValue()));
        }
        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.CYAN);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setValueTextColor(Color.WHITE);
        LineData lineData = new LineData(dataSet);
        mChart.setData(lineData);
        mChart.invalidate();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
