package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        Intent myIntent = getIntent();
        Bundle myBundle = myIntent.getExtras();
        String symbolName = myBundle.getString("symbol");
        String price = myBundle.getString("price");
        Cursor mCursor ;



        mCursor = getApplicationContext().getContentResolver().query(QuoteProvider.Quotes.withSymbol(symbolName),null,
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null
        );
        mCursor.moveToFirst();
        String name = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.NAME));
        String yearLow = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.YEAR_LOW));
        String yearHigh = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.YEAR_HIGH));

        // Setting the views
        TextView headingView = (TextView) findViewById(R.id.HeadingView);
        headingView.setText(name);
        TextView lowView = (TextView) findViewById(R.id.lowView);
        lowView.setText("Yearly Low: " + yearLow);
        TextView highView = (TextView) findViewById(R.id.highView);
        highView.setText("Yearly High: " + yearHigh);







        // Monthly graph to be created now

        final SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        String startDate = myFormat.format(c.getTime());
        c.add(Calendar.YEAR,-1);
        String endDate = myFormat.format(c.getTime());
        OkHttpClient client = new OkHttpClient();

         final ArrayList<Entry> entries = new ArrayList<Entry>() ;
        final ArrayList<String> labels = new ArrayList<>();
        Request request = new Request.Builder()

                .url("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22" +
                         symbolName + "%22%20and%20startDate%20%3D%20%22"+ endDate + "%22%20and%20endDate%20%3D%20%22" + startDate + "%22&format=json" +
                        "&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=").build();

        Log.e("URL : ",request.urlString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

                Log.e("Failed to ","get the data");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String myJson = response.body().string();
                JSONObject myReader = null;
                try {
                     myReader = new JSONObject(myJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
             if (myReader != null){
                 try {
                     JSONObject queryObj = myReader.getJSONObject("query");
                     JSONObject result = queryObj.getJSONObject("results");
                     JSONArray quotes = result.getJSONArray("quote");
                    for (int i = quotes.length() - 1 , k =0 ; i >= 0 ; i--) {
                        JSONObject j = quotes.getJSONObject(i);
                        String price = j.getString("Close");
                        float priceF = ((float) Float.parseFloat(price));
                        String date = j.getString("Date");
                        SimpleDateFormat dispFormat = new SimpleDateFormat("dd-MMM");
                        String dispLabel = null;
                        try {
                            Date myDate = myFormat.parse(date);
                            dispLabel = dispFormat.format(myDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        entries.add(new Entry((float) k, priceF));
                        k++;
                        labels.add(dispLabel);
                    }
                        // Always wait for tasks on other threads to finish
                        //Code to  update UI from background thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LineChart myChart = (LineChart) findViewById(R.id.chart);
                                LineDataSet lineDataSet = new LineDataSet(entries,"Prices of Stock");
                                lineDataSet.setDrawCircles(false);
                                lineDataSet.setColor(R.color.headingWhite);

                                lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT); LineDataSet xyz = new LineDataSet(entries,"Entries");
                                lineDataSet.setColor(R.color.headingWhite);
                                LineData myData = new LineData(lineDataSet);
                                myChart.setData(myData);
                                myChart.invalidate();
                                XAxis xAxis = myChart.getXAxis();
                                xAxis.setAxisLineColor(R.color.material_red_700);
                                xAxis.setDrawGridLines(false);
                                YAxis yAxis = myChart.getAxisRight();
                                yAxis.setEnabled(false);
                                myChart.setMaxVisibleValueCount(10);
                                xAxis.setValueFormatter(new AxisValueFormatter() {
                                    @Override
                                    public String getFormattedValue(float value, AxisBase axis) {
                                        return labels.get((int) value);
                                    }

                                    @Override
                                    public int getDecimalDigits() {
                                        return 0;
                                    }
                                });

                                myChart.setScaleYEnabled(false);

                            }
                        });

                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }

            }
        });









    }
}
