package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GetExpensesByYearTask extends AsyncTask<Void, String, Map<Integer, Float>> {
    private final DBHandler db;
    private LineChart lineChart;
    private AppCompatActivity activity;

    public GetExpensesByYearTask(DBHandler db, LineChart lineChart, AppCompatActivity activity) {
        this.db = db;
        this.lineChart = lineChart;
        this.activity=activity;
    }

    @Override
    protected Map<Integer, Float> doInBackground(Void... voids) {
        Map<Integer, Float> expensesByYear = db.getExpensesOfLastYears();
        return expensesByYear;
    }

    @Override
    protected void onPostExecute(Map<Integer, Float> expensesByYear) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (Map.Entry<Integer, Float> entry : expensesByYear.entrySet()) {
            float year = entry.getKey();
            float amount = entry.getValue() / 100;
            entries.add(new Entry(year,amount));
        }

        setLineChartData(entries);

        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getLegend().setEnabled(true);
        lineChart.invalidate();
    }

    private void setLineChartData(ArrayList<Entry> entries){
        LineDataSet dataSet = new LineDataSet(entries, activity.getResources().getString(R.string.line_chart_desc));
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
    }
}