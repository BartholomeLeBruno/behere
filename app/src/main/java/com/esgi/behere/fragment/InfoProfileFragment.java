package com.esgi.behere.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.error.VolleyError;
import com.esgi.behere.LoginActivity;
import com.esgi.behere.R;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class InfoProfileFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private TextView tvDescription, tvName, tvBirthdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        tvDescription = rootView.findViewById(R.id.tvDescription);
        tvName = rootView.findViewById(R.id.tvNameEntity);
        tvBirthdate = rootView.findViewById(R.id.tvBirthDateUser);

        sharedPreferences = rootView.getContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        long entityId = (long) Objects.requireNonNull(Objects.requireNonNull(getActivity()).getIntent().getExtras()).get("entityID");
        String entityType = getActivity().getClass().getSimpleName();

        if(entityType.equals("ProfilFriendGroupActivity")) {
            prepareGetUser();
            ApiUsage mVolleyService = new ApiUsage(mResultCallback, rootView.getContext());
            mVolleyService.getUser(entityId);
        }
        if(entityType.equals("GroupActivity"))
        {
            prepareGetGroup();
            ApiUsage mVolleyService = new ApiUsage(mResultCallback, rootView.getContext());
            mVolleyService.getGroup(entityId);
        }

        return rootView;
    }



    private void prepareGetUser(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String name;
                    String surname;
                    String description;
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("user").toString()).nextValue();
                        name = objres.getString("name");
                        surname = objres.getString("surname");
                        description = objres.getString("description");
                        tvName.setText(String.format("%s %s", name, surname));
                        tvBirthdate.setVisibility(View.VISIBLE);
                        tvBirthdate.setText(objres.getString("birthDate").substring(0,10));
                        tvDescription.setText(description);
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

    private void prepareGetGroup(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String name;
                    String description;
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("group").toString()).nextValue();
                        name = objres.getString("name");
                        description = objres.getString("description");
                        tvName.setText(String.format("%s", name));
                        tvBirthdate.setVisibility(View.INVISIBLE);
                        tvDescription.setText(description);
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