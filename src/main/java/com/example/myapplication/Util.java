package com.example.myapplication;

abstract public class Util {

    public static String categoryToEnglish (String it_category) {
        String category = "";
        switch (it_category) {
            case "Alimentari":
                category = "Groceries";
                break;
            case "Trasporti":
                category = "Transport";
                break;
            case "Bollette":
                category = "Bills";
                break;
            case "Svago":
                category = "Leisure";
                break;
            case "Altro":
                category = "Other";
                break;
            case "Vestiti":
                category = "Clothes";
                break;
        }
        return category;
    }

    public static String categoryToItalian (String en_category) {
        String category = "";
        switch (en_category) {
            case "Groceries":
                category = "Alimentari";
                break;
            case "Transport":
                category = "Trasporti";
                break;
            case "Bills":
                category = "Bollette";
                break;
            case "Leisure":
                category = "Svago";
                break;
            case "Other":
                category = "Altro";
                break;
            case "Clothes":
                category = "Vestiti";
                break;
        }
        return category;
    }

    public static String dateToString(int year, int month, int day){
        String date = year + "-";
        month += 1;
        if (month<10) date +="0" + month + "-";
        else date += month + "-";
        if (day<10) date +="0" + day;
        else date += day;
        return date;
    }
}
