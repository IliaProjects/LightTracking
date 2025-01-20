package com.example.user1.lighttracking.classes;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user1 on 8/1/2017.
 */

public class ListCallsObserver extends ContentObserver {

    private  Context mContext;

    public ListCallsObserver(Handler handler, Context context) {
        super(handler);

        mContext=context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        Log.i("TAG","Журнал звонков изменился");

        Uri uri = Uri.parse("content://call_log/calls");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        long lastDateTime=sp.getLong("lastDateTime",0);

        Cursor cursor =mContext.getContentResolver().query(uri, null, "date> ? ", new String[]{ Long.toString(lastDateTime) }, "date");

        if (cursor.moveToFirst()){

            do{

                String phoneNumber=cursor.getString(cursor.getColumnIndex("number"));
                int duration=cursor.getInt(cursor.getColumnIndex("duration"));
                long dateCall=cursor.getLong(cursor.getColumnIndex("date"));
                int typeCall=cursor.getInt(cursor.getColumnIndex("type"));

                if (duration==0 && typeCall==1){
                    typeCall=5;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");
                String dateText=sdf.format(new Date(dateCall));

                DbHelper dbHelper=new DbHelper(mContext);
                SQLiteDatabase db =dbHelper.getWritableDatabase();

                ContentValues cv=new ContentValues();

                cv.put("date_call", dateCall);
                cv.put("date_text", dateText);
                cv.put("phone_number", phoneNumber);
                cv.put("duration",duration);
                cv.put("type_call",typeCall);
                cv.put("is_synced",0);

                String contactName=GetContactName(phoneNumber);

                if (contactName.equals(""))
                    cv.put("contact_name",phoneNumber);
                else
                    cv.put("contact_name",contactName);

                db.insert("calls",null,cv);

                db.close();
                dbHelper.close();


                sp.edit().putLong("lastDateTime",dateCall).commit();

                // запускаем задачу синхронизации данных Andoroid дувайса и Web сервера
                WebServerCalls c=new WebServerCalls();
                c.SyncData(mContext);
            }
            while (cursor.moveToNext());

        }
    }

    private String GetContactName(String phoneNumber){

        ContentResolver cr = mContext.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));

        Cursor cursor = cr.query(uri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
        if (cursor == null) {
            return "";
        }
        String contactName = "";

        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (!cursor.equals("") && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }
}
