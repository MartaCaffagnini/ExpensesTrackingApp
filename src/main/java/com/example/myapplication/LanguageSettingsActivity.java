package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class LanguageSettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        translateToChosenLanguage();

        setContentView(R.layout.activity_language_settings);

        buildAndSetActionBar();

        SharedPreferences.Editor editor = sharedPrefs.edit();

        Button save = findViewById(R.id.buttonSave);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        save.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(LanguageSettingsActivity.this,
                                getResources().getString((R.string.no_language)),Toast.LENGTH_SHORT).show();
            }
            else {
                RadioButton radioButton = (RadioButton)radioGroup.findViewById(selectedId);
                if (radioButton.getId()==R.id.radioButtonItalian){
                    editor.putString("language", "italian");
                    editor.apply();
                }
                else {
                    editor.putString("language", "english");
                    editor.apply();
                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void translateToChosenLanguage () {
        sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Configuration configuration = getResources().getConfiguration();
        if(sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("english")){
            configuration.locale=Locale.ENGLISH;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
        }
        else if (sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("italian")){
            configuration.locale=Locale.ITALIAN;
            getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
        }
    }

    private void buildAndSetActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getResources().getString(R.string.language_settings));
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