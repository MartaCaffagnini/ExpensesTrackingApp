package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// task che calcola i soldi spesi per ogni categoria
public class GetExpensesByCategoryTask extends AsyncTask<Void, String, Map<String, Float>> {
    private final DBHandler db;
    private final PieChart pieChart;
    private String startDate="1900-01-01";
    private String endDate="2100-12-31";
    private AppCompatActivity activity;

    public GetExpensesByCategoryTask(DBHandler db, PieChart pieChart, AppCompatActivity activity) {
        this.db = db;
        this.pieChart = pieChart;
        this.activity=activity;
    }

    public GetExpensesByCategoryTask(DBHandler db, PieChart pieChart, String startDate, String endDate, AppCompatActivity activity) {
        this.db = db;
        this.pieChart = pieChart;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activity=activity;
    }

    @Override
    protected Map<String, Float> doInBackground(Void... voids) {
        SharedPreferences sharedPrefs = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String language="";
        if(sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("english")){
            language = "english";
        }
        else language = "italian";
        Map<String, Float> expensesByCategory = db.getExpensesByCategory(startDate,endDate, language);

        return expensesByCategory;
    }

    @Override
    protected void onPostExecute(Map<String, Float> expensesByCategory) {
        float totalExpenses = 0;
        for (float amount : expensesByCategory.values()) {
            totalExpenses += amount;
        }
        totalExpenses = totalExpenses/100;

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : expensesByCategory.entrySet()) {
            String category = entry.getKey();
            float amount = entry.getValue()/100;
            int percentage = Math.round(amount / totalExpenses * 100);
            entries.add(new PieEntry(percentage,category));
        }

        setPieChartData(entries);

        pieChart.setCenterText(totalExpenses + "€");
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setCenterTextSize(20f);

        setPieChartLegend();

        setPieChartDescription();

        pieChart.invalidate();
    }

    private void setPieChartData(ArrayList<PieEntry> entries){
        PieDataSet dataSet = new PieDataSet(entries,activity.getResources().getString(R.string.categories));
        dataSet.setColors(Color.rgb(217, 80, 138), Color.CYAN,  Color.RED, Color.GREEN,
                Color.MAGENTA, Color.rgb(254, 149, 7));
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(11f);
        dataSet.setDrawValues(true);
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
    }

    private void setPieChartLegend(){
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setTextSize(8f);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
    }

    private void setPieChartDescription() {
        Description desc = new Description();
        desc.setTextColor(Color.BLACK);
        desc.setText(activity.getResources().getString(R.string.expenses_by_category));
        desc.setTextSize(15f);
        pieChart.setDescription(desc);
    }
}
