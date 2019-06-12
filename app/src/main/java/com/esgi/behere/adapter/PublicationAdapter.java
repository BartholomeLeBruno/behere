package com.esgi.behere.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esgi.behere.R;
import com.esgi.behere.actor.Publication;

import java.util.List;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private List<Publication> mPublication;

    // Pass in the contact array into the constructor
    public PublicationAdapter(List<Publication> publications) {
        mPublication = publications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.fragment_publication, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        // Get the data model based on position
        Publication publication = mPublication.get(i);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        TextView textView2 = viewHolder.messageButton;

        textView.setText("Sarah Croche");
        textView2.setText(publication.getContent());
    }

    @Override
    public int getItemCount() {
        return mPublication.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
     class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
         TextView nameTextView;
         TextView messageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
         ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.funText);
            messageButton = itemView.findViewById(R.id.PublicationText);
        }
    }

}
