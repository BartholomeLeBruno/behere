package com.esgi.behere.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.error.VolleyError;
import com.esgi.behere.ProfilFriendGroupActivity;
import com.esgi.behere.R;
import com.esgi.behere.actor.Notification;
import com.esgi.behere.fragment.NotificationFragment;
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
        sharedPreferences = parent.getContext().getSharedPreferences(parent.getContext().getString(R.string.prefs), MODE_PRIVATE);
        TextView agree, denied, profil, textMessage;
        Button agreebtn, deniedbtn, profilbtn;
        TextView textMessageComment;
        View vi = convertView;
        if (vi == null) {
            switch (data.get(position).getType()) {
                case "Friends":
                    vi = inflater.inflate(R.layout.fragment_add_friend, parent, false);
                    textMessage = vi.findViewById(R.id.tvDemand);
                    textMessage.setText(data.get(position).getText());
                    agreebtn = vi.findViewById(R.id.tvAdd);
                    deniedbtn = vi.findViewById(R.id.tvNo);
                    profilbtn = vi.findViewById(R.id.tvSeeProfil);
                    profilbtn.setOnClickListener(v -> seeProfil(v, data.get(position).getOther_user_id()));
                    agreebtn.setOnClickListener(v -> addFriend(v, data.get(position).getOther_user_id(), data.get(position).getId(), data.get(position)));
                    deniedbtn.setOnClickListener(v -> denyFriend(v, data.get(position).getId(), data.get(position)));
                    break;
                case "Groups":
                    vi = inflater.inflate(R.layout.fragment_add_friend, parent, false);
                    textMessage = vi.findViewById(R.id.tvDemand);
                    textMessage.setText(data.get(position).getText());
                    agreebtn = vi.findViewById(R.id.tvAdd);
                    deniedbtn = vi.findViewById(R.id.tvNo);
                    profilbtn = vi.findViewById(R.id.tvSeeProfil);
                    profilbtn.setOnClickListener(v -> seeProfil(v, data.get(position).getOther_user_id()));
                    agreebtn.setOnClickListener(v -> addToGroup(v, data.get(position).getGroup_id(), data.get(position).getOther_user_id(), data.get(position).getId(), data.get(position)));
                    deniedbtn.setOnClickListener(v -> denyFriend(v, data.get(position).getId(), data.get(position)));
                    break;
                case "Comments":
                    vi = inflater.inflate(R.layout.fragment_comment_notif, parent, false);
                    textMessageComment = vi.findViewById(R.id.tvNotificationComment);
                    textMessageComment.setText(data.get(position).getText());
                    break;
                case "MarketNotif":
                    vi = inflater.inflate(R.layout.fragment_comment_notif, parent, false);
                    textMessageComment = vi.findViewById(R.id.tvNotificationComment);
                    textMessageComment.setText(data.get(position).getText());
                    break;
                default:
                    vi = inflater.inflate(R.layout.fragment_comment_notif, parent, false);
                    textMessageComment = vi.findViewById(R.id.tvNotificationComment);
                    textMessageComment.setText(data.get(position).getText());
                    break;
            }
        }
        return vi;
    }

    private void seeProfil(View v, long idProfil) {
        Intent profil = new Intent(v.getContext(), ProfilFriendGroupActivity.class);
        profil.putExtra("entityID", idProfil);
        v.getContext().startActivity(profil);
    }

    private void addFriend(View v, long entityId, long notifID, Notification notification) {
        prepareEmpty(v);
        mVolleyService = new ApiUsage(mResultCallback, v.getContext());
        mVolleyService.addFriend(sharedPreferences.getLong(v.getContext().getString(R.string.prefs_id), 0), (int) entityId, sharedPreferences.getString(v.getContext().getString(R.string.access_token), ""));
        mVolleyService = new ApiUsage(mResultCallback, v.getContext());
        mVolleyService.deleteNotification(notifID, sharedPreferences.getString(v.getContext().getString(R.string.access_token), ""));
        NotificationFragment.removeNotification(notification);
        NotificationFragment.refreshAdapter(v.getContext());
        //DefaultProfileActivity.updateNbFriends();
    }

    private void addToGroup(View v, long entityId, long user_id, long notifID, Notification notification) {
        prepareEmpty(v);
        mVolleyService = new ApiUsage(mResultCallback, v.getContext());
        mVolleyService.addUserInGroup(user_id, entityId, sharedPreferences.getString(v.getContext().getString(R.string.access_token), ""));
        mVolleyService = new ApiUsage(mResultCallback, v.getContext());
        mVolleyService.deleteNotification(notifID, sharedPreferences.getString(v.getContext().getString(R.string.access_token), ""));
        NotificationFragment.removeNotification(notification);
        NotificationFragment.refreshAdapter(v.getContext());
    }

    private void denyFriend(View v, long notifID, Notification notification) {
        prepareEmpty(v);
        mVolleyService = new ApiUsage(mResultCallback, v.getContext());
        mVolleyService.deleteNotification(notifID, sharedPreferences.getString(v.getContext().getString(R.string.access_token), ""));
        NotificationFragment.removeNotification(notification);
        NotificationFragment.refreshAdapter(v.getContext());
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
