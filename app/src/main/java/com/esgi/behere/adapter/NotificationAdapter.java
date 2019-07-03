package com.esgi.behere.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.android.volley.VolleyError;
import com.esgi.behere.ProfilFriendGroupActivity;
import com.esgi.behere.R;
import com.esgi.behere.actor.Notification;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.PopupAchievement;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NotificationAdapter extends BaseAdapter {

    private List<Notification> data;
    private static LayoutInflater inflater = null;
    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private static final String PREFS = "PREFS";

    public NotificationAdapter(Context context, List<Notification> data) {
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
        switch (data.get(position).getType()) {
            case "Friends":
                if (vi == null)
                    vi = inflater.inflate(R.layout.fragment_add_friend, null);
                TextView textMessage = vi.findViewById(R.id.tvDemand);
                textMessage.setText(data.get(position).getText());
                TextView agree = vi.findViewById(R.id.tvAdd);
                TextView denied = vi.findViewById(R.id.tvNo);
                TextView profil = vi.findViewById(R.id.tvSeeProfil);
                profil.setOnClickListener(v -> seeProfil(v, data.get(position).getOther_user_id()));
                agree.setOnClickListener(v -> addFriend(v, data.get(position).getOther_user_id()));
                denied.setOnClickListener(v -> denyFriend(v, data.get(position).getId()));
                break;
        }
        return vi;
    }

    private void seeProfil(View v, long idProfil) {
        Intent profil = new Intent(v.getContext(), ProfilFriendGroupActivity.class);
        profil.putExtra("entityID", idProfil);
        v.getContext().startActivity(profil);
    }

    private void addFriend(View v, long entityId) {
        prepareEmpty(v);
        mVolleyService = new ApiUsage(mResultCallback, v.getContext());
        mVolleyService.addFriend(sharedPreferences.getLong(v.getContext().getString(R.string.prefs_id), 0), (int) entityId, sharedPreferences.getString(v.getContext().getString(R.string.access_token), ""));

    }

    private void denyFriend(View v, long notifID) {
        prepareEmpty(v);
        mVolleyService = new ApiUsage(mResultCallback, v.getContext());
        mVolleyService.deleteNotification(notifID, sharedPreferences.getString(v.getContext().getString(R.string.access_token), ""));
    }


    private void prepareEmpty(View v) {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(v);
                }
            }
        };
    }


}
