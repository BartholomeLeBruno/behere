package com.esgi.behere.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.error.VolleyError;
import com.esgi.behere.LoginActivity;
import com.esgi.behere.R;
import com.esgi.behere.actor.Publication;
import com.esgi.behere.adapter.PublicationAdapter;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class WallProfileFragment extends Fragment {

    private ArrayList<Publication> publications = new ArrayList<>();
    private ListView recyclerView;
    private VolleyCallback mResultCallback = null;
    private long entityID;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_publication_list, container, false);

        recyclerView = rootView.findViewById(R.id.listPublication);
        // Initialize contacts
        SharedPreferences sharedPreferences = rootView.getContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        if(Objects.requireNonNull(Objects.requireNonNull(getActivity()).getIntent().getExtras()).containsKey("entityID"))
            entityID = Objects.requireNonNull(getActivity().getIntent().getExtras()).getLong("entityID");
        ApiUsage mVolleyService;
        if (Objects.requireNonNull(getActivity().getIntent().getExtras()).containsKey("group")) {
            prepareGetGroupComments();
            mVolleyService = new ApiUsage(mResultCallback, rootView.getContext());
            mVolleyService.getAllCommentsGroups(entityID);

        } else {
            prepareGetAllComments();
            if (getActivity().getIntent().getExtras().containsKey("entityID")) {
                mVolleyService = new ApiUsage(mResultCallback, rootView.getContext());
                mVolleyService.getAllComments(entityID);
            } else {
                mVolleyService = new ApiUsage(mResultCallback, rootView.getContext());
                mVolleyService.getAllComments(sharedPreferences.getLong(getString(R.string.prefs_id), 0));
            }
        }
        // Create adapter passing in the sample user data
        // That's all!

        return rootView;
    }



    private void prepareGetAllComments() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (!(boolean) response.get("error")) {
                        publications = new ArrayList<>();
                        JSONParser parser = new JSONParser();
                        Log.d("response", response.toString());
                        loadComments(response, formatter, parser, "commentsBrewery", "brewery_id", "brewery");
                        loadComments(response, formatter, parser, "commentsBars", "bar_id", "bar");
                        loadComments(response, formatter, parser, "commentsBeers", "beer_id", "beer");
                        JSONArray resCommentUser = (JSONArray) parser.parse(response.get("commentsUsers").toString());
                        if (!resCommentUser.isEmpty()) {
                            for (Object unres : resCommentUser) {
                                fillPublication(formatter, unres, "user_comment_id", "user");
                            }
                        }
                        Log.d("publication", publications.toString());
                        publications.forEach(v -> Log.d("publication", v.getType() + v.getContent()));
                        //Collections.sort(publications);
                        PublicationAdapter adapter = new PublicationAdapter(Objects.requireNonNull(getContext()), publications);
                        recyclerView.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            private void loadComments(JSONObject response, SimpleDateFormat formatter, JSONParser parser, String commentsType, String entityID, String entity) throws org.json.simple.parser.ParseException, JSONException, ParseException {
                JSONArray resComment = (JSONArray) parser.parse(response.get(commentsType).toString());
                if (!resComment.isEmpty()) {
                    for (Object unres : resComment) {
                        fillPublication(formatter, unres, entityID, entity);
                    }
                }
            }

            private void fillPublication(SimpleDateFormat formatter, Object unres, String entityId, String entity) throws JSONException, ParseException {
                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                String resDate = objres.getString("created_at").replace("T", " ").replace(".000Z", " ");
                Date created_at = formatter.parse(resDate);
                publications.add(new Publication("", objres.getString("text"), created_at, objres.getLong(entityId), entity));
            }

            @Override
            public void onError(VolleyError error) {
                Intent loginActivity = new Intent(getContext(), LoginActivity.class);
                startActivity(loginActivity);

            }
        };
    }

    private void prepareGetGroupComments() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (!(boolean) response.get("error")) {
                        publications = new ArrayList<>();
                        JSONParser parser = new JSONParser();
                        JSONArray resCommentGroup = (JSONArray) parser.parse(response.get("commentsGroup").toString());
                        if (!resCommentGroup.isEmpty()) {
                            for (Object unres : resCommentGroup) {
                                JSONObject objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                String resDate = objres.getString("created_at").replace("T", " ").replace(".000Z", " ");
                                Date created_at = formatter.parse(resDate);
                                publications.add(new Publication("", objres.getString("text"), created_at, objres.getLong("user_id"), "group"));
                            }
                        }
                        PublicationAdapter adapter = new PublicationAdapter(Objects.requireNonNull(getContext()), publications);
                        recyclerView.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onError(VolleyError error) { }
        };
    }

}
