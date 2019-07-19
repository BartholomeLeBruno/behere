package com.esgi.behere;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.error.VolleyError;
import com.esgi.behere.actor.Message;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.PopupAchievement;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;

import java.util.Objects;


public class Chat extends AppCompatActivity {
    private EditText messageArea;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private String messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        ImageView sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        long idResponder = Objects.requireNonNull(getIntent().getExtras()).getLong("reponsderID", 0);

        sendButton.setOnClickListener(v -> {
             messageText = messageArea.getText().toString();

            if (!messageText.equals("")) {
                Message message = new Message();
                message.setTextMessage(messageText);
                message.setUser_receiver_id(idResponder);
                prepareEmpty();
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.createMessage(message, sharedPreferences.getString(getString(R.string.access_token),""));
                messageArea.setText("");
            }
        });
    }

    private void prepareEmpty()
    {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        Toast.makeText(getApplicationContext(), String.format("Text : %s", messageText), Toast.LENGTH_LONG).show();

                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) {
                if(error.networkResponse.statusCode == 500)
                {
                    new PopupAchievement().popupAuthentification(getWindow().getDecorView().getRootView());
                }
            }
        };
    }
}