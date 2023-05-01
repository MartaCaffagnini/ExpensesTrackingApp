package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class DetailExpenseActivity extends AppCompatActivity {
    private int id;
    private String title;
    private String category;
    private String amount;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        translateToChosenLanguage();

        setContentView(R.layout.activity_detail_expense);

        DBHandler dbHandler = new DBHandler(DetailExpenseActivity.this);

        buildAndSetActionBar();

        setDetails();

        Button buttonEdit = findViewById(R.id.buttonEdit);
        buttonEdit.setOnClickListener(view -> { intentAndStartActivity(); });

        Button buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(view -> {
            dbHandler.deleteExpense(id);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });
    }

    private void translateToChosenLanguage() {
        SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Configuration configuration = getResources().getConfiguration();
        if(sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("english")){
            configuration.locale= Locale.ENGLISH;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
        }
        else if (sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("italian")){
            configuration.locale=Locale.ITALIAN;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
        }
    }

    private void setDetails(){
        Bundle input = getIntent().getExtras();
        id = input.getInt("id");
        title = input.getString("title");
        category = input.getString("category");
        amount = input.getString("amount");
        date = input.getString("date");

        final TextView txtViewTitle = findViewById(R.id.title);
        txtViewTitle.setText(title);
        final TextView txtViewCategory = findViewById(R.id.category);
        txtViewCategory.setText(category);
        final TextView txtViewAmount = findViewById(R.id.amount);
        txtViewAmount.setText(amount);
        final TextView txtViewDate = findViewById(R.id.date);
        txtViewDate.setText(date);
    }

    private void buildAndSetActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getResources().getString(R.string.detail_expense_title));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void intentAndStartActivity(){
        Intent intent = new Intent(getApplicationContext(), EditExpenseActivity.class);
        intent.putExtra("id", id );
        intent.putExtra("title", title );
        intent.putExtra("category", category );
        intent.putExtra("amount", amount.replace("€",""));
        intent.putExtra("date", date );
        startActivity(intent);
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