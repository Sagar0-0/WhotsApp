package com.example.android.whotsapp.view.startup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.airbnb.lottie.LottieAnimationView;
import com.example.android.whotsapp.R;
import com.example.android.whotsapp.databinding.ActivitySplashScreenBinding;
import com.example.android.whotsapp.view.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    private ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_splash_screen);
        binding.splashBackground.animate().translationY(-3000).setDuration(1200).setStartDelay(5000);
        binding.lottieAnimation.animate().translationY(1400).setDuration(1200).setStartDelay(5000);
        binding.from.animate().translationY(1400).setDuration(1200).setStartDelay(5000);
        binding.sagar.animate().translationY(1400).setDuration(1200).setStartDelay(5000);

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            Handler handler=new Handler();
            handler.postDelayed(() -> {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            },3800);
        }else{
            Handler handler=new Handler();
            handler.postDelayed(() -> {
                startActivity(new Intent(SplashScreen.this, WelcomeScreenActivity.class));
                finish();
            },3800);
        }


    }
}