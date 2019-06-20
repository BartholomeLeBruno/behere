package com.esgi.behere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.esgi.behere.R;
import com.esgi.behere.actor.User;

import java.util.List;

public class FriendAdpater extends BaseAdapter {

    private Context context;
    private List<User> data;
    private static LayoutInflater inflater = null;

    public FriendAdpater(Context context, List<User> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
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
            vi = inflater.inflate(R.layout.fragment_friend, null);
        TextView text =  vi.findViewById(R.id.pseudoPubPro);
        Button addFriend = vi.findViewById(R.id.btnADDFriend);
        addFriend.setEnabled(false);
        text.setText(data.get(position).getName());
        text.setOnClickListener((View v) -> Toast.makeText(FriendAdpater.inflater.getContext(),"voila"+position,Toast.LENGTH_SHORT).show());
        return vi;
    }
}
