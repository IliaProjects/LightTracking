package com.example.user1.lighttracking.activity;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.user1.lighttracking.R;
import com.example.user1.lighttracking.adapters.CallsAdapter;
import com.example.user1.lighttracking.adapters.MessagesAdapter;
import com.example.user1.lighttracking.classes.DbHelper;
import com.example.user1.lighttracking.model.Call;
import com.example.user1.lighttracking.model.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListMessagesActivity extends AppCompatActivity {

    private ArrayList<Message> _listMessages=new ArrayList<Message>();

    private TabLayout mTabLayout=null;
    private ListView _listViewMessages=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_messages);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //region Установка параметров для закладок
        mTabLayout=(TabLayout)findViewById(R.id.tabEvents);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.i("TAG","Выбрана закладка:"+String.valueOf(tab.getPosition()));
                switch (tab.getPosition()){
                    case 0:
                        LoadDataFromDatabase(1);
                        _listViewMessages=(ListView)findViewById(R.id.lvMessages);
                        break;
                    case 1:
                        LoadDataFromDatabase(2);
                        _listViewMessages=(ListView)findViewById(R.id.lvMessages);
                        break;

                    default:
                        break;
                }

                MessagesAdapter adapter=new MessagesAdapter(getBaseContext(),_listMessages);
                _listViewMessages.setAdapter(adapter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //endregion

        _listViewMessages=(ListView)findViewById(R.id.lvMessages);

    }

    @Override
    protected void onResume() {
        super.onResume();

        LoadDataFromDatabase(1);

        MessagesAdapter adapter=new MessagesAdapter(this,_listMessages);
        _listViewMessages.setAdapter(adapter);
    }

    private void LoadDataFromDatabase(int smsType)
    {
        _listMessages.clear();

        DbHelper dbHelper=new DbHelper(this);
        SQLiteDatabase db =dbHelper.getReadableDatabase();

        Cursor cursor=db.rawQuery("select * from messages order by date_message desc",null);

        if (cursor.moveToFirst()){

            do{

                Message m = new Message();

                m.Id=cursor.getString(cursor.getColumnIndex("_id"));
                m.DateMessage=cursor.getLong(cursor.getColumnIndex("date_message"));
                m.PhoneNumber=cursor.getString(cursor.getColumnIndex("phone_number"));
                m.ContactName=cursor.getString(cursor.getColumnIndex("contact_name"));
                m.TypeSms=cursor.getInt(cursor.getColumnIndex("type_sms"));
                m.MessageText=cursor.getString(cursor.getColumnIndex("message_text"));

                if(m.TypeSms == smsType) _listMessages.add(m);

            }
            while (cursor.moveToNext());

        }

        db.close();
        dbHelper.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.calls_menu,menu);

        //region настройка кнопки поиска

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.i("TAG","Вызов метода onQueryTextSubmit. query="+query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Log.i("TAG","Вызов метода onQueryTextChange. newText="+newText);
                return false;
            }
        });



        //endregion

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
