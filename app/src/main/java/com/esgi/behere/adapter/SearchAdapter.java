package com.esgi.behere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.esgi.behere.R;
import com.esgi.behere.actor.ResultSearch;

import java.util.List;

public class SearchAdapter extends BaseAdapter {

    private Context context;
    private List<ResultSearch> data;
    private static LayoutInflater inflater = null;

    public SearchAdapter(Context context, List<ResultSearch> data) {
        this.context = context;
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
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.fragment_result_search, null);
        TextView textpseudo =  vi.findViewById(R.id.pseudoRes);
        TextView texttype =  vi.findViewById(R.id.typeRes);
        textpseudo.setText(data.get(position).getName());
        texttype.setText(data.get(position).getType());
        vi.setOnClickListener((View v) -> Toast.makeText(SearchAdapter.inflater.getContext(),"voila"+position,Toast.LENGTH_SHORT).show());
        return vi;
    }
}
