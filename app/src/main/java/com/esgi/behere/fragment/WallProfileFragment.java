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

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class WallProfileFragment extends Fragment {

    private ArrayList<Publication> publications =new ArrayList<>();
    ListView recyclerView;

    private static final String PREFS = "PREFS";
    private static final String PREFS_ID = "USER_ID";
     SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
     ApiUsage mVolleyService;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_publication_list, container, false);

        recyclerView = rootView.findViewById(R.id.listPublication);
        // Initialize contacts
        sharedPreferences = rootView.getContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        prepareGetAllComments();
        //prepareGetUser();
        mVolleyService = new ApiUsage(mResultCallback,rootView.getContext());
        mVolleyService.getAllComments(sharedPreferences.getLong(PREFS_ID,0));
        // Create adapter passing in the sample user data
        PublicationAdapter adapter = new PublicationAdapter(getContext(), publications);
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        // That's all!

        return rootView;
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
                        PublicationAdapter adapter = new PublicationAdapter(getContext(),publications);
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
