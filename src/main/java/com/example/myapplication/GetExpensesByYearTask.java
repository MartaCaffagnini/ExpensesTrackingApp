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
    private final SQLiteDatabase db;
    private LineChart lineChart;
    private AppCompatActivity activity;

    public GetExpensesByYearTask(SQLiteDatabase db, LineChart lineChart, AppCompatActivity activity) {
        this.db = db;
        this.lineChart = lineChart;
        this.activity=activity;
    }

    @Override
    protected Map<Integer, Float> doInBackground(Void... voids) {
        Calendar startCal;
        Calendar endCal;
        startCal = Calendar.getInstance();
        endCal = Calendar.getInstance();
        startCal.set(Calendar.MONTH, 0);
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        endCal.set(Calendar.MONTH,11);
        endCal.set(Calendar.DAY_OF_MONTH,31);

        String startDate = Util.dateToString(Calendar.getInstance().get(Calendar.YEAR),0,1);
        String endDate = Util.dateToString(Calendar.getInstance().get(Calendar.YEAR),11,31);
        String selectQuery;
        Map<Integer, Float> expensesByYear = new HashMap<>();
        Cursor cursor;
        float amount=0;

        for (int i=0; i<5; i++){
            selectQuery = "SELECT SUM(amountInCents) AS totale_spese FROM expenses WHERE date BETWEEN ? AND ?";
            cursor = db.rawQuery(selectQuery, new String[]{startDate, endDate});
            int year = startCal.get(Calendar.YEAR);

            amount=0;
            if (cursor.moveToFirst()) {
                amount = cursor.getFloat(cursor.getColumnIndexOrThrow("totale_spese"));
            }
            expensesByYear.put(year, amount);
            startCal.add(Calendar.YEAR, -1);
            endCal.add(Calendar.YEAR, -1);
            startDate = Util.dateToString(startCal.get(Calendar.YEAR),0,1);
            endDate = Util.dateToString(endCal.get(Calendar.YEAR),11,31);
        }
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