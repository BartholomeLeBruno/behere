package com.esgi.behere.utils;

import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.esgi.behere.R;

import org.json.JSONObject;
import org.json.JSONTokener;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class PopupAchievement{

    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private SharedPreferences sharedPreferences;
    private static final String PREFS = "PREFS";
    private static final String PREFS_ACCESS_TOKEN = "ACCESS_TOKEN";
    private PopupWindow popupWindow;
    private View convertView, popupView;

    public void popupAuthentification(View view)
    {
        convertView = view;
        sharedPreferences = convertView.getContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) convertView.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_connection, null);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(convertView, Gravity.CENTER, 0, 0);
        Button btnSendComment = popupView.findViewById(R.id.btnAuthentificate);
        EditText edEmail =  popupView.findViewById(R.id.edLogin);
        EditText edPasword =  popupView.findViewById(R.id.edPassword);
        btnSendComment.setOnClickListener((View v) -> {
            prepareAuthentification();
            if(!edEmail.getText().toString().equals("") && !edPasword.getText().toString().equals(""))
            {
                mVolleyService = new ApiUsage(mResultCallback,  convertView.getContext());
                mVolleyService.authentificate(edEmail.getText().toString(), edPasword.getText().toString());
            }
        });
    }


    private void prepareAuthentification(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("user").toString()).nextValue();
                        sharedPreferences.edit().putString(PREFS_ACCESS_TOKEN,objres.getString("token")).apply();
                        popupWindow.dismiss();
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) {
                popupAuthentification(convertView);
                TextView testError =  popupView.findViewById(R.id.textError);
                testError.setVisibility(View.VISIBLE);

            }
        };
    }
}
