package com.esgi.behere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.esgi.behere.R;
import com.esgi.behere.actor.Publication;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;


public class CommentMarketAdapter extends BaseAdapter {

    private List<Publication> data;
    private static LayoutInflater inflater = null;
    private VolleyCallback mResultCallback = null;

    public CommentMarketAdapter(Context context, List<Publication> data) {
        // TODO Auto-generated constructor stub
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.fragment_publication, null);
            TextView text = vi.findViewById(R.id.PublicationText);
            text.setText(data.get(position).getContent());
            prepareGetUser(vi);
            ApiUsage mVolleyService = new ApiUsage(mResultCallback, vi.getContext());
            mVolleyService.getUser(data.get(position).getFrom_id());
        }
        return vi;
    }
    private void prepareGetUser(View vi) {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("user").toString()).nextValue();
                        TextView textPseudo = vi.findViewById(R.id.pseudoPubPro);
                        textPseudo.setText(String.format("%s %s", objres.getString("name"), objres.getString("surname")));
                        //todo get image

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
