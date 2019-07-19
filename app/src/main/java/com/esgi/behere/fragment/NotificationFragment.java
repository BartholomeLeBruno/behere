package com.esgi.behere.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.error.VolleyError;
import com.esgi.behere.R;
import com.esgi.behere.actor.Notification;
import com.esgi.behere.adapter.NotificationAdapter;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.PopupAchievement;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class NotificationFragment extends Fragment {

    private static final String PREFS = "PREFS";
    private SharedPreferences sharedPreferences;
    private static ListView listNotification;
    private VolleyCallback mResultCallback = null;
    private static ArrayList<Notification> notifications = new ArrayList<>();
    ApiUsage mVolleyService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification_list, container, false);
        listNotification = rootView.findViewById(R.id.listNotification);
        sharedPreferences = rootView.getContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        prepareGetNotification();
        mVolleyService = new ApiUsage(mResultCallback, rootView.getContext());
        mVolleyService.getNotification(sharedPreferences.getLong(getString(R.string.prefs_id), 0), sharedPreferences.getString(getString(R.string.access_token), ""));
        return rootView;

    }

    private void prepareGetNotification() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                notifications = new ArrayList<>();
                try {
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONArray resNotification = (JSONArray) parser.parse(response.get("notification").toString());
                        JSONObject objres;
                        if (!resNotification.isEmpty()) {
                            for (Object unres : resNotification) {
                                objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                Notification notification = new Notification();
                                notification.setId(objres.getLong("id"));
                                notification.setText(objres.getString("texte"));
                                notification.setType(objres.getString("type"));
                                notification.setUser_id(sharedPreferences.getLong(getString(R.string.prefs_id), 0));
                                if(JSONObject.NULL.equals(objres.get("other_user_id")))
                                    notification.setOther_user_id(0);
                                else
                                    notification.setOther_user_id(objres.getLong("other_user_id"));
                                if (!objres.isNull("group_id"))
                                    notification.setGroup_id(objres.getLong("group_id"));
                                notifications.add(notification);
                            }
                        }
                        NotificationAdapter adapter = new NotificationAdapter(Objects.requireNonNull(getContext()), notifications);
                        listNotification.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(getView());
                }
            }
        };
    }

    public static void removeNotification(Notification notification)
    {
        notifications.remove(notification);
    }

    public static void refreshAdapter(Context v)
    {
        NotificationAdapter adapter = new NotificationAdapter(v, notifications);
        listNotification.setAdapter(adapter);
    }
}