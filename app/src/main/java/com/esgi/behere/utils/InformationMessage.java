package com.esgi.behere.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esgi.behere.R;

public class InformationMessage extends Activity {

    private static ImageView funImage;
    private static TextView funText;
    private static LayoutInflater inflater;
    private static View viewFun;
    private static Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cust_toast_layout);
    }

    public static void createToastInformation(Activity activity, LayoutInflater getLayout, Context context, int drawable, String text)
    {
        inflater =  getLayout;
        viewFun = inflater.inflate(R.layout.cust_toast_layout, activity.findViewById(R.id.funRelativeLayout));
        funImage = viewFun.findViewById(R.id.imgPubPro);
        Drawable d = context.getDrawable(drawable);
        funImage.setBackground(d);
        funText = viewFun.findViewById(R.id.pseudoPubPro);
        funText.setText(text);
        toast = new Toast(context);
        toast.setView(viewFun);
        toast.show();
    }
}
