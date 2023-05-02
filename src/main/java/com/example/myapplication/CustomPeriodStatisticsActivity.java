package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;

import java.util.Locale;

public class CustomPeriodStatisticsActivity extends AppCompatActivity {
    private String startDate;
    private String endDate;
    private String language = "italian";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        translateToChosenLanguage();

        setContentView(R.layout.activity_custom_period_statistics);

        buildAndSetActionBar();

        DBHandler dbHandler = new DBHandler(CustomPeriodStatisticsActivity.this);

        getDatesFromIntent();

        final TextView txtViewPeriod = (TextView) findViewById(R.id.txtViewPeriod);
        txtViewPeriod.setText(new StringBuilder().append(getResources().getString(R.string.from))
                .append(" ").append(startDate).append(" ").append(getResources().getString(R.string.to))
                .append(" ").append(endDate).toString());

        final ListView listView = findViewById(R.id.expenses_list);
        listView.setAdapter(new CustomListAdapter(this, dbHandler.getAllExpensesBetweenDates(startDate, endDate, language)));

        if(listView.getCount()>0) {
            TextView txtView = findViewById(R.id.textViewNumberExpenses);
            txtView.setText(new StringBuilder().append(getResources().getString(R.string.registered_expenses))
                    .append(" ").append(listView.getCount()).toString());

            PieChart pieChart = findViewById(R.id.pie_chart);
            pieChart.setVisibility(PieChart.VISIBLE);

            new GetExpensesByCategoryTask(dbHandler.getReadableDatabase(), pieChart, startDate, endDate,this).execute();
        }
    }

    private void translateToChosenLanguage() {
        SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Configuration configuration = getResources().getConfiguration();
        if(sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("english")){
            configuration.locale= Locale.ENGLISH;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
            language = "english";
        }
        else if (sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("italian")) {
            configuration.locale=Locale.ITALIAN;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
            language="italian";
        }
    }

    private void buildAndSetActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getResources().getString(R.string.custom_period_title));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void getDatesFromIntent() {
        Bundle input = getIntent().getExtras();
        startDate = input.getString("startDate");
        endDate = input.getString("endDate");
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