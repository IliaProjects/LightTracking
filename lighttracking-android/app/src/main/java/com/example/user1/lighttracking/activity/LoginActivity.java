package com.example.user1.lighttracking.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user1.lighttracking.R;
import com.example.user1.lighttracking.classes.DbHelper;
import com.example.user1.lighttracking.classes.ListCallsObserver;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    final String TAG="Lighttracking";



    EditText mPassword=null;
    EditText mEmail=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPassword= (EditText)findViewById(R.id.edPassword);
        mEmail= (EditText)findViewById(R.id.edEmail);

        findViewById(R.id.activity_login).setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
                inputMethodManager.hideSoftInputFromWindow(mEmail.getWindowToken(), 0);
                return true;
            }
        });

        // для примера заполним таблицу calls тестовыми данными
        // FillDatabase();


    }

    public void LoginClick(View v){

        if (mEmail.getText().toString().equals("")){
            mEmail.requestFocus();
            Toast.makeText(this, getString(R.string.email_required) , Toast.LENGTH_SHORT).show();
            return;
        }

        if (mPassword.getText().toString().equals("")){
            mPassword.requestFocus();
            Toast.makeText(this, getString(R.string.pwd_required) , Toast.LENGTH_SHORT).show();
            return;
        }

        LoginTask task=new LoginTask();
        task.execute();


    }

    public class LoginTask extends AsyncTask<Void,Void,Void> {

        String error="";
        String responseToken="";
        String email=mEmail.getText().toString();
        String password=mPassword.getText().toString();

        @Override
        protected Void doInBackground(Void... voids) {

            OkHttpClient client = new OkHttpClient();

            String uri = "http://student.rti.md/api/user/login";

            JSONObject obj=new JSONObject();
            obj.put("login", email);
            obj.put("password", password);

            Request request = new Request.Builder()
                    .url(uri)
                    .method("POST",RequestBody.create(MediaType.parse("application/json"), obj.toJSONString()))
                    .build();

            try {

                Response response = client.newCall(request).execute();

                if (response.code() == 200) {
                    String responseText = response.body().source().readUtf8();

                    JSONObject responseObject= (JSONObject)(new JSONParser()).parse(responseText);

                    error=(String)responseObject.get("Error");
                    responseToken=(String)responseObject.get("Token");

                    Log.i("TAG",responseText);
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

            if (error.equals("")) {

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                sp.edit().putString("token",responseToken).commit();

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
            else
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();

        }
    }

}
