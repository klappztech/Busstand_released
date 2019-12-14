package com.klappztech.platform;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter {

    private List<String> BusNo = new ArrayList<>();
    private List<String> Destination = new ArrayList<>();
    private Context context;

    public ListViewAdapter(List<String> BusNo, List<String> Destination, Context context)
    {
        super(context,R.layout.item_layout,BusNo);
        this.context        = context;
        this.BusNo          = BusNo;
        this.Destination    = Destination;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View row = inflater.inflate(R.layout.item_layout,parent,false);
        TextView tvBusNo = row.findViewById(R.id.bus_no);
        tvBusNo.setText(BusNo.get(position));

        TextView tvRoute = row.findViewById(R.id.route);
        tvRoute.setText(Destination.get(position));

        return row;
    }
}
