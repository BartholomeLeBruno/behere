package com.esgi.behere;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.NumberPicker;

public class ReservationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        NumberPicker np =  findViewById(R.id.numberPlace);

        np.setMinValue(1);

        np.setMaxValue(10);
    }
}
