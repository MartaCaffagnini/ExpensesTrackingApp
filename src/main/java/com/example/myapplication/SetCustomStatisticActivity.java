package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Locale;

public class SetCustomStatisticActivity extends AppCompatActivity {
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        translateToChosenLanguage();

        setContentView(R.layout.activity_set_custom_statistics);

        buildAndSetActionBar();

        setDatePickers(savedInstanceState);

        Button buttonCustomPeriod = findViewById(R.id.buttonCustomPeriod);
        buttonCustomPeriod.setOnClickListener(view -> {

            String startDate = Util.dateToString(startDatePicker.getYear(),
                    startDatePicker.getMonth(),startDatePicker.getDayOfMonth());
            String endDate = Util.dateToString(endDatePicker.getYear(),
                    endDatePicker.getMonth(),endDatePicker.getDayOfMonth());

            Intent intent = new Intent(getApplicationContext(), CustomPeriodStatisticsActivity.class);
            intent.putExtra("startDate", startDate);
            intent.putExtra("endDate", endDate);
            startActivity(intent);
        });
    }

    private void setDatePickers(Bundle savedInstanceState){
        startDatePicker = findViewById(R.id.startDatePicker);
        endDatePicker = findViewById(R.id.endDatePicker);
        if (savedInstanceState != null) {
            startDatePicker.updateDate(savedInstanceState.getInt("yearStartDate"),
                    savedInstanceState.getInt("monthStartDate"), savedInstanceState.getInt("dayStartDate"));
            endDatePicker.updateDate(savedInstanceState.getInt("yearEndDate"),
                    savedInstanceState.getInt("monthEndDate"), savedInstanceState.getInt("dayEndDate"));
        }
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
        }
        else if (sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("italian")){
            configuration.locale=Locale.ITALIAN;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
            Locale locale = new Locale("IT");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, null);
        }
    }

    private void buildAndSetActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getResources().getString(R.string.set_custom_statistics_title));
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

    public void onSaveInstanceState(@NonNull Bundle outBundle){
        super.onSaveInstanceState(outBundle);
        outBundle.putInt("yearStartDate",startDatePicker.getYear());
        outBundle.putInt("monthStartDate",startDatePicker.getMonth());
        outBundle.putInt("dayStartDate",startDatePicker.getDayOfMonth());

        outBundle.putInt("yearEndDate",endDatePicker.getYear());
        outBundle.putInt("monthEndDate",endDatePicker.getMonth());
        outBundle.putInt("dayEndDate",endDatePicker.getDayOfMonth());
    }
}