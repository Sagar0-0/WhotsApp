package com.example.android.whotsapp.view.chats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.android.whotsapp.R;
import com.example.android.whotsapp.databinding.ActivityChatsBinding;

public class ChatsActivity extends AppCompatActivity {

    private ActivityChatsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_chats);

        Intent intent=getIntent();
        String userName=intent.getStringExtra("userName");
        String userId=intent.getStringExtra("userId");
        String userProfile=intent.getStringExtra("userProfile");
//        String userName=intent.getStringExtra("userName");

        if(userId!=null){
            binding.tvUsername.setText(userName);
            Glide.with(this).load(userProfile).into(binding.imageProfile);
        }

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}