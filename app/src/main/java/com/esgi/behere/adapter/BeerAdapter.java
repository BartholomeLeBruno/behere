package com.esgi.behere.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.atomic.AtomicLong;

import static android.content.Context.MODE_PRIVATE;

public class BeerAdapter extends BaseAdapter {

    private List<Beer> data;
    private static LayoutInflater inflater = null;
    private VolleyCallback mResultCallback = null;
    private LinearLayout linearLayoutStar;
    private SharedPreferences sharedPreferences;


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
            sharedPreferences = vi.getContext().getSharedPreferences(vi.getContext().getString(R.string.prefs), MODE_PRIVATE);
            vi.setOnClickListener(v -> onButtonShowPopupWindowClick(v, position, parent));

        }
        return vi;
    }

    private void onButtonShowPopupWindowClick(View view, int position, ViewGroup parent) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
        View popupView = inflater.inflate(R.layout.popup_beer, parent, false);
        linearLayoutStar = popupView.findViewById(R.id.linearLayoutStar);
        AtomicLong note = new AtomicLong();
        note.set(0);
        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);        // show the popup window
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
        ImageView firstStar = popupView.findViewById(R.id.firstStar);
        ImageView secondStar = popupView.findViewById(R.id.secondStar);
        ImageView thirdStar = popupView.findViewById(R.id.thirdStar);
        ImageView fourthStar = popupView.findViewById(R.id.fourthStar);
        ImageView fifthStar = popupView.findViewById(R.id.fifthStar);
        firstStar.setOnClickListener(first -> {
            firstStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            secondStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            fourthStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            fifthStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            note.set(1);
        });
        secondStar.setOnClickListener(second -> {
            firstStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            secondStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            fourthStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            fifthStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            note.set(2);
        });
        thirdStar.setOnClickListener(third -> {
            firstStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            secondStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            fourthStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            fifthStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            note.set(3);
        });
        fourthStar.setOnClickListener(fourth -> {
            firstStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            secondStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            fourthStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            fifthStar.setImageResource(R.drawable.ic_star_border_yellow_24dp);
            note.set(4);
        });
        fifthStar.setOnClickListener(fifth -> {
            firstStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            secondStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            fourthStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            fifthStar.setImageResource(R.drawable.ic_star_yellow_24dp);
            note.set(5);
        });
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        Button btnSendComment = popupView.findViewById(R.id.btnSendComment);
        EditText tvComment = popupView.findViewById(R.id.tvComment);
        btnSendComment.setOnClickListener((View v) -> {
            prepareAddComment(v);
            sharedPreferences = v.getContext().getSharedPreferences(v.getContext().getString(R.string.prefs), MODE_PRIVATE);
            ApiUsage mVolleyServiceBeer = new ApiUsage(mResultCallback, view.getContext());
            mVolleyServiceBeer.addCommentsToBeer(tvComment.getText().toString(), (int) data.get(position).getId(), sharedPreferences.getString(v.getContext().getString(R.string.access_token), ""));
            prepareEmpty(v);
            ApiUsage mVolleyServiceNote = new ApiUsage(mResultCallback, view.getContext());
            mVolleyServiceNote.addNoteToBeer(note.get(), data.get(position).getId(), sharedPreferences.getString(v.getContext().getString(R.string.access_token), ""));
            popupWindow.dismiss();
        });
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

    private void prepareAddComment(View v) {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if ((boolean) response.get("error")) {
                        Toast.makeText(v.getContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(v);
                }
            }
        };
    }

    private void prepareEmpty(View v) {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(v);
                }
            }
        };
    }
}
