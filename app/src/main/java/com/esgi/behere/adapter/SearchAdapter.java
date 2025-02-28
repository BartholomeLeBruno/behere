package com.esgi.behere.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.ui.NetworkImageView;
import com.esgi.behere.GroupActivity;
import com.esgi.behere.MarketProfilActivity;
import com.esgi.behere.ProfilFriendGroupActivity;
import com.esgi.behere.R;
import com.esgi.behere.actor.Market;
import com.esgi.behere.actor.ResultSearch;
import com.esgi.behere.utils.CacheContainer;

import java.util.List;

public class SearchAdapter extends BaseAdapter {

    private List<ResultSearch> data;
    private static LayoutInflater inflater = null;

    public SearchAdapter(Context context, List<ResultSearch> data) {
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
            vi = inflater.inflate(R.layout.fragment_result_search, null);
            TextView textpseudo = vi.findViewById(R.id.pseudoRes);
            TextView texttype = vi.findViewById(R.id.typeRes);
            textpseudo.setText(data.get(position).getName());
            NetworkImageView imageView = vi.findViewById(R.id.imgRes);
            texttype.setText(data.get(position).getType());
            if (!data.get(position).getPath().isEmpty())
                imageView.setImageUrl(data.get(position).getPath(), new ImageLoader(CacheContainer.getQueue()));
            else
                imageView.setBackground(vi.getContext().getDrawable(R.drawable.default_image));
            imageView.setImageUrl(data.get(position).getPath(),new ImageLoader(CacheContainer.getQueue()));
            vi.setOnClickListener(v -> {
                Intent nextStep;
                switch (data.get(position).getType()) {
                    case "User":
                        nextStep = new Intent(v.getContext(), ProfilFriendGroupActivity.class);
                        nextStep.putExtra("entityID", data.get(position).getId());
                        nextStep.putExtra("entityType", data.get(position).getType());
                        parent.getContext().startActivity(nextStep);
                        break;
                    case "Bar":
                        Market bar = CacheContainer.getInstance().getMarketHashMap().get(data.get(position).getName());
                        nextStep = new Intent(v.getContext(), MarketProfilActivity.class);
                        nextStep.putExtra("market", bar);
                        parent.getContext().startActivity(nextStep);
                        break;
                    case "Brewery":
                        Market brewery = CacheContainer.getInstance().getMarketHashMap().get(data.get(position).getName());
                        nextStep = new Intent(v.getContext(), MarketProfilActivity.class);
                        nextStep.putExtra("market", brewery);
                        parent.getContext().startActivity(nextStep);
                        break;
                    case "Group":
                        nextStep = new Intent(v.getContext(), GroupActivity.class);
                        nextStep.putExtra("entityID", data.get(position).getId());
                        nextStep.putExtra("group", "group");
                        parent.getContext().startActivity(nextStep);
                        break;
                }
            });

        return vi;
    }
}
