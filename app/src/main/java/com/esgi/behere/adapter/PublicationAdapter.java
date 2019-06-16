package com.esgi.behere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esgi.behere.R;
import com.esgi.behere.actor.Publication;

import java.util.List;


public class PublicationAdapter extends BaseAdapter {

    private Context context;
    private List<Publication> data;
    private static LayoutInflater inflater = null;

    public PublicationAdapter(Context context, List<Publication> data) {
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
            vi = inflater.inflate(R.layout.fragment_publication, null);
        ImageView imageView = vi.findViewById(R.id.imgPubPro);
        TextView text =  vi.findViewById(R.id.PublicationText);
        TextView textView = vi.findViewById(R.id.pseudoPubPro);
        imageView.setImageResource(R.drawable.beerwallpaper2);
        textView.setText(data.get(position).getPseudo());
        text.setText(data.get(position).getContent());
        text.setOnClickListener((View v) -> Toast.makeText(PublicationAdapter.inflater.getContext(),"voila"+position,Toast.LENGTH_SHORT).show());
        return vi;
    }

}
