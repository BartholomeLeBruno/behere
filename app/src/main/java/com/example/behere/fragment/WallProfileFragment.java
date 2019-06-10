package com.example.behere.fragment;

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

import com.example.behere.R;
import com.example.behere.actor.Publication;
import com.example.behere.adapter.PublicationAdapter;

import java.util.ArrayList;

public class WallProfileFragment extends Fragment {

    ArrayList<Publication> publications;
    RecyclerView recyclerView;
    private static final String PREFS = "PREFS";
    private static final String PREFS_ID = "USER_ID";
    private SharedPreferences sharedPreferences;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_publication_list, container, false);

        recyclerView = rootView.findViewById(R.id.listPublication);
        // Initialize contacts
        publications = Publication.createPublicationList(20);
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


}
