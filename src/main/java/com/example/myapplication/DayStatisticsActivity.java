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

public class DayStatisticsActivity extends AppCompatActivity {
    private Calendar cal;
    private String language = "italian";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        translateToChosenLanguage();

        setContentView(R.layout.activity_day_statistics);

        buildAndSetActionBar();

        initializeCalendar(savedInstanceState);

        showChartsAndExpenses();

        Button buttonLastDay = findViewById(R.id.buttonLastDay);
        buttonLastDay.setOnClickListener(view -> {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            showChartsAndExpenses();
        });

        Button buttonNextYear = findViewById(R.id.buttonNextDay);
        buttonNextYear.setOnClickListener(view -> {
            cal.add(Calendar.DAY_OF_MONTH, 1);
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
        actionBar.setTitle(getResources().getString(R.string.day_statistics_title));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initializeCalendar(Bundle savedInstanceState) {
        if(savedInstanceState== null){
            cal = Calendar.getInstance();
        }
        else {
            cal = (Calendar) savedInstanceState.getSerializable("calendar");
        }
    }

    private void showChartsAndExpenses() {
        String date = Util.dateToString(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH) );

        TextView txtViewDay = findViewById(R.id.txtViewDay);
        txtViewDay.setText(new StringBuilder().append(getResources().getString(R.string.day)).append(" ").append(date).toString());

        final ListView listView = (ListView) findViewById(R.id.expenses_list);
        DBHandler dbHandler = new DBHandler(DayStatisticsActivity.this);

        listView.setAdapter(new CustomListAdapter(this, dbHandler.getAllExpensesBetweenDates(date, date, language)));
        final TextView txtView = findViewById(R.id.textViewNumberExpenses);
        PieChart pieChart = findViewById(R.id.pie_chart);
        if(listView.getCount()>0) {
            txtView.setText(new StringBuilder().append(getResources().getString(R.string.registered_expenses))
                    .append(" ").append(listView.getCount()).toString());
            pieChart.setVisibility(PieChart.VISIBLE);
            new GetExpensesByCategoryTask(dbHandler.getReadableDatabase(), pieChart, date, date,this).execute();
        }
        else {
            txtView.setText(getResources().getString(R.string.no_expenses));
            pieChart.setVisibility(PieChart.GONE);
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
        outBundle.putSerializable("calendar",cal);
    }
}