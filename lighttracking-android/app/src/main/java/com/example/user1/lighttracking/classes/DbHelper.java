package com.example.user1.lighttracking.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user1 on 7/26/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {

        // конструктор суперкласса
        super(context, "light_tracker", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table calls (_id  INTEGER PRIMARY KEY AUTOINCREMENT, date_call long, duration int, phone_number text, " +
                "type_call int, contact_name text, date_text text, is_synced int )");
        db.execSQL("create table messages (_id INTEGER PRIMARY KEY AUTOINCREMENT, date_message long, phone_number text, " +
                 "type_sms int, message_text text, contact_name text, is_synced int )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
