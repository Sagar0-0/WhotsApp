package com.example.android.whotsapp.service;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.example.android.whotsapp.model.StatusModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseService {

    private Context context;

    public FirebaseService(Context context) {
        this.context = context;
    }

    public void uploadImageToFireBaseStorage(Uri imageUri, final OnCallBack onCallBack) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ImagesChats/"
                + System.currentTimeMillis() + "." + getFileExtension(imageUri));
        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!urlTask.isSuccessful()) ;
            Uri downloadUri = urlTask.getResult();
            final String sdownload_url = String.valueOf(downloadUri);

            onCallBack.onUploadSuccess(sdownload_url);
        }).addOnFailureListener(onCallBack::OnUploadFailed);

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public interface OnCallBack {
        void onUploadSuccess(String imageUri);

        void OnUploadFailed(Exception e);
    }

    public interface OnAddNewStatusCallBack {
        void onSuccess();
        void OnFailed();
    }

    public void addNewStatus(StatusModel statusModel,OnAddNewStatusCallBack onAddNewStatusCallBack){
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Status Daily").document(statusModel.getId()).set(statusModel)
                .addOnSuccessListener(unused -> onAddNewStatusCallBack.onSuccess())
        .addOnFailureListener(e -> onAddNewStatusCallBack.OnFailed());
    }
}
