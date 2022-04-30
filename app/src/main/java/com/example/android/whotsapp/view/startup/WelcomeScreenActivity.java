package com.example.android.whotsapp.view.startup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.android.whotsapp.view.MainActivity;
import com.example.android.whotsapp.R;
import com.example.android.whotsapp.view.auth.PhoneLoginActivity;

public class WelcomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Button button=findViewById(R.id.btn_agree);
        button.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeScreenActivity.this, PhoneLoginActivity.class));
            finish();
        });
    }
}