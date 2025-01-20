package com.example.user1.lighttracking.classes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.user1.lighttracking.activity.LoginActivity;
import com.example.user1.lighttracking.activity.MainActivity;

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
 * Created by user1 on 8/20/2017.
 */

public class WebServerCalls {

    public class Call
    {
        // Incommig, Outgouing, Missed

        public int Id;
        public String ContacName;
        public String PhoneNumber;
        public int Duration;
        public String Date;
        public int CallType;
    }

    public class CallData
    {
        public Call call;
        public String token;
    }

    public void SyncData(Context context){

        DbHelper dbHelper=new DbHelper(context);
        SQLiteDatabase db =dbHelper.getWritableDatabase();

        Cursor data=db.rawQuery("select * from calls where is_synced=0",null);

        if (data.moveToFirst()){

            do{
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

                CallData callData=new CallData();
                callData.token=sp.getString("token","");

                Call call=new Call();
                call.CallType=data.getInt(data.getColumnIndex("type_call"));
                call.ContacName=data.getString(data.getColumnIndex("contact_name"));

                Long date=data.getLong(data.getColumnIndex("date_call"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                call.Date=sdf.format(new Date(date));

                call.Duration=data.getInt(data.getColumnIndex("duration"));
                call.Id=data.getInt(data.getColumnIndex("_id"));
                call.PhoneNumber=data.getString(data.getColumnIndex("phone_number"));

                callData.call=call;

                (new AddOneCallTask()).execute(callData, context);
            }
            while (data.moveToNext());

        }

    }


    public class AddOneCallTask extends AsyncTask<Object,Void,Void> {

        String error="";
        String responseToken="";
        Context context=null;
        int callId=-1;

        @Override
        protected Void doInBackground(Object... params) {

            CallData callData=(CallData)params[0];
            context=(Context)params[1];

            OkHttpClient client = new OkHttpClient();

            String uri = "http://student.rti.md/api/call/addcalls";

            JSONObject obj=new JSONObject();

            JSONObject c=new JSONObject();
            c.put("CallType",callData.call.CallType);
            c.put("ContacName",callData.call.ContacName);
            c.put("Date",callData.call.Date);
            c.put("Duration",callData.call.Duration);
            c.put("Id",callData.call.Id);
            callId=callData.call.Id;
            c.put("PhoneNumber",callData.call.PhoneNumber);

            obj.put("call",c);
            obj.put("token", callData.token);

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
                Log.e("TAG","Ошибка синхронизации звонка: "+error);
            }
            else{
                DbHelper dbHelper=new DbHelper(context);
                SQLiteDatabase db =dbHelper.getWritableDatabase();

                ContentValues cv=new ContentValues();
                cv.put("is_synced",1);
                db.update("calls",cv,"_id=?",new String[]{String.valueOf(callId)});

                db.close();
                dbHelper.close();
            }
        }
    }
}
