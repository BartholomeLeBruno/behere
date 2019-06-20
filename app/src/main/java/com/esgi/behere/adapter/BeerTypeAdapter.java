package com.esgi.behere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.esgi.behere.R;
import com.esgi.behere.actor.BeerType;
import com.esgi.behere.register.RegisterSecondStep;

import java.util.List;

public class BeerTypeAdapter extends BaseAdapter {

    private Context context;
    private List<BeerType> data;
    private static LayoutInflater inflater = null;

    public BeerTypeAdapter(Context context, List<BeerType> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
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
        if (vi == null)
            vi = inflater.inflate(R.layout.fragment_beertype, null);
        TextView text =  vi.findViewById(R.id.beerTypeName);
        Button button = vi.findViewById(R.id.btnAddTypeOfBeer);
        text.setText(data.get(position).getName());

        button.setOnClickListener(v -> {
            if(button.getText().toString().equals("REMOVE")) {
                RegisterSecondStep.finallistBeerType.remove(data.get(position).getId());
                button.setText("ADD");
                return;
            }
            if(button.getText().toString().equals("ADD")) {
                RegisterSecondStep.finallistBeerType.add(data.get(position).getId());
                button.setText("REMOVE");
            }
        });
        return vi;
    }
}
