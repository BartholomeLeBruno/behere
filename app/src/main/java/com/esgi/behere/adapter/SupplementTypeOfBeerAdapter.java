package com.esgi.behere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esgi.behere.LexiconActivity;
import com.esgi.behere.R;
import com.esgi.behere.actor.BeerType;

import java.util.List;

public class SupplementTypeOfBeerAdapter extends BaseAdapter {

    private List<BeerType> data;
    private static LayoutInflater inflater = null;

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
            vi = inflater.inflate(R.layout.fragment_supplement_typeofbeer, parent, false);
            TextView text = vi.findViewById(R.id.beerTypeName);
            text.setText(data.get(position).getName());
            vi.setOnClickListener(v -> LexiconActivity.listView_result.setVisibility(View.INVISIBLE));
        }
        return vi;
    }
}
