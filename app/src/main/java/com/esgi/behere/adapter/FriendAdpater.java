package com.esgi.behere.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esgi.behere.DefaultProfileActivity;
import com.esgi.behere.ProfilFriendGroupActivity;
import com.esgi.behere.R;
import com.esgi.behere.actor.User;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FriendAdpater extends BaseAdapter {

    private List<User> data;
    private static LayoutInflater inflater = null;
    private SharedPreferences sharedPreferences;
    private static final String PREFS = "PREFS";
    private static final String PREFS_ID = "USER_ID";

    public FriendAdpater(Context context, List<User> data) {
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
        sharedPreferences = parent.getContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.fragment_friend, null);
            TextView text = vi.findViewById(R.id.pseudoPubPro);
            text.setText(String.format("%s %s", data.get(position).getName(), data.get(position).getSurname()));
            vi.setOnClickListener(v -> {
                Intent next;
                if (sharedPreferences.getLong(PREFS_ID, 0) != data.get(position).getId()) {
                    next = new Intent(parent.getContext(), ProfilFriendGroupActivity.class);
                    next.putExtra("entityID", data.get(position).getId());
                    next.putExtra("entityType", "User");
                    parent.getContext().startActivity(next);
                } else {
                    next = new Intent(parent.getContext(), DefaultProfileActivity.class);
                    parent.getContext().startActivity(next);
                }
            });
        }
        return vi;
    }
}
