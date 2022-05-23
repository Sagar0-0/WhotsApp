package com.example.android.whotsapp.view.profile;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.android.whotsapp.R;
import com.example.android.whotsapp.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private final int IMAGE_GALLERY_REQUEST = 111;
    private ActivityProfileBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private BottomSheetDialog bsPickPhoto,bsEditName;
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
    }

    private void showBottomSheetEditName() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name, null);

        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            bsEditName.dismiss();
        });
        final EditText editText=view.findViewById(R.id.ed_username);
        view.findViewById(R.id.btn_save).setOnClickListener(v -> {
            if(TextUtils.isEmpty(editText.getText().toString())){
                Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
            }else{
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
        firestore.collection("Users").document(firebaseUser.getUid()).update("userName",newName)
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
            Toast.makeText(this, "Can't you wait for it?", Toast.LENGTH_SHORT).show();
            bsPickPhoto.dismiss();
        });

        bsPickPhoto = new BottomSheetDialog(this);
        bsPickPhoto.setContentView(view);

        bsPickPhoto.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        bsPickPhoto.setOnDismissListener(dialog -> bsPickPhoto = null);
        bsPickPhoto.show();
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
        }
    }

    private void uploadToFirebase() {
        if (imageUri != null) {
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ImagesProfile/"
                    + System.currentTimeMillis() + "." + getFileExtension(imageUri));
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri downloadUri = urlTask.getResult();
                    final String sdownload_url = String.valueOf(downloadUri);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("imageProfile", sdownload_url);
                    progressDialog.dismiss();
                    firestore.collection("Users").document(firebaseUser.getUid()).update(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                                    getinfo();
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void getinfo() {
        firestore.collection("Users").document(firebaseUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = documentSnapshot.getString("userName");
                    String number = documentSnapshot.getString("userPhone");
                    String imageProfile = documentSnapshot.getString("imageProfile");

                    binding.tvUsername.setText(userName);
                    binding.tvPhone.setText(number);
                    Glide.with(ProfileActivity.this).load(imageProfile).into(binding.imageProfile);

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Get data", "onFailure: " + e.getMessage());
            }
        });
    }
}