package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;

import java.util.Calendar;
import java.util.Locale;

//activity che mostra le statistiche delle spese mese per mese
public class MonthStatisticsActivity extends AppCompatActivity {
    private Calendar startCal;
    private Calendar endCal;
    String language = "italian";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        translateToChosenLanguage();

        setContentView(R.layout.activity_month_statistics);

        buildAndSetActionBar();

        initializeCalendar(savedInstanceState);

        showChartsAndExpenses();

        Button buttonLastMonth = findViewById(R.id.buttonLastMonth);
        buttonLastMonth.setOnClickListener(view -> {
            startCal.add(Calendar.MONTH, -1);
            endCal.add(Calendar.MONTH, -1);
            endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
            showChartsAndExpenses();
        });

        Button buttonNextYear = findViewById(R.id.buttonNextMonth);
        buttonNextYear.setOnClickListener(view -> {
            startCal.add(Calendar.MONTH, 1);
            endCal.add(Calendar.MONTH, 1);
            endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
            showChartsAndExpenses();
        });
    }

    private void translateToChosenLanguage() {
        SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Configuration configuration = getResources().getConfiguration();
        if(sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("english")){
            configuration.locale= Locale.ENGLISH;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
            Locale locale = new Locale("EN");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, null);
            language = "english";
        }
        else if (sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("italian")) {
            configuration.locale=Locale.ITALIAN;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
            Locale locale = new Locale("IT");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, null);
            language="italian";
        }
    }

    private void buildAndSetActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getResources().getString(R.string.month_statistics_title));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initializeCalendar(Bundle savedInstanceState) {
        if(savedInstanceState == null){
            startCal = Calendar.getInstance();
            endCal = Calendar.getInstance();
            startCal.set(Calendar.DAY_OF_MONTH, 1);
            endCal.set(Calendar.DAY_OF_MONTH,endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        else {
            startCal = (Calendar) savedInstanceState.getSerializable("startCalendar");
            endCal = (Calendar) savedInstanceState.getSerializable("endCalendar");
        }
    }

    private void showChartsAndExpenses() {
        String startDate = Util.dateToString(startCal.get(Calendar.YEAR),startCal.get(Calendar.MONTH),startCal.get(Calendar.DAY_OF_MONTH) );
        String endDate = Util.dateToString(endCal.get(Calendar.YEAR),endCal.get(Calendar.MONTH),endCal.get(Calendar.DAY_OF_MONTH) );

        final TextView txtViewYear = (TextView) findViewById(R.id.txtViewMonth);
        txtViewYear.setText(new StringBuilder().append(startCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()))
                .append(" ").append(startCal.get(Calendar.YEAR)).toString());

        DBHandler dbHandler = new DBHandler(MonthStatisticsActivity.this);

        final ListView listView = (ListView) findViewById(R.id.expenses_list);
        listView.setAdapter(new CustomListAdapter(this, dbHandler.getAllExpensesBetweenDates(startDate, endDate, language)));
        PieChart pieChart = findViewById(R.id.pie_chart);

        final TextView txtView = findViewById(R.id.textViewNumberExpenses);

        if(listView.getCount()>0) {
            txtView.setText(new StringBuilder().append(getResources().getString(R.string.registered_expenses))
                    .append(" ").append(listView.getCount()).toString());
            pieChart.setVisibility(PieChart.VISIBLE);
            new GetExpensesByCategoryTask(dbHandler, pieChart, startDate, endDate,this).execute();
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

    public void onSaveInstanceState(@NonNull Bundle outBundle){
        super.onSaveInstanceState(outBundle);
        outBundle.putSerializable("startCalendar",startCal);
        outBundle.putSerializable("endCalendar",endCal);
    }
}