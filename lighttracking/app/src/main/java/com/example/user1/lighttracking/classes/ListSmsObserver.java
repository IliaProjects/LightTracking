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

/**
 * Created by user1 on 8/7/2017.
 */

public class ListSmsObserver extends ContentObserver {

    private Context mContext;

    public ListSmsObserver(Handler handler, Context context) {
        super(handler);

        mContext=context;
    }

    @Override
    public void onChange(boolean selfChange) {

        Log.i("TAG","Журнал СМС изменился");

        Uri uri = Uri.parse("content://sms");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        long lastDateTimeSMS=sp.getLong("lastDateTimeSMS",0);

        Cursor cursor =mContext.getContentResolver().query(uri, null, "date> ? ", new String[]{ Long.toString(lastDateTimeSMS) }, "date");

        if (cursor.moveToFirst()){
            long dateSMS=0;
            DbHelper dbHelper=new DbHelper(mContext);
            SQLiteDatabase db =dbHelper.getWritableDatabase();

            do{

                String phoneNumber=cursor.getString(cursor.getColumnIndex("address"));
                dateSMS=cursor.getLong(cursor.getColumnIndex("date"));
                String body=cursor.getString(cursor.getColumnIndex("body"));
                int typeSms = cursor.getInt(cursor.getColumnIndex("type"));


                try{

                    ContentValues cv=new ContentValues();
                    cv.put("date_message",dateSMS);
                    cv.put("phone_number",phoneNumber);
                    cv.put("message_text",body);
                    cv.put("type_sms", typeSms);
                    cv.put("is_synced", 0);
                    String contactName=GetContactName(phoneNumber);

                    if (contactName.equals(""))
                        cv.put("contact_name",phoneNumber);
                    else
                        cv.put("contact_name",contactName);

                    db.insert("messages",null,cv);

                }
                catch (Exception ex){
                    ex.printStackTrace();
                }




            } while (cursor.moveToNext());

            sp.edit().putLong("lastDateTimeSMS",dateSMS).commit();

            db.close();
            dbHelper.close();

            // запускаем задачу синхронизации данных Andoroid дувайса и Web сервера
            WebServerSms c=new WebServerSms();
            c.SyncData(mContext);

        }
        cursor.close();

        super.onChange(selfChange);
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
