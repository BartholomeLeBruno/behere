package com.esgi.behere;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class LegalMentionActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_mention);
        webView = findViewById(R.id.webview);

        webView.loadUrl("https://lebrunobartholome.fr/legalMention.html");

    }
}
