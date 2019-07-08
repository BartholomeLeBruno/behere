package com.esgi.behere.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.esgi.behere.R;
import com.esgi.behere.actor.Beer;
import com.esgi.behere.actor.BeerType;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

import static com.esgi.behere.LexiconActivity.gridViewBeers;
import static com.esgi.behere.LexiconActivity.listView_result;

public class SupplementTypeOfBeerAdapter extends BaseAdapter {

    private List<BeerType> data;
    private static LayoutInflater inflater = null;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private ArrayList<Beer> beers;

    public SupplementTypeOfBeerAdapter(Context context, List<BeerType> data) {
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
            vi = inflater.inflate(R.layout.fragment_supplement_typeofbeer, null);
            TextView text = vi.findViewById(R.id.beerTypeName);
            text.setText(data.get(position).getName());
            long idTypeBeer = data.get(position).getId();
            vi.setOnClickListener(v -> {
                listView_result.setVisibility(View.INVISIBLE);
                prepareGetBeer(v);
                mVolleyService = new ApiUsage(mResultCallback, v.getContext());
                mVolleyService.getAllBeerWithTypeOfBeer(idTypeBeer);
            });
        }
        return vi;
    }

    private void prepareGetBeer(View v) {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        Log.d("voila", response.toString());
                        beers = new ArrayList<>();
                        JSONParser parser = new JSONParser();
                        JSONArray resBeer = (JSONArray) parser.parse(response.get("beer").toString());
                        JSONObject objres;
                        Beer oneBeer = new Beer();
                        if (!resBeer.isEmpty()) {
                            for (Object beer : resBeer) {
                                objres = (JSONObject) new JSONTokener(beer.toString()).nextValue();
                                oneBeer.setId(objres.getLong("id"));
                                oneBeer.setColor(objres.getString("color"));
                                oneBeer.setDescription(objres.getString("description"));
                                oneBeer.setName(objres.getString("name"));
                                oneBeer.setOrigin(objres.getString("origin"));
                                beers.add(oneBeer);
                            }
                            gridViewBeers.setAdapter(new BeerAdapter(v.getContext(), beers));
                        }

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
