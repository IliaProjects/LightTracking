package com.example.user1.lighttracking.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.user1.lighttracking.R;
import com.example.user1.lighttracking.classes.ListCallsObserver;
import com.example.user1.lighttracking.classes.ListSmsObserver;
import com.example.user1.lighttracking.classes.WebServerCalls;
import com.example.user1.lighttracking.classes.WebServerSms;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    int MY_REQUEST_READ_PHONE_STATE =1;
    int MY_REQUEST_READ_SMS =2;
    int MY_REQUEST_READ_CONTACT =3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ContentResolver contentResolver = getContentResolver();

        ListCallsObserver callObserver=new ListCallsObserver(new Handler(),this);
        contentResolver.registerContentObserver(Uri.parse("content://call_log/calls"),true,callObserver);


        ListSmsObserver smsObserver=new ListSmsObserver(new Handler(),this);
        contentResolver.registerContentObserver(Uri.parse("content://sms"),true,smsObserver);


        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_REQUEST_READ_PHONE_STATE);
        }

        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, MY_REQUEST_READ_SMS);
        }

        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_REQUEST_READ_CONTACT);
        }

        // получаем ссылку на объект настроек
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (sp.getLong("lastDateTime",-1)==-1)
            sp.edit().putLong("lastDateTime", (new Date()).getTime()).commit();

        if (sp.getLong("lastDateTimeSMS",-1)==-1)
            sp.edit().putLong("lastDateTimeSMS", (new Date()).getTime()).commit();


        WebServerCalls c=new WebServerCalls();
        c.SyncData(this);

        WebServerSms s=new WebServerSms();
        s.SyncData(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==MY_REQUEST_READ_PHONE_STATE){

        }

        if (requestCode==MY_REQUEST_READ_SMS){

        }

        if (requestCode==MY_REQUEST_READ_CONTACT){

        }

    }


    public void CallsClick(View v){

        startActivity(new Intent(this,ListCallsActivity.class));

    }

    public void MessagesClick(View v){

        startActivity(new Intent (this,ListMessagesActivity.class));

    }

    public void ExitClick(View v){
        (new LogoutTask()).execute();
    }

    public class LogoutTask extends AsyncTask<Void,Void,Void> {

        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            token=sp.getString("token","");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            OkHttpClient client = new OkHttpClient();

            String uri = "http://student.rti.md/api/user/logout";

            JSONObject obj=new JSONObject();
            obj.put("token", token );

            Request request = new Request.Builder()
                    .url(uri)
                    .method("POST", RequestBody.create(MediaType.parse("application/json"), obj.toJSONString()))
                    .build();

            try {

                Response response = client.newCall(request).execute();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            sp.edit().clear().commit();

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();

        }
    }
}
