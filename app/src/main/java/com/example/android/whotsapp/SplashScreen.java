package com.example.android.whotsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {

    ImageView background;
    LottieAnimationView lottieAnimationView;
    TextView from;
    TextView sagar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        background=findViewById(R.id.splash_background);
        lottieAnimationView=findViewById(R.id.lottie_animation);
        from=findViewById(R.id.from);
        sagar=findViewById(R.id.sagar);

        background.animate().translationY(-3000).setDuration(1200).setStartDelay(5000);
        lottieAnimationView.animate().translationY(1400).setDuration(1200).setStartDelay(5000);
        from.animate().translationY(1400).setDuration(1200).setStartDelay(5000);
        sagar.animate().translationY(1400).setDuration(1200).setStartDelay(5000);

        Handler handler=new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this,MainActivity.class));
            finish();
        },4000);
    }
}