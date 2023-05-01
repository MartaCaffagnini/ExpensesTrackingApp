package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

// task per creare e scaricare il file csv con tutte le spese registrate

public class GenerateCsvTask extends AsyncTask<Void, String, Void> {
    private final SQLiteDatabase db;
    private final Activity activity;
    private boolean csvCreationFailed = false;

    public GenerateCsvTask(SQLiteDatabase db, Activity activity) {
        this.db = db;
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        Cursor cursor = db.rawQuery("SELECT * FROM expenses ORDER BY date DESC", null);

        StringBuilder csvStringBuilder = createCsvContent(cursor);
        db.close();

        String filenameCsv= activity.getResources().getString(R.string.csv_filename) + calendar.get(Calendar.YEAR)
                + calendar.get(Calendar.MONTH)
                + calendar.get(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.HOUR_OF_DAY)
                + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND)+ ".csv";
        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File file = new File(fileDir, filenameCsv);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(csvStringBuilder.toString().getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            csvCreationFailed = true;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (csvCreationFailed)
            Toast.makeText(activity, activity.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
        else Toast.makeText(activity, activity.getResources().getString(R.string.csv_downloaded),Toast.LENGTH_SHORT).show();
    }

    private StringBuilder createCsvContent (Cursor cursor){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(activity.getResources().getString(R.string.heading_csv));
        SharedPreferences sharedPrefs = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                if(sharedPrefs.contains("language") && sharedPrefs.getString("language","italian").equals("english")){
                    category = Util.categoryToEnglish(category);
                }
                int amount = cursor.getInt(cursor.getColumnIndexOrThrow("amountInCents"));
                stringBuilder.append(title).append(",").append(category).append(",").append(amount).append(",").append(date).append("\n");
                cursor.moveToNext();
            }
        }
        cursor.close();
        return stringBuilder;
    }
}
