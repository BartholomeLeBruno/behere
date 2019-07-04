package com.esgi.behere.tools;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.esgi.behere.R;

public class StarTools {




    public StarTools(double number, Context context, LinearLayout linearLayout)
    {
        double note = roundToHalf(number);
        int numberofpassage  = 0;
        int diff;
        for ( diff = 0; diff < note ; diff++) {
            if(note - diff == 0.5) {
                linearLayout.addView(instanciateHalfStar(context));
                numberofpassage ++;

            }
            else {
                linearLayout.addView(instanciateFullStar(context));
                numberofpassage ++;
            }
        }
        for (int i = numberofpassage; i < 5; i++) {
            linearLayout.addView(instanciateEmptyStar(context));
        }

    }

    private static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }

    private ImageView instanciateFullStar(Context context)
    {
        ImageView star =  new ImageView(context);
        star.setImageResource(R.drawable.ic_star_yellow_24dp);
        return  star;
    }
    private ImageView instanciateHalfStar(Context context)
    {
        ImageView star =  new ImageView(context);
        star.setImageResource(R.drawable.ic_star_half_yellow_24dp);
        return  star;
    }
    private ImageView instanciateEmptyStar(Context context)
    {
        ImageView star =  new ImageView(context);
        star.setImageResource(R.drawable.ic_star_border_yellow_24dp);
        return  star;
    }

}
