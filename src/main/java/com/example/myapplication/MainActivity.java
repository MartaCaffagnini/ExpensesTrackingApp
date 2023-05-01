package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DBHandler dbHandler;
    String language = "italian";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        translateToChosenLanguage();

        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getResources().getString(R.string.expenses));

        Button buttonAdd = findViewById(R.id.button);
        buttonAdd.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddExpenseActivity.class);
            startActivity(intent);
        });

        dbHandler = new DBHandler(MainActivity.this);

        final ListView listView = findViewById(R.id.transaction_list);
        listView.setAdapter(new CustomListAdapter(this, dbHandler.getAllExpenses(language)));
        if(listView.getCount()>0) {
            final TextView txtView = findViewById(R.id.textViewNumberExpenses);
            txtView.setText(new StringBuilder().append(getResources().getString(R.string.registered_expenses))
                    .append(" ").append(listView.getCount()).toString());
        }

        listView.setOnItemClickListener((a, v, position, id) -> {
            ListItem expense = (ListItem) listView.getItemAtPosition(position);
            createIntentAndStartActivity(expense);
        });
    }

    private void translateToChosenLanguage() {
        SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Configuration configuration = getResources().getConfiguration();
        if(sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("english")){
            configuration.locale=Locale.ENGLISH;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
            language = "english";
        }
        else if (sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("italian")){
            configuration.locale=Locale.ITALIAN;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
            language="italian";
        }
    }

    private void createIntentAndStartActivity(ListItem expense){
        Intent intent = new Intent(getApplicationContext(), DetailExpenseActivity.class);
        intent.putExtra("id", expense.getId());
        intent.putExtra("title", expense.getTitle());
        intent.putExtra("category", expense.getCategory());
        intent.putExtra("amount", expense.getAmount() + "€");
        intent.putExtra("date", expense.getDate());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.general_statistics:
                intent = new Intent(getApplicationContext(), GeneralStatisticsActivity.class);
                startActivity(intent);
                break;
            case R.id.year_statistics:
                intent = new Intent(getApplicationContext(), YearStatisticsActivity.class);
                startActivity(intent);
                break;
            case R.id.month_statistics:
                intent = new Intent(getApplicationContext(), MonthStatisticsActivity.class);
                startActivity(intent);
                break;
            case R.id.day_statistics:
                intent = new Intent(getApplicationContext(), DayStatisticsActivity.class);
                startActivity(intent);
                break;
            case R.id.custom_statistics:
                intent = new Intent(getApplicationContext(), SetCustomStatisticActivity.class);
                startActivity(intent);
                break;
            case R.id.csv:
                new GenerateCsvTask(dbHandler.getReadableDatabase(),this).execute();
                break;
            case R.id.setting:
                intent = new Intent(getApplicationContext(), LanguageSettingsActivity.class);
                startActivity(intent);
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }
}