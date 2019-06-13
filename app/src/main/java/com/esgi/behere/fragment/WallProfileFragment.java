package com.esgi.behere.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.esgi.behere.LoginActivity;
import com.esgi.behere.MapActivity;
import com.esgi.behere.R;
import com.esgi.behere.actor.Publication;
import com.esgi.behere.adapter.PublicationAdapter;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class WallProfileFragment extends Fragment {

    private ArrayList<Publication> publications =new ArrayList<>();
    RecyclerView recyclerView;

    private static final String PREFS = "PREFS";
    private static final String PREFS_ID = "USER_ID";
    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_publication_list, container, false);

        recyclerView = rootView.findViewById(R.id.listPublication);
        // Initialize contacts
        sharedPreferences = rootView.getContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        prepareGetAllComments();
        prepareGetUser();
        mVolleyService = new ApiUsage(mResultCallback,rootView.getContext());
        mVolleyService.getAllComments(sharedPreferences.getLong(PREFS_ID,0));
        // Create adapter passing in the sample user data
        PublicationAdapter adapter = new PublicationAdapter(publications);
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // That's all!

        return rootView;
    }

    /*ArrayList<Publication> getAllPublicationsOfActualUsers()
    {
        ApiUsage apiUsage = new ApiUsage()
        return null;
    }*/

    void prepareGetUser(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        try {
                            Intent mapActivity = new Intent(getContext(), MapActivity.class);
                            mapActivity.putExtra("userID", sharedPreferences.getLong(PREFS_ID, 0));
                            startActivity(mapActivity);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Intent loginActivity = new Intent(getContext(), LoginActivity.class);
                        startActivity(loginActivity);
                    }
                }
                catch (Exception e)
                {

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

    void prepareGetAllComments(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONParser parser = new JSONParser();
                        JSONArray resCommentBar = (JSONArray) parser.parse(response.get("commentsBars").toString());
                        if (!resCommentBar.isEmpty()) {
                            for (Object unres : resCommentBar) {
                                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                publications.add(new Publication("Test", objres.getString("text")));
                            }
                        }
                        JSONArray resCommentBrewery = (JSONArray) parser.parse(response.get("commentsBrewery").toString());
                        if (!resCommentBrewery.isEmpty()) {
                            for (Object unres : resCommentBrewery) {
                                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                publications.add(new Publication("Test", objres.getString("text")));
                            }
                        }
                        JSONArray resCommentBeer = (JSONArray) parser.parse(response.get("commentsBeers").toString());
                        if (!resCommentBeer.isEmpty()) {
                            for (Object unres : resCommentBeer) {
                                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                publications.add(new Publication("Test", objres.getString("text")));
                            }
                        }
                        PublicationAdapter adapter = new PublicationAdapter(publications);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
