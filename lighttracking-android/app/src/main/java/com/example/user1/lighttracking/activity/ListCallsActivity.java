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
import android.widget.ListView;

import com.example.user1.lighttracking.R;
import com.example.user1.lighttracking.adapters.CallsAdapter;
import com.example.user1.lighttracking.classes.DbHelper;
import com.example.user1.lighttracking.model.Call;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListCallsActivity extends AppCompatActivity {

    private ArrayList<Call> _listCalls=new ArrayList<Call>();
    private TabLayout mTabLayout=null;
    private ListView _listViewCalls=null;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_calls);


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //region Установка параметров для закладок
        mTabLayout=(TabLayout)findViewById(R.id.tabEvents);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {


            private int callsAll = -1;
            private int callsMissed = 0;
            private int callsIncoming = 1;
            private int callsOutgoing = 2;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.i("TAG","Выбрана закладка:"+String.valueOf(tab.getPosition()));
                switch (tab.getPosition()){
                    case 0:
                        LoadDataFromDatabase(callsAll);
                        _listViewCalls=(ListView)findViewById(R.id.lvCalls);
                        break;
                    case 1:
                        LoadDataFromDatabase(callsMissed);
                        _listViewCalls=(ListView)findViewById(R.id.lvCalls);
                        break;
                    case 2:
                        LoadDataFromDatabase(callsIncoming);
                        _listViewCalls=(ListView)findViewById(R.id.lvCalls);
                        break;
                    case 3:
                        LoadDataFromDatabase(callsOutgoing);
                        _listViewCalls=(ListView)findViewById(R.id.lvCalls);
                        break;
                    default:
                        break;
                }

                CallsAdapter adapter=new CallsAdapter(getBaseContext(),_listCalls);
                _listViewCalls.setAdapter(adapter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //endregion

        _listViewCalls=(ListView)findViewById(R.id.lvCalls);

    }


    @Override
    protected void onResume() {
        super.onResume();

        LoadDataFromDatabase(-1);

        CallsAdapter adapter=new CallsAdapter(this,_listCalls);
        _listViewCalls.setAdapter(adapter);
    }

    private void LoadDataFromDatabase(int callType)
    {

        _listCalls.clear();

        DbHelper dbHelper=new DbHelper(this);
        SQLiteDatabase db =dbHelper.getReadableDatabase();

        Cursor cursor=db.rawQuery("select * from calls order by date_call desc",null);

        if (cursor.moveToFirst()){

            do{

                Call c=new Call();

                c.Id=cursor.getString(cursor.getColumnIndex("_id"));
                c.DateCall=cursor.getLong(cursor.getColumnIndex("date_call"));
                c.Duration=cursor.getInt(cursor.getColumnIndex("duration"));
                c.PhoneNumber=cursor.getString(cursor.getColumnIndex("phone_number"));
                c.TypeCall=cursor.getInt(cursor.getColumnIndex("type_call"));
                c.ContactName=cursor.getString(cursor.getColumnIndex("contact_name"));

                switch (callType) {
                    case -1:
                        _listCalls.add(c);
                        break;

                    case 0:
                        if (c.TypeCall == 0 || c.TypeCall == 3 || c.TypeCall == 5)
                            _listCalls.add(c);
                        break;

                    case 1:
                        if (c.TypeCall == 1)
                            _listCalls.add(c);
                        break;

                    case 2:
                        if (c.TypeCall == 2)
                            _listCalls.add(c);
                        break;

                    default:
                        break;
                }

            }
            while (cursor.moveToNext());


        }

        db.close();;
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

                ((CallsAdapter)_listViewCalls.getAdapter()).getFilter().filter(newText);

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
