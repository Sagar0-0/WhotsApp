package com.example.android.whotsapp.activities.display;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.android.whotsapp.R;
import com.example.android.whotsapp.common.Common;
import com.example.android.whotsapp.databinding.ActivityViewImageBinding;

public class ViewImageActivity extends AppCompatActivity {

    private ActivityViewImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_view_image);
        binding.imageView.setImageBitmap(Common.IMAGE_BITMAP);
    }
}