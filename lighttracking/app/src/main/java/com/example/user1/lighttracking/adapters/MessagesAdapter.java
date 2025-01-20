package com.example.user1.lighttracking.adapters;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user1.lighttracking.R;
import com.example.user1.lighttracking.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by user1 on 8/1/2017.
 */

public class MessagesAdapter extends BaseAdapter{

    private Context _context;
    private ArrayList<Message> _data;

    public MessagesAdapter(Context context, ArrayList<Message> data){
        _context=context;
        _data=data;
    }

        @Override
        public int getCount() {
            return _data.size();
        }

        @Override
        public Object getItem(int position) {
            return _data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            View result=null;
            Message message=_data.get(position);

            LayoutInflater inflater= (LayoutInflater)_context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            result=inflater.inflate(R.layout.list_item_message,null);

            ((TextView)result.findViewById(R.id.senderName)).setText(message.ContactName);

            ((TextView)result.findViewById(R.id.messageText)).setText(message.MessageText);

            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yy");
            SimpleDateFormat sdfTime = new SimpleDateFormat("kk:mm");

            String dateText=sdfDate.format(new Date(message.DateMessage));
            String timeText=sdfTime.format(new Date(message.DateMessage));

            ((TextView)result.findViewById(R.id.dateMessage)).setText(dateText);
            ((TextView)result.findViewById(R.id.timeMessage)).setText(timeText);


            return result;
        }
}
