package com.example.user1.lighttracking.classes;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by user1 on 8/22/2017.
 */

public class WebServerSms {

    public class Sms {
        //incoming, outgoing

        public int Id;
        public String SmsText;
        public String ContacName;
        public String PhoneNumber;
        public String Date;
        public int SmsType;
    }

    public class SmsData
    {
        public Sms sms;
        public String token;
    }

    public  void SyncData(Context context){

        DbHelper dbHelper=new DbHelper(context);
        SQLiteDatabase db =dbHelper.getWritableDatabase();

        Cursor data=db.rawQuery("select * from messages where is_synced=0",null);

        if (data.moveToFirst()){

            do{
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

                SmsData smsData=new SmsData();
                smsData.token=sp.getString("token","");

                Sms sms=new Sms();
                sms.SmsType=data.getInt(data.getColumnIndex("type_sms"));
                sms.ContacName=data.getString(data.getColumnIndex("contact_name"));

                Long date=data.getLong(data.getColumnIndex("date_message"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                sms.Date=sdf.format(new Date(date));

                sms.Id=data.getInt(data.getColumnIndex("_id"));
                sms.PhoneNumber=data.getString(data.getColumnIndex("phone_number"));
                sms.SmsText=data.getString(data.getColumnIndex("message_text"));

                smsData.sms=sms;

                (new AddOneSmsTask()).execute(smsData, context);
            }
            while (data.moveToNext());

        }

    }


    public class AddOneSmsTask extends AsyncTask<Object,Void,Void> {

        String error="";
        String smsText="";
        String responseToken="";
        Context context=null;
        int smsId=-1;

        @Override
        protected Void doInBackground(Object... params) {

            SmsData smsData=(SmsData)params[0];
            context=(Context)params[1];

            OkHttpClient client = new OkHttpClient();

            String uri = "http://student.rti.md/api/sms/addsms";

            JSONObject obj=new JSONObject();

            JSONObject s=new JSONObject();
            s.put("SmsType",smsData.sms.SmsType);
            s.put("ContacName",smsData.sms.ContacName);
            s.put("Date",smsData.sms.Date);
            s.put("SmsText",smsData.sms.SmsText);
            s.put("Id",smsData.sms.Id);
            smsId=smsData.sms.Id;
            s.put("PhoneNumber",smsData.sms.PhoneNumber);

            smsText=smsData.sms.SmsText;

            obj.put("sms",s);
            obj.put("token", smsData.token);

            Request request = new Request.Builder()
                    .url(uri)
                    .method("POST", RequestBody.create(MediaType.parse("application/json"), obj.toJSONString()))
                    .build();

            try {

                Response response = client.newCall(request).execute();

                if (response.code() == 200) {
                    String responseText = response.body().source().readUtf8();

                    JSONObject responseObject= (JSONObject)(new JSONParser()).parse(responseText);

                    error=(String)responseObject.get("ErrorText");

                } else {
                    Log.e("TAG", "Сервер вернул http сатус: " + String.valueOf(response.code()));
                    error="Сервер вернул http сатус: " + String.valueOf(response.code());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                error="Ошибка http запроса." + ex.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!error.equals("")) {
                Log.e("TAG","Ошибка синхронизации смс: "+error);
            }
            else {

                Log.i("TAG","смс засинхронизирована: "+ smsText);
                DbHelper dbHelper=new DbHelper(context);
                SQLiteDatabase db =dbHelper.getWritableDatabase();

                ContentValues cv=new ContentValues();
                cv.put("is_synced",1);
                db.update("messages",cv,"_id=?",new String[]{String.valueOf(smsId)});

                db.close();
                dbHelper.close();
            }
        }
    }
}
