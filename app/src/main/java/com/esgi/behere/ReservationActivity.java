package com.esgi.behere;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.esgi.behere.actor.Market;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.PopupAchievement;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Objects;

public class ReservationActivity extends AppCompatActivity {

    private Button btnDay, btnTime;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        btnDay = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnHour);
        Button btnConfirm = findViewById(R.id.btnConfirm);
        EditText np = findViewById(R.id.etNumberOfPlace);
        btnDay.setOnClickListener(v -> showDatePickerDialog());
        btnTime.setOnClickListener(v -> showTimePickerDialog());
        Market market = (Market) Objects.requireNonNull(getIntent().getExtras()).get("market");
        Log.d("voila", market.getDescription());
        btnConfirm.setOnClickListener(v -> {
            if (!btnDay.getText().toString().isEmpty() && !np.getText().toString().isEmpty() && !btnTime.getText().toString().isEmpty()) {
                String date = btnDay.getText().toString() + " " + btnTime.getText().toString();
                //Date date = formatterDate.parse(btnDay.getText().toString()+" "+btnTime.getText().toString());
                prepareEmpty(market);
                mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
                mVolleyService.addReservation(sharedPreferences.getLong(getString(R.string.prefs_id), 0), market.getId(), Integer.parseInt(np.getText().toString()), date, sharedPreferences.getString(getString(R.string.access_token), ""));
            }
        });
    }

    private void showDatePickerDialog() {
        // Get open DatePickerDialog button.

        // Create a new OnDateSetListener instance. This listener will be invoked when user click ok button in DatePickerDialog.

        DatePickerDialog.OnDateSetListener onDateSetListener = (datePicker, year, month, dayOfMonth) -> {
            String strBuf = "";
            if (month + 1 < 10) strBuf = year + "-0" + (month + 1) + "-" + dayOfMonth;
            else strBuf = year + "-" + (month + 1) + "-" + dayOfMonth;
            btnDay.setText(strBuf);
        };

        // Get current year, month and day.
        Calendar now = Calendar.getInstance();
        int year = now.get(java.util.Calendar.YEAR);
        int month = now.get(java.util.Calendar.MONTH);
        int day = now.get(java.util.Calendar.DAY_OF_MONTH);

        // Create the new DatePickerDialog instance.
        DatePickerDialog datePickerDialog = new DatePickerDialog(ReservationActivity.this, onDateSetListener, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        // Set dialog icon and title.
        datePickerDialog.setIcon(R.drawable.ic_settings_applications_blue_24dp);
        datePickerDialog.setTitle("Please select date.");

        // Popup the dialog.
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        // Get open TimePickerDialog button.
        // Create a new OnTimeSetListener instance. This listener will be invoked when user click ok button in TimePickerDialog.
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, hour, minute) -> {
            String strBuf = "";
            if (minute < 10) strBuf = hour + ":" + "0" + minute;
            else strBuf = hour + ":" + minute;
            btnTime.setText(strBuf);

        };

        Calendar now = Calendar.getInstance();
        int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = now.get(java.util.Calendar.MINUTE);

        // Whether show time in 24 hour format or not.
        boolean is24Hour = true;

        TimePickerDialog timePickerDialog = new TimePickerDialog(ReservationActivity.this, onTimeSetListener, hour, minute, is24Hour);

        timePickerDialog.setIcon(R.drawable.ic_settings_applications_blue_24dp);
        timePickerDialog.setTitle("Please select time.");

        timePickerDialog.show();
    }

    private void prepareEmpty(Market market) {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        Toast.makeText(getApplicationContext(), "Your reservation is processing !", Toast.LENGTH_LONG).show();
                        Intent back = new Intent(getApplicationContext(), MarketProfilActivity.class);
                        back.putExtra("market", market);
                        startActivity(back);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(getWindow().getDecorView().getRootView());
                }
            }
        };
    }
}
