package com.example.user1.lighttracking.adapters;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user1.lighttracking.R;
import com.example.user1.lighttracking.model.Call;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user1 on 7/26/2017.
 */

public class CallsAdapter extends BaseAdapter implements Filterable {

    private Context _context;

    private ArrayList<Call> _data;
    private ArrayList<Call> _filteredData;

    private ItemFilter filter=new ItemFilter();

    public CallsAdapter(Context context, ArrayList<Call> data){
        _context=context;
        _data=data;
        _filteredData=data;
    }

    @Override
    public int getCount() {
        return _filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return _filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View result=null;
        Call call=_filteredData.get(position);

        LayoutInflater inflater= (LayoutInflater)_context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        result=inflater.inflate(R.layout.list_item_call,null);

        ((TextView)result.findViewById(R.id.contactName)).setText(call.ContactName);
        ((TextView)result.findViewById(R.id.phoneNumber)).setText(call.PhoneNumber);


        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy kk:mm");
        String dateTimeText=sdf.format(new Date(call.DateCall));

        ((TextView)result.findViewById(R.id.dateTimeCall)).setText(dateTimeText);

        String durationText="";
        durationText=String.format("%s мин %s сек",String.valueOf(call.Duration/60), String.valueOf (call.Duration % 60));

        ((TextView)result.findViewById(R.id.duration)).setText(durationText);

        ImageView imgTypeCall=(ImageView)result.findViewById(R.id.typeCall);

        switch (call.TypeCall){
            case 0:

                imgTypeCall.setImageResource(R.drawable.missed);

                break;
            case 1:

                imgTypeCall.setImageResource(R.drawable.incoming);

                break;
            case 2:

                imgTypeCall.setImageResource(R.drawable.outgoing);

                break;
            case 3:

                imgTypeCall.setImageResource(R.drawable.missed);

                break;
            case 5:

                imgTypeCall.setImageResource(R.drawable.missed);

                break;
            default:
                break;
        }

        return result;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }


    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults results = new FilterResults();
            ArrayList<Call> tmp=new ArrayList<>();

            for (int i=0; i<_data.size();i++){
                Call call=_data.get(i);

                if (call.PhoneNumber.toLowerCase().contains(charSequence.toString().toLowerCase()) || call.ContactName.toLowerCase().contains(charSequence.toString().toLowerCase()))
                    tmp.add(call);

            }

            results.values=tmp;
            results.count=tmp.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            _filteredData = (ArrayList<Call>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
