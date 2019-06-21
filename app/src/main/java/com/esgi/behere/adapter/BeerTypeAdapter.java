package com.esgi.behere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.esgi.behere.R;
import com.esgi.behere.actor.BeerType;
import com.esgi.behere.register.RegisterSecondStep;

import java.util.List;

public class BeerTypeAdapter extends BaseAdapter {

    private List<BeerType> data;
    private static LayoutInflater inflater = null;

    public BeerTypeAdapter(Context context, List<BeerType> data) {
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
        if (vi == null)
            vi = inflater.inflate(R.layout.fragment_beertype, parent,false);
        TextView text =  vi.findViewById(R.id.beerTypeName);
        Button button = vi.findViewById(R.id.btnAddTypeOfBeer);
        text.setText(data.get(position).getName());

        button.setOnClickListener(v -> {
            try {
                if (button.getText().toString().equals("REMOVE")) {
                    RegisterSecondStep.finallistBeerType.remove(position);
                    button.setText("ADD");
                    return;
                }
                if (button.getText().toString().equals("ADD")) {
                    RegisterSecondStep.finallistBeerType.add(data.get(position).getId());
                    button.setText("REMOVE");
                }
            }
            catch (Exception e)
            {
                Toast.makeText(parent.getContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
        return vi;
    }
}
