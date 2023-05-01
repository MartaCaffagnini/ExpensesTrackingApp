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

public class EditExpenseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private DatePicker simpleDatePicker;
    private EditText editTextTitle;
    private EditText editTextAmount;
    private Spinner spinner;
    private SharedPreferences sharedPrefs;
    private int id;
    private String title;
    private String category;
    private String amount;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        translateToChosenLanguage();

        setContentView(R.layout.activity_edit_expense);

        buildAndSetActionBar();

        getDetails();

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextTitle.setText(title);

        editTextAmount = findViewById(R.id.editTextAmount);
        editTextAmount.setText(amount);

        setSpinner();

        setDatePicker(savedInstanceState);

        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(view -> { save(); });
    }

    private void translateToChosenLanguage() {
        sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
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
        actionBar.setTitle(getResources().getString(R.string.edit_expense_title));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void getDetails(){
        Bundle input = getIntent().getExtras();
        id = input.getInt("id");
        title = input.getString("title");
        category = input.getString("category");
        amount = input.getString("amount");
        date = input.getString("date");
    }

    private void setSpinner(){
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        List<String> categories = new ArrayList<String>();
        categories.add(getResources().getString(R.string.groceries_category));
        categories.add(getResources().getString(R.string.transport_category));
        categories.add(getResources().getString(R.string.bills_category));
        categories.add(getResources().getString(R.string.leisure_category));
        categories.add(getResources().getString(R.string.clothes_category));
        categories.add(getResources().getString(R.string.other_category));
        int index = categories.indexOf(category);
        // creazione adapter per lo spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // setting del layout stile drop down
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // associo lo spinner all'adapter
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(index);
    }

    private void setDatePicker(Bundle savedInstanceState){
        simpleDatePicker = findViewById(R.id.simpleDatePicker);
        if (savedInstanceState == null) {
            String[] dayMonthYear = date.split("-");
            int year = Integer.parseInt(dayMonthYear[0]);
            int month = Integer.parseInt(dayMonthYear[1]);
            int day = Integer.parseInt(dayMonthYear[2]);
            simpleDatePicker.updateDate(year, month - 1, day);
        }
        else {
            simpleDatePicker.updateDate(savedInstanceState.getInt("year"),
                    savedInstanceState.getInt("month"), savedInstanceState.getInt("day"));
        }
    }

    private void save(){
        String newTitle = editTextTitle.getText().toString();
        String newCategory = String.valueOf(spinner.getSelectedItem());
        if(sharedPrefs.getString("language","italian").equals("english")){
            newCategory = Util.categoryToItalian(newCategory);
        }
        String amountInCents =editTextAmount.getText().toString();
        String newDate = Util.dateToString(simpleDatePicker.getYear(),
                simpleDatePicker.getMonth(),simpleDatePicker.getDayOfMonth());

        if(!newTitle.isEmpty() && !amountInCents.isEmpty()) {
            DBHandler dbHandler = new DBHandler(EditExpenseActivity.this);
            dbHandler.editExpense(id, newTitle, newCategory, Integer.parseInt(amountInCents) * 100, newDate);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        else Toast.makeText(EditExpenseActivity.this, getResources().getString(R.string.missing_values), Toast.LENGTH_SHORT).show();
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
        outBundle.putInt("year",simpleDatePicker.getYear());
        outBundle.putInt("month",simpleDatePicker.getMonth());
        outBundle.putInt("day",simpleDatePicker.getDayOfMonth());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { }

    public void onNothingSelected(AdapterView<?> arg0) { }
}