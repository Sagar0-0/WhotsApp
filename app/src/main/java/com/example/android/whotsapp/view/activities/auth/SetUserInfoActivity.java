package com.example.android.whotsapp.view.activities.auth;

import static com.example.android.whotsapp.view.activities.chats.ChatsActivity.TAG;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.android.whotsapp.BuildConfig;
import com.example.android.whotsapp.R;
import com.example.android.whotsapp.databinding.ActivitySetUserInfoBinding;
import com.example.android.whotsapp.view.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SetUserInfoActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 212;
    private static final int WRITE_EXTERNAL_STORAGE = 123;
    private static final int IMAGE_INTENT_CODE = 443;
    private final int IMAGE_GALLERY_REQUEST = 111;
    private ActivitySetUserInfoBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private BottomSheetDialog bsPickPhoto;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_user_info);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(firebaseUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            binding.edName.setText(task.getResult().getString("userName"));
                            Glide.with(SetUserInfoActivity.this).load(task.getResult().getString("imageProfile")).into(binding.imageProfile);
                            Toast.makeText(SetUserInfoActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


        progressDialog = new ProgressDialog(this);
        initButtonClick();
    }

    private void initButtonClick() {
        binding.btnNext.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.edName.getText().toString())) {
                Toast.makeText(this, "Please add your name", Toast.LENGTH_SHORT).show();
            } else {
                uploadToFirebase();
            }
        });
        binding.imageProfile.setOnClickListener(v -> {
            showBottomSheetPickPhoto();
        });
    }

    private void showBottomSheetPickPhoto() {
        View view = getLayoutInflater().inflate(R.layout.profile_pick_sheet, null);

        view.findViewById(R.id.ll_gallery).setOnClickListener(v -> {
            openGallery();
            bsPickPhoto.dismiss();
        });
        view.findViewById(R.id.ll_camera).setOnClickListener(v -> {
            checkCameraPermission();
            bsPickPhoto.dismiss();
        });

        bsPickPhoto = new BottomSheetDialog(this);
        bsPickPhoto.setContentView(view);

        bsPickPhoto.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        bsPickPhoto.setOnDismissListener(dialog -> bsPickPhoto = null);
        bsPickPhoto.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";
        try {
            File file = File.createTempFile("IMG_" + timeStamp, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("listPhotoName", imageFileName);
            startActivityForResult(intent, IMAGE_INTENT_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(SetUserInfoActivity.this).load(imageUri).into(binding.imageProfile);
//            uploadToFirebase();
        } else if (requestCode == IMAGE_INTENT_CODE
                && resultCode == RESULT_OK) {
            Glide.with(SetUserInfoActivity.this).load(imageUri).into(binding.imageProfile);
//            uploadToFirebase();
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_GALLERY_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadToFirebase() {
        if (imageUri != null) {
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("ImagesProfile/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
            riversRef.putFile(imageUri).addOnSuccessListener((taskSnapshot) -> {

                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                final String sdownload_url = String.valueOf(downloadUrl);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("imageProfile", sdownload_url);
                hashMap.put("userName", binding.edName.getText().toString());
                progressDialog.dismiss();
                firestore.collection("Users").
                        document(firebaseUser.getUid()).update(hashMap)
                        .addOnSuccessListener((aVoid) -> {
                            Toast.makeText(getApplicationContext(), "upload successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        });
            }).addOnFailureListener((e) -> {
                Toast.makeText(getApplicationContext(), "upload Failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        }

    }
}