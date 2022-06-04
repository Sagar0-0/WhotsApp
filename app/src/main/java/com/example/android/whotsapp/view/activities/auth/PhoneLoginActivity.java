package com.example.android.whotsapp.view.activities.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.android.whotsapp.R;
import com.example.android.whotsapp.databinding.ActivityPhoneLoginBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity{

    private ActivityPhoneLoginBinding binding;
    private static final String TAG="PhoneLoginActivity";
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore fireStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_phone_login);

        //firebase
        mAuth=FirebaseAuth.getInstance();
        fireStore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        //button call
        progressDialog=new ProgressDialog(this);
        binding.btnNext.setOnClickListener(v -> {
            if(binding.btnNext.getText().toString().equals("Next")){
                progressDialog.show();
                String phone ="+"+binding.edCodeCountry.getText().toString()+ binding.edPhone.getText().toString();
                startPhoneVerification(phone);
            }else{
                if(binding.edCode.getText().toString().equals("")){
                    Toast.makeText(this, "Please enter your verification code first", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("Verifying..");
                    progressDialog.show();
                    verifyPhoneWithCode(mVerificationId,binding.edCode.getText().toString());
                }
            }
        });

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: Success");
                progressDialog.dismiss();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d(TAG, "onVerificationFailed: "+ e.getMessage());
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                Toast.makeText(PhoneLoginActivity.this, "Code sent!", Toast.LENGTH_LONG).show();
                binding.btnNext.setText("Continue");
                progressDialog.dismiss();
            }
        };
    }
    private void startPhoneVerification(String phoneNumber){
        progressDialog.setMessage("Sending SMS to "+phoneNumber);
        progressDialog.show();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneWithCode(String verificationId,String code){
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Log.d(TAG, "signInWithCredential:success");
                        Toast.makeText(PhoneLoginActivity.this, "Sign in Successful :)", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = task.getResult().getUser();
                        startActivity(new Intent(PhoneLoginActivity.this, SetUserInfoActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(PhoneLoginActivity.this, "Wrong Code :(", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "signInWithPhoneAuthCredential: Wrong input code");
                        }
                    }
                });
    }

}