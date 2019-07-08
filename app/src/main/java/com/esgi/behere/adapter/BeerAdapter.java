package com.esgi.behere.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.esgi.behere.R;
import com.esgi.behere.actor.Beer;

import java.util.List;

public class BeerAdapter extends BaseAdapter {

    private List<Beer> data;
    private static LayoutInflater inflater = null;

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
            vi = inflater.inflate(R.layout.fragment_beer, null);
            TextView text = vi.findViewById(R.id.tvNameBeer);
            text.setText(data.get(position).getName());
            vi.setOnClickListener(v -> onButtonShowPopupWindowClick(v,position, parent));
        }
        return vi;
    }

    private void onButtonShowPopupWindowClick(View view, int position, ViewGroup parent) {

        // inflate the layout of the popup window
        View popupView = inflater.inflate(R.layout.popup_beer, parent,false);
        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupView.setElevation(15);
        TextView tvName = popupView.findViewById(R.id.beerName);
        TextView tvDescription = popupView.findViewById(R.id.beerDescription);
        tvName.setText(String.format("%s Origine : %s", data.get(position).getName(), data.get(position).getOrigin()));
        tvDescription.setText(data.get(position).getDescription());
        //popupWindow.dismiss();
    }
}
