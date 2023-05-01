package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;

import java.util.Locale;

//activity per visualizzare le statistiche di tutte le spese

public class GeneralStatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        translateToChosenLanguage();

        setContentView(R.layout.activity_general_statistics);

        buildAndSetActionBar();

        DBHandler dbHandler = new DBHandler(GeneralStatisticsActivity.this);

        PieChart pieChart = findViewById(R.id.pie_chart);
        new GetExpensesByCategoryTask(dbHandler.getReadableDatabase(), pieChart,this).execute();

        LineChart lineChart = findViewById(R.id.line_chart);
        new GetExpensesByYearTask(dbHandler.getReadableDatabase(),lineChart, this).execute();
    }

    private void translateToChosenLanguage() {
        SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Configuration configuration = getResources().getConfiguration();
        if(sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("english")){
            configuration.locale=Locale.ENGLISH;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
        }
        else if (sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("italian")) {
            configuration.locale=Locale.ITALIAN;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
        }
    }

    private void buildAndSetActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getResources().getString(R.string.general_statistics_title));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}