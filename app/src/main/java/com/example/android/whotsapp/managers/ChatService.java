package com.example.android.whotsapp.managers;

import static com.example.android.whotsapp.view.activities.chats.ChatsActivity.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.android.whotsapp.interfaces.OnReadChatCallBack;
import com.example.android.whotsapp.model.chat.Chats;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatService {
    private Context context;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String receiverId;

    public ChatService(Context context, String receiverId) {
        this.context = context;
        this.receiverId = receiverId;
    }
    public ChatService(Context context) {
        this.context = context;
    }

    public void readChatData(final OnReadChatCallBack onCallBack) {
        reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                List<Chats> list = new ArrayList<>();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    Chats chats = snapshot.getValue(Chats.class);
                    assert chats != null;
                    try{
                        if (chats.getSender().equals(firebaseUser.getUid()) && chats.getReceiver().equals(receiverId)
                                || chats.getSender().equals(receiverId) && chats.getReceiver().equals(firebaseUser.getUid())) {
                            list.add(chats);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                onCallBack.OnReadSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onCallBack.OnReadFailed();
            }
        });
    }

    public void sendTextMsg(String text) {
        Chats chats = new Chats(
                getCurrentDate(),
                text,
                "",
                "TEXT",
                firebaseUser.getUid(),
                receiverId
        );

        reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverId);
        chatRef1.child("chatid").setValue(receiverId);

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverId).child(firebaseUser.getUid());
        chatRef2.child("chatid").setValue(firebaseUser.getUid());
    }

    public void sendImage(String imageUrl) {
        Chats chats = new Chats(
                getCurrentDate(),
                "",
                imageUrl,
                "IMAGE",
                firebaseUser.getUid(),
                receiverId
        );

        reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverId);
        chatRef1.child("chatid").setValue(receiverId);

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverId).child(firebaseUser.getUid());
        chatRef2.child("chatid").setValue(firebaseUser.getUid());
    }

    public String getCurrentDate() {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(date);
        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String currentTime = df.format(currentDateTime.getTime());
        return today + ", " + currentTime;
    }

    public void sendVoice(String audioPath) {
        final Uri uriAudio = Uri.fromFile(new File(audioPath));
        final StorageReference audioRef = FirebaseStorage.getInstance().getReference().child("Chats/Voice/" + System.currentTimeMillis());
        audioRef.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                String voiceUrl = String.valueOf(downloadUrl);
                Chats chats = new Chats(
                        getCurrentDate(),
                        "",
                        voiceUrl,
                        "VOICE",
                        firebaseUser.getUid(),
                        receiverId
                );

                reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: ");
                    }
                });

                DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverId);
                chatRef1.child("chatid").setValue(receiverId);

                DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverId).child(firebaseUser.getUid());
                chatRef2.child("chatid").setValue(firebaseUser.getUid());

            }
        });

    }
}
