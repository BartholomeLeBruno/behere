package com.esgi.behere.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.esgi.behere.LoginActivity;
import com.esgi.behere.R;
import com.esgi.behere.actor.Publication;
import com.esgi.behere.adapter.PublicationAdapter;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class WallProfileFragment extends Fragment {

    private ArrayList<Publication> publications =new ArrayList<>();
    private ListView recyclerView;

    private static final String PREFS = "PREFS";
    private static final String PREFS_ID = "USER_ID";
    private VolleyCallback mResultCallback = null;
    ApiUsage mVolleyService;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_publication_list, container, false);

        recyclerView = rootView.findViewById(R.id.listPublication);
        // Initialize contacts
        SharedPreferences sharedPreferences = rootView.getContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        prepareGetAllComments();
        if(Objects.requireNonNull(Objects.requireNonNull(getActivity()).getIntent().getExtras()).get("entityID") != null
            && sharedPreferences.getLong(PREFS_ID, 0) != (long) Objects.requireNonNull(Objects.requireNonNull(getActivity()).getIntent().getExtras()).get("entityID"))
        {
            mVolleyService = new ApiUsage(mResultCallback, rootView.getContext());
            mVolleyService.getAllComments((long) Objects.requireNonNull(Objects.requireNonNull(getActivity()).getIntent().getExtras()).get("entityID"));
        }
        else {
            mVolleyService = new ApiUsage(mResultCallback, rootView.getContext());
            mVolleyService.getAllComments(sharedPreferences.getLong(PREFS_ID, 0));
        }
        // Create adapter passing in the sample user data
        PublicationAdapter adapter = new PublicationAdapter(Objects.requireNonNull(getContext()), publications);
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        // That's all!

        return rootView;
    }



    private void prepareGetAllComments(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (!(boolean) response.get("error")) {
                        publications = new ArrayList<>();
                        JSONParser parser = new JSONParser();
                        JSONArray resCommentBrewery = (JSONArray) parser.parse(response.get("commentsBrewery").toString());
                        if (!resCommentBrewery.isEmpty()) {
                            for (Object unres : resCommentBrewery) {
                                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                String resDate = objres.getString("created_at").replace("T"," ").replace(".000Z", " ");
                                Date created_at= formatter.parse(resDate);
                                publications.add(new Publication("", objres.getString("text"),created_at,objres.getLong("brewery_id"),"brewery"));
                            }
                        }
                        JSONArray resCommentBar = (JSONArray) parser.parse(response.get("commentsBars").toString());
                        if (!resCommentBar.isEmpty()) {
                            for (Object unres : resCommentBar) {
                                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                String resDate = objres.getString("created_at").replace("T"," ").replace(".000Z", " ");
                                 Date created_at= formatter.parse(resDate);
                                publications.add(new Publication("", objres.getString("text"),created_at,objres.getLong("bar_id"),"bar"));
                            }
                        }
                        JSONArray resCommentBeer = (JSONArray) parser.parse(response.get("commentsBeers").toString());
                        if (!resCommentBeer.isEmpty()) {
                            for (Object unres : resCommentBeer) {
                                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                String resDate = objres.getString("created_at").replace("T"," ").replace(".000Z", " ");
                                Date created_at= formatter.parse(resDate);
                                publications.add(new Publication("", objres.getString("text"),created_at, objres.getLong("beer_id"),"beer"));
                            }
                        }
                        Collections.sort(publications);
                        PublicationAdapter adapter = new PublicationAdapter(Objects.requireNonNull(getContext()),publications);
                        recyclerView.setAdapter(adapter);
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
            @Override
            public void onError(VolleyError error) {
                Intent loginActivity = new Intent(getContext(), LoginActivity.class);
                startActivity(loginActivity);

            }
        };
    }

}
