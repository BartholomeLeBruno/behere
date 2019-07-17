package com.esgi.behere.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.ui.NetworkImageView;
import com.esgi.behere.GroupActivity;
import com.esgi.behere.R;
import com.esgi.behere.actor.Group;

import java.util.List;

public class GroupAdapter extends BaseAdapter {

    private List<Group> data;
    private static LayoutInflater inflater = null;

    public GroupAdapter(Context context, List<Group> data) {
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
            vi = inflater.inflate(R.layout.fragment_group, null);
            TextView tvNameGroup = vi.findViewById(R.id.tvNameGroup);
            tvNameGroup.setText(data.get(position).getName());
            NetworkImageView imageView = vi.findViewById(R.id.groupImage);
            if (!data.get(position).getPath().isEmpty())
                //imageView.setImageUrl(data.get(position).getPath(), new ImageLoader(CacheContainer.getQueue()));
                imageView.setBackground(vi.getContext().getDrawable(R.drawable.default_image));
            else
                imageView.setBackground(vi.getContext().getDrawable(R.drawable.default_image));
            vi.setOnClickListener(v -> {
                Intent theGroup = new Intent(v.getContext(), GroupActivity.class);
                theGroup.putExtra("entityID", data.get(position).getId());
                theGroup.putExtra("group", "group");
                parent.getContext().startActivity(theGroup);
            });
        }
        return vi;
    }

}
