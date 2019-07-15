package com.esgi.behere;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class HowItWorksActivity extends AppCompatActivity {

    CarouselView carouselView;

    int[] sampleImages = {R.drawable.beherehome, R.drawable.beheresearch, R.drawable.beheremarket, R.drawable.beherego, R.drawable.behereprofile};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_it_works);

        carouselView = findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);

        ImageListener imageListener = (position, imageView) -> imageView.setImageResource(sampleImages[position]);

        carouselView.setImageListener(imageListener);

    }
}
