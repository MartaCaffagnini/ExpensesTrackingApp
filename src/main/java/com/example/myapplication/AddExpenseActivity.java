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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private DBHandler dbHandler;
    private DatePicker simpleDatePicker;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        translateToChosenLanguage();

        setContentView(R.layout.activity_add_expense);

        dbHandler = new DBHandler(AddExpenseActivity.this);

        buildAndSetActionBar();

        Button saveButton = findViewById(R.id.buttonSave);

        simpleDatePicker = findViewById(R.id.simpleDatePicker);
        restoreDateIfSavedInstance(savedInstanceState);

        Spinner spinner = buildAndSetSpinner();

        saveButton.setOnClickListener(view -> save(spinner));
    }

    private void translateToChosenLanguage() {
        sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Configuration configuration = getResources().getConfiguration();
        if (sharedPrefs.contains("language") && sharedPrefs.getString("language", "italian").equals("english")) {
            configuration.locale = Locale.ENGLISH;
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
            Locale locale = new Locale("EN");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, null);
        }
        else if (sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("italian")) {
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
        actionBar.setTitle(getResources().getString(R.string.add_expense_title));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        categories.add(getResources().getString(R.string.groceries_category));
        categories.add(getResources().getString(R.string.transport_category));
        categories.add(getResources().getString(R.string.bills_category));
        categories.add(getResources().getString(R.string.leisure_category));
        categories.add(getResources().getString(R.string.clothes_category));
        categories.add(getResources().getString(R.string.other_category));

        return categories;
    }

    private Spinner buildAndSetSpinner() {
        // creazione adapter per lo spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getCategories());

        // setting del layout stile drop down
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(dataAdapter);

        return spinner;
    }

    private void restoreDateIfSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            simpleDatePicker.updateDate(savedInstanceState.getInt("year"),
                    savedInstanceState.getInt("month"), savedInstanceState.getInt("day"));
        }
    }

    private void save(Spinner spinner) {
        final EditText editTextTitle = findViewById(R.id.editTextTitle);
        String title = editTextTitle.getText().toString();

        String category = String.valueOf(spinner.getSelectedItem());
        if (sharedPrefs.getString("language", "italian").equals("english")) {
            category = Util.categoryToItalian(category);
        }

        final EditText editTextAmount = findViewById(R.id.editTextAmount);
        String amountInEuros = editTextAmount.getText().toString();

        String date = Util.dateToString(simpleDatePicker.getYear(), simpleDatePicker.getMonth(), simpleDatePicker.getDayOfMonth());

        if (!title.isEmpty() && !amountInEuros.isEmpty()) {
            int amountInCents = Integer.parseInt(amountInEuros);
            dbHandler.addExpense(title, category, amountInCents * 100, date);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            Toast.makeText(AddExpenseActivity.this, getResources().getString(R.string.missing_values), Toast.LENGTH_SHORT).show();
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    public void onSaveInstanceState(@NonNull Bundle outBundle) {
        super.onSaveInstanceState(outBundle);
        outBundle.putInt("year", simpleDatePicker.getYear());
        outBundle.putInt("month", simpleDatePicker.getMonth());
        outBundle.putInt("day", simpleDatePicker.getDayOfMonth());
    }
}