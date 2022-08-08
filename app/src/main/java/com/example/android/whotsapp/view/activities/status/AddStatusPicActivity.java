package com.example.android.whotsapp.view.activities.status;

import static com.example.android.whotsapp.view.activities.chats.ChatsActivity.TAG;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.android.whotsapp.R;
import com.example.android.whotsapp.databinding.ActivityAddStatusPicBinding;
import com.example.android.whotsapp.managers.ChatService;
import com.example.android.whotsapp.model.StatusModel;
import com.example.android.whotsapp.service.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.UUID;

public class AddStatusPicActivity extends AppCompatActivity {

    private Uri imageUri;
    private ActivityAddStatusPicBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_add_status_pic);
        imageUri=getIntent().getParcelableExtra("image");
        setInfo();
        initClick();
    }

    private void initClick() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSend.setOnClickListener(v -> new FirebaseService(AddStatusPicActivity.this).uploadImageToFireBaseStorage(imageUri, new FirebaseService.OnCallBack() {
            @Override
            public void onUploadSuccess(String imageUrl) {
                StatusModel status=new StatusModel();
                status.setId(UUID.randomUUID().toString());
                status.setCreatedDate(new ChatService(AddStatusPicActivity.this).getCurrentDate());
                status.setImageStatus(imageUrl);
                status.setUserID(FirebaseAuth.getInstance().getUid());
                status.setViewCount("0");
                status.setTextStatus(binding.edDescription.getText().toString());

                new FirebaseService(AddStatusPicActivity.this).addNewStatus(status, new FirebaseService.OnAddNewStatusCallBack() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(AddStatusPicActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void OnFailed() {
                        Toast.makeText(AddStatusPicActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void OnUploadFailed(Exception e) {
                Log.e(TAG, "OnUploadFailed: ",e );
            }
        }));
    }

    private void setInfo() {
        Glide.with(this).load(imageUri).into(binding.imageView);
    }
}