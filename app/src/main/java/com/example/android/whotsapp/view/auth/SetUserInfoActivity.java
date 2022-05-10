package com.example.android.whotsapp.view.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.android.whotsapp.R;
import com.example.android.whotsapp.databinding.ActivitySetUserInfoBinding;
import com.example.android.whotsapp.model.users.User;
import com.example.android.whotsapp.view.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SetUserInfoActivity extends AppCompatActivity {

    private ActivitySetUserInfoBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_set_user_info);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        progressDialog=new ProgressDialog(this);
        initButtonClick();
    }

    private void initButtonClick() {
        binding.btnNext.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.edName.getText().toString())){
                Toast.makeText(this, "Please add your name", Toast.LENGTH_SHORT).show();
            }else {
                doUpdate();
            }
        });
        binding.imageProfile.setOnClickListener(v-> {
            pickImage();
        });
    }

    private void pickImage() {
        Toast.makeText(this, "Feature not implemented yet!", Toast.LENGTH_SHORT).show();
    }

    private void doUpdate() {
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        if(firebaseUser !=null){
            User myUser=new User(
                    firebaseUser.getUid(),
                    binding.edName.getText().toString(),
                    firebaseUser.getPhoneNumber(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");
            firestore.collection("Users").document(firebaseUser.getUid()).set(myUser)
                    .addOnSuccessListener(unused -> {
                        progressDialog.dismiss();
                        Toast.makeText(SetUserInfoActivity.this, "Profile created ;)", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(SetUserInfoActivity.this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
                    });
        }else{
            Toast.makeText(this, "You need to login first", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
}