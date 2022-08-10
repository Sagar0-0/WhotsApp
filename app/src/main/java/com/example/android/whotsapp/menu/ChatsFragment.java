package com.example.android.whotsapp.menu;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android.whotsapp.R;
import com.example.android.whotsapp.adapter.ChatListAdapter;
import com.example.android.whotsapp.databinding.FragmentChatsBinding;
import com.example.android.whotsapp.model.ChatList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseFirestore firestore;
    private List<ChatList> list;
    private ArrayList<String> allUserId;
    private ChatListAdapter adapter;
    private Handler handler=new Handler()
;
    public ChatsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);

        list=new ArrayList<>();
        allUserId=new ArrayList<>();

        binding.chatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new ChatListAdapter(list,getContext());
        binding.chatsRecyclerView.setAdapter(adapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        firestore=FirebaseFirestore.getInstance();

        if(firebaseUser!=null) getChatList();

        return binding.getRoot();
    }

    private void getChatList() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        list.clear();
        allUserId.clear();
        reference.child("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                list.clear();
                allUserId.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    String userId= Objects.requireNonNull(snapshot.child("chatid").getValue()).toString();
                    allUserId.add(userId);
                }
                getUserInfo();
                binding.progressCircular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserInfo(){
        handler.post(() -> {
            for(String userId:allUserId){
                firestore.collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try{
                            ChatList chat=new ChatList(
                                    documentSnapshot.getString("userId"),
                                    documentSnapshot.getString("userName"),
                                    "Hey there! I am using WhotsApp!",
                                    "",
                                    documentSnapshot.getString("imageProfile")
                            );
                            list.add(chat);
                        }catch (Exception e){
                            Log.d(TAG, "onSuccess: ");
                        }
                        if(adapter!=null){
                            adapter.notifyItemInserted(0);
                            adapter.notifyDataSetChanged();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                });
            }
        });
    }
}