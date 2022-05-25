package com.example.android.whotsapp.view.display;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.android.whotsapp.R;
import com.example.android.whotsapp.common.Common;
import com.example.android.whotsapp.databinding.ActivityViewImageBinding;
import com.google.android.gms.common.util.DataUtils;

public class ViewImageActivity extends AppCompatActivity {

    private ActivityViewImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_view_image);
        binding.imageView.setImageBitmap(Common.IMAGE_BITMAP);
    }
}