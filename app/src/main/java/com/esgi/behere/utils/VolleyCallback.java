package com.esgi.behere.utils;

import com.android.volley.error.VolleyError;

import org.json.JSONObject;

public interface VolleyCallback {
    void onSuccess(JSONObject response);
    void onError(VolleyError error);
}
