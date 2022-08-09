package com.example.android.whotsapp.activities.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.android.whotsapp.BuildConfig;
import com.example.android.whotsapp.R;
import com.example.android.whotsapp.common.Common;
import com.example.android.whotsapp.databinding.ActivityProfileBinding;
import com.example.android.whotsapp.activities.display.ViewImageActivity;
import com.example.android.whotsapp.activities.startup.SplashScreen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 212;
    private static final int WRITE_EXTERNAL_STORAGE = 123;
    private static final int IMAGE_INTENT_CODE = 443;
    private final int IMAGE_GALLERY_REQUEST = 111;
    private ActivityProfileBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private BottomSheetDialog bsPickPhoto, bsEditName;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        if (firebaseUser != null) getinfo();

        initActionClick();
    }

    private void initActionClick() {
        binding.fabCamera.setOnClickListener(v -> showBottomSheetPickPhoto());
        binding.llEditName.setOnClickListener(v -> showBottomSheetEditName());
        binding.imageProfile.setOnClickListener(v -> {
            binding.imageProfile.invalidate();
            Drawable dr = binding.imageProfile.getDrawable();
            Common.IMAGE_BITMAP = ((BitmapDrawable) dr.getCurrent()).getBitmap();
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileActivity.this, binding.imageProfile, "image");
            Intent intent = new Intent(ProfileActivity.this, ViewImageActivity.class);
            startActivity(intent, activityOptionsCompat.toBundle());
        });
        binding.btnLogOut.setOnClickListener(v -> {
            showDialogSignOut();
        });
    }

    private void showDialogSignOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("DO you want to Sign out?");
        builder.setPositiveButton("Sign Out", (dialog, which) -> {
            dialog.cancel();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this, SplashScreen.class));
            finish();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showBottomSheetEditName() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name, null);

        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            bsEditName.dismiss();
        });
        final EditText editText = view.findViewById(R.id.ed_username);
        view.findViewById(R.id.btn_save).setOnClickListener(v -> {
            if (TextUtils.isEmpty(editText.getText().toString())) {
                Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                upadteName(editText.getText().toString());
                bsEditName.dismiss();
            }
        });

        bsEditName = new BottomSheetDialog(this);
        bsEditName.setContentView(view);

        bsEditName.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        bsEditName.setOnDismissListener(dialog -> bsEditName = null);
        bsEditName.show();
    }

    private void upadteName(String newName) {
        firestore.collection("Users").document(firebaseUser.getUid()).update("userName", newName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ProfileActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
                        getinfo();
                    }
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

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            imageUri = data.getData();
            uploadToFirebase();
        }else if (requestCode == IMAGE_INTENT_CODE
                && resultCode == RESULT_OK) {
            uploadToFirebase();
        }
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
                progressDialog.dismiss();
                firestore.collection("Users").
                        document(firebaseUser.getUid()).update(hashMap)
                        .addOnSuccessListener((aVoid) -> {
                            Toast.makeText(getApplicationContext(), "upload successfully", Toast.LENGTH_SHORT).show();
                        });
            }).addOnFailureListener((e) -> {
                Toast.makeText(getApplicationContext(), "upload Failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        }

    }

    private void getinfo() {
        firestore.collection("Users").document(firebaseUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = documentSnapshot.getString("userName");
                    String number = documentSnapshot.getString("userPhone");
                    String imageProfile = documentSnapshot.getString("imageProfile");

                    binding.tvUsername.setText(userName);
                    binding.tvPhone.setText(number);

                    assert imageProfile != null;
                    if (imageProfile.equals("")) {
                        binding.imageProfile.setImageResource(R.drawable.profile_placeholder);
                    } else {
                        Glide.with(ProfileActivity.this).load(imageProfile).into(binding.imageProfile);
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Get data", "onFailure: " + e.getMessage());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}