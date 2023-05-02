package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;

import java.util.Calendar;
import java.util.Locale;

public class YearStatisticsActivity extends AppCompatActivity {
    private Calendar startCal;
    private Calendar endCal;
    private String language = "italian";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        translateToChosenLanguage();

        setContentView(R.layout.activity_year_statistics);

        buildAndSetActionBar();

        initializeCalendar(savedInstanceState);

        showChartsAndExpenses();

        Button buttonLastYear = findViewById(R.id.buttonLastYear);
        buttonLastYear.setOnClickListener(view -> {
            startCal.add(Calendar.YEAR, -1);
            endCal.add(Calendar.YEAR, -1);
            showChartsAndExpenses();
        });

        Button buttonNextYear = findViewById(R.id.buttonNextYear);
        buttonNextYear.setOnClickListener(view -> {
            startCal.add(Calendar.YEAR, 1);
            endCal.add(Calendar.YEAR, 1);
            showChartsAndExpenses();
        });
    }

    private void translateToChosenLanguage() {
        SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Configuration configuration = getResources().getConfiguration();
        if(sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("english")){
            configuration.locale= Locale.ENGLISH;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
            language = "english";
        }
        else if (sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("italian")){
            configuration.locale=Locale.ITALIAN;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
            language="italian";
        }
    }

    private void buildAndSetActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getResources().getString(R.string.year_statistics_title));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initializeCalendar(Bundle savedInstanceState) {
        if(savedInstanceState== null){
            startCal = Calendar.getInstance();
            endCal = Calendar.getInstance();
            startCal.set(Calendar.MONTH, 0);
            startCal.set(Calendar.DAY_OF_MONTH, 1);
            endCal.set(Calendar.MONTH,11);
            endCal.set(Calendar.DAY_OF_MONTH,31);
        }
        else {
            startCal = (Calendar) savedInstanceState.getSerializable("startCalendar");
            endCal = (Calendar) savedInstanceState.getSerializable("endCalendar");
        }
    }

    private void showChartsAndExpenses() {
        String startDate = Util.dateToString(startCal.get(Calendar.YEAR),0,1);
        String endDate = Util.dateToString(startCal.get(Calendar.YEAR),11,31);

        final TextView txtViewYear = findViewById(R.id.txtViewYear);
        txtViewYear.setText(new StringBuilder().append(startCal.get(Calendar.YEAR)).append("").toString());

        final TextView txtView = findViewById(R.id.textViewNumberExpenses);

        DBHandler dbHandler = new DBHandler(YearStatisticsActivity.this);

        final ListView listView = findViewById(R.id.expenses_list);
        listView.setAdapter(new CustomListAdapter(this, dbHandler.getAllExpensesBetweenDates(startDate, endDate, language)));

        PieChart pieChart = findViewById(R.id.pie_chart);

        if(listView.getCount()>0) {
            txtView.setText(new StringBuilder().append(getResources().getString(R.string.registered_expenses))
                    .append(" ").append(listView.getCount()).toString());
            pieChart.setVisibility(PieChart.VISIBLE);
            new GetExpensesByCategoryTask(dbHandler.getReadableDatabase(), pieChart, startDate, endDate,this).execute();
        }
        else {
            pieChart.setVisibility(PieChart.INVISIBLE);
            txtView.setText(getResources().getString(R.string.no_expenses));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outBundle){
        super.onSaveInstanceState(outBundle);
        outBundle.putSerializable("startCalendar",startCal);
        outBundle.putSerializable("endCalendar",endCal);
    }
}