package com.esgi.behere.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.esgi.behere.CommentaryListActivity;
import com.esgi.behere.R;
import com.esgi.behere.actor.Beer;
import com.esgi.behere.tools.StarTools;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.PopupAchievement;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.List;

public class BeerAdapter extends BaseAdapter {

    private List<Beer> data;
    private static LayoutInflater inflater = null;
    private VolleyCallback mResultCallback = null;
    private LinearLayout linearLayoutStar;


    public BeerAdapter(Context context, List<Beer> data) {
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
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.fragment_beer, parent, false);
            TextView text = vi.findViewById(R.id.tvNameBeer);
            text.setText(data.get(position).getName());
            vi.setOnClickListener(v -> onButtonShowPopupWindowClick(v,position, parent));

        }
        return vi;
    }

    private void onButtonShowPopupWindowClick(View view, int position, ViewGroup parent) {

        // inflate the layout of the popup window
        View popupView = inflater.inflate(R.layout.popup_beer, parent,false);
        linearLayoutStar = popupView.findViewById(R.id.linearLayoutStar);
        linearLayoutStar.removeAllViews();
        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupView.setElevation(15);
        TextView tvName = popupView.findViewById(R.id.beerName);
        TextView tvDescription = popupView.findViewById(R.id.beerDescription);
        Button btnSeeComment = popupView.findViewById(R.id.btnSeeCommentBeer);
        btnSeeComment.setOnClickListener(v -> {
            Intent allComments = new Intent(parent.getContext(), CommentaryListActivity.class);
            allComments.putExtra("entityID", data.get(position).getId());
            allComments.putExtra("entityType", "Beer");
            parent.getContext().startActivity(allComments);
        });
        prepareStar(popupView);
        ApiUsage mVolleyService = new ApiUsage(mResultCallback, popupView.getContext());
        mVolleyService.getNotesBeer(data.get(position).getId());
        tvName.setText(String.format("%s Origine : %s", data.get(position).getName(), data.get(position).getOrigin()));
        tvDescription.setText(data.get(position).getDescription());
        //popupWindow.dismiss();
    }

    private void prepareStar(View view) {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        double note = 0;
                        JSONParser parser = new JSONParser();
                        JSONArray resNote;
                        resNote = (JSONArray) parser.parse(response.get("notesBeer").toString());
                        JSONObject objres;
                        if (!resNote.isEmpty()) {
                            for (Object unres : resNote) {
                                objres = (JSONObject) new JSONTokener(unres.toString()).nextValue();
                                note = note + objres.getDouble("note");
                            }
                            note = note / resNote.size();
                        }
                        StarTools starTools = new StarTools(note, view.getContext(), linearLayoutStar);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(view);
                }
            }
        };
    }
}
