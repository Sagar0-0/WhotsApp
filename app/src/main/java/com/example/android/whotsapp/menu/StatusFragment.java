package com.example.android.whotsapp.menu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.android.whotsapp.R;
import com.example.android.whotsapp.databinding.FragmentStatusBinding;
import com.example.android.whotsapp.activities.status.DisplayStatusActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class StatusFragment extends Fragment {
    public StatusFragment(){}

    private FragmentStatusBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_status, container, false);

        getProfile();
        binding.lnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getContext(), DisplayStatusActivity.class));
            }
        });
        return binding.getRoot();
    }

    private void getProfile() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection("Users").document(firebaseUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String imageProfile = documentSnapshot.getString("imageProfile");

                    assert imageProfile != null;
                    if(imageProfile.equals("")){
                        binding.imageProfile.setImageResource(R.drawable.profile_placeholder);
                    }else{
                        Glide.with(getContext()).load(imageProfile).into(binding.imageProfile);
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Get data", "onFailure: " + e.getMessage());
            }
        });
    }
}