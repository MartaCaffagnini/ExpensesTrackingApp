package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DBHandler extends SQLiteOpenHelper {
    public DBHandler(Context context) {
        super(context, "expenses_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + "expenses" + " ("
                + "id" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title" + " TEXT,"
                + "category" + " TEXT,"
                + "amountInCents" + " INTEGER,"
                + "date" + " TEXT)";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    private ArrayList<ListItem> tableToArrayList(Cursor cursor, String language) {
        ArrayList<ListItem> expenses = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                if (language.equals("english")) {
                    category = Util.categoryToEnglish(category);
                }
                String amountInCents = String.valueOf(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("amountInCents"))) / 100);
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                expenses.add(new ListItem(id, title, category, amountInCents, date));

                cursor.moveToNext();
            }
        }
        return expenses;
    }

    public void addExpense(String title, String category, int amountInCents, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("category", category);
        values.put("amountInCents", amountInCents);
        values.put("date", date);

        db.insert("expenses", null, values);

        db.close();
    }

    public ArrayList<ListItem> getAllExpenses(String language) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM expenses ORDER BY date DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<ListItem> expenses = tableToArrayList(cursor, language);

        db.close();
        return expenses;
    }

    public ArrayList<ListItem> getAllExpensesBetweenDates(String startDate, String endDate, String language) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM expenses WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{startDate, endDate});

        ArrayList<ListItem> expenses = tableToArrayList(cursor, language);

        db.close();
        return expenses;
    }

    public Map<String,Float> getExpensesByCategory(String startDate, String endDate, String language){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT category, SUM(amountInCents) FROM expenses WHERE date BETWEEN ? AND ? GROUP BY category";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{startDate, endDate});
        Map<String, Float> expensesByCategory = new HashMap<>();

        while (cursor.moveToNext()) {
            String category = cursor.getString(0);
            if(language.equals("english")){
                category = Util.categoryToEnglish(category);
            }
            float amount = cursor.getFloat(1);
            expensesByCategory.put(category, amount);
        }
        cursor.close();
        return expensesByCategory;
    }

    public Map<Integer,Float> getExpensesOfLastYears(){
        Calendar startCal;
        Calendar endCal;
        startCal = Calendar.getInstance();
        endCal = Calendar.getInstance();
        startCal.set(Calendar.MONTH, 0);
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        endCal.set(Calendar.MONTH,11);
        endCal.set(Calendar.DAY_OF_MONTH,31);

        String startDate = Util.dateToString(Calendar.getInstance().get(Calendar.YEAR),0,1);
        String endDate = Util.dateToString(Calendar.getInstance().get(Calendar.YEAR),11,31);

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery;
        Map<Integer, Float> expensesByYear = new HashMap<>();
        Cursor cursor;
        float amount=0;

        for (int i=0; i<5; i++){
            selectQuery = "SELECT SUM(amountInCents) AS totale_spese FROM expenses WHERE date BETWEEN ? AND ?";
            cursor = db.rawQuery(selectQuery, new String[]{startDate, endDate});
            int year = startCal.get(Calendar.YEAR);

            amount=0;
            if (cursor.moveToFirst()) {
                amount = cursor.getFloat(cursor.getColumnIndexOrThrow("totale_spese"));
            }
            expensesByYear.put(year, amount);
            startCal.add(Calendar.YEAR, -1);
            endCal.add(Calendar.YEAR, -1);
            startDate = Util.dateToString(startCal.get(Calendar.YEAR),0,1);
            endDate = Util.dateToString(endCal.get(Calendar.YEAR),11,31);
        }
        return expensesByYear;
    }

    public void deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("expenses", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void editExpense(int id, String title, String category, int amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put("title", title);
        args.put("category", category);
        args.put("amountInCents", amount);
        args.put("date", date);

        db.update("expenses", args, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}