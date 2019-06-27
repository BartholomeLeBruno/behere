package com.esgi.behere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.esgi.behere.R;
import com.esgi.behere.actor.Group;

import java.util.List;


public class GroupAdapter extends BaseAdapter {

    private List<Group> data;
    private static LayoutInflater inflater = null;

    public GroupAdapter(Context context, List<Group> data) {
        // TODO Auto-generated constructor stub
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.fragment_group, null);
        return vi;
    }

}
