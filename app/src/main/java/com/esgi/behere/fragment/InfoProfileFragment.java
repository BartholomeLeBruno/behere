package com.esgi.behere.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.esgi.behere.LoginActivity;
import com.esgi.behere.R;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;

import static android.content.Context.MODE_PRIVATE;


public class InfoProfileFragment extends Fragment {

    private static final String PREFS = "PREFS";
    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private TextView tvEmail, tvName, tvBirthdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        tvEmail = rootView.findViewById(R.id.tvEmailEntity);
        tvName = rootView.findViewById(R.id.tvNameEntity);
        tvBirthdate = rootView.findViewById(R.id.tvBirthDateUser);

        sharedPreferences = rootView.getContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        long entityId = (long) getActivity().getIntent().getExtras().get("entityID");
        prepareGetUser();
        ApiUsage mVolleyService = new ApiUsage(mResultCallback, rootView.getContext());
        mVolleyService.getUser(entityId);

        return rootView;
    }



    private void prepareGetUser(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String name;
                    String surname;
                    String email;
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("user").toString()).nextValue();
                        name = objres.getString("name");
                        surname = objres.getString("surname");
                        email = objres.getString("email");
                        tvName.setText(String.format("%s %s", name, surname));
                        tvBirthdate.setText(objres.getString("birthDate").substring(0,10));
                        tvEmail.setText(email);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) {
                Intent loginActivity = new Intent(getContext(), LoginActivity.class);
                sharedPreferences.getAll().clear();
                startActivity(loginActivity);
            }
        };
    }
}