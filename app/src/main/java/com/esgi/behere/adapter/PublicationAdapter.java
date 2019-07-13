package com.esgi.behere.adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
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


public class PublicationAdapter extends BaseAdapter {

    private List<Publication> data;
    private static LayoutInflater inflater = null;
    private VolleyCallback mResultCallback = null;
    private TextView textPseudo;

    public PublicationAdapter(Context context, List<Publication> data) {
        // TODO Auto-generated constructor stub
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SparseBooleanArray expanded = new SparseBooleanArray();
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
        Log.d("ici", "ici");
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.fragment_publication, null);
            TextView text = vi.findViewById(R.id.PublicationText);
            text.setText(data.get(position).getContent());
            textPseudo = vi.findViewById(R.id.pseudoPubPro);
            switch (data.get(position).getType()) {
                case "bar":
                    prepareGetBar(vi);
                    ApiUsage mVolleyService = new ApiUsage(mResultCallback, vi.getContext());
                    mVolleyService.getBar(data.get(position).getFrom_id());
                    break;
                case "beer":
                    prepareGetBeer(vi);
                    mVolleyService = new ApiUsage(mResultCallback, vi.getContext());
                    mVolleyService.getBeer(data.get(position).getFrom_id());
                    break;
                case "brewery":
                    prepareGetBrewery(vi);
                    mVolleyService = new ApiUsage(mResultCallback, vi.getContext());
                    mVolleyService.getBrewery(data.get(position).getFrom_id());
                    break;
                case "user":
                    prepareGetUser(vi);
                    mVolleyService = new ApiUsage(mResultCallback, vi.getContext());
                    mVolleyService.getUser(data.get(position).getFrom_id());
                    break;
                case "group":
                    prepareGetUser(vi);
                    mVolleyService = new ApiUsage(mResultCallback, vi.getContext());
                    mVolleyService.getUser(data.get(position).getFrom_id());
                    break;
            }
        }
        return vi;
    }

    private void prepareGetBar(View vi) {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("bar").toString()).nextValue();
                        textPseudo = vi.findViewById(R.id.pseudoPubPro);
                        textPseudo.setText(objres.getString("name"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
            }
        };
    }

    private void prepareGetBrewery(View vi) {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("brewery").toString()).nextValue();
                        textPseudo = vi.findViewById(R.id.pseudoPubPro);
                        textPseudo.setText(objres.getString("name"));

                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
            }
        };
    }

    private void prepareGetBeer(View vi) {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("beer").toString()).nextValue();
                        textPseudo = vi.findViewById(R.id.pseudoPubPro);
                        textPseudo.setText(objres.getString("name"));

                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
            }
        };
    }

    private void prepareGetUser(View vi) {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("user").toString()).nextValue();
                        textPseudo = vi.findViewById(R.id.pseudoPubPro);
                        textPseudo.setText(String.format("%s %s", objres.getString("name"), objres.getString("surname")));

                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
            }
        };
    }

    private void prepareGetGroup(View vi) {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("user").toString()).nextValue();
                        textPseudo = vi.findViewById(R.id.pseudoPubPro);
                        textPseudo.setText(String.format("%s %s", objres.getString("name"), objres.getString("surname")));

                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
            }
        };
    }

}
