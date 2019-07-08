package com.esgi.behere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.esgi.behere.R;
import com.esgi.behere.actor.Reservation;

import java.util.List;

public class MyReservationAdapter extends BaseAdapter {

    private List<Reservation> data;
    private static LayoutInflater inflater = null;

    public MyReservationAdapter(Context context, List<Reservation> data) {
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.fragment_reservation, null);
        return vi;
    }
}
