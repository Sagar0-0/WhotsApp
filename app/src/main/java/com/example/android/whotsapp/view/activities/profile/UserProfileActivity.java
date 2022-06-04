package com.example.android.whotsapp.view.activities.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.android.whotsapp.R;
import com.example.android.whotsapp.databinding.ActivityProfileBinding;
import com.example.android.whotsapp.databinding.ActivityUserProfileBinding;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_user_profile);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String receiverId = intent.getStringExtra("userId");
        String userProfile = intent.getStringExtra("userProfile");

        if (receiverId != null) {
            binding.toolbar.setTitle(userName);
            if(userProfile==null || userProfile.equals("")){
                binding.imageProfile.setImageResource(R.drawable.profile_placeholder);
            }else{
                Glide.with(this).load(userProfile).into(binding.imageProfile);
            }
        }
    }
}