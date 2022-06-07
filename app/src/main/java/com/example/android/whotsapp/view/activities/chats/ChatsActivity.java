package com.example.android.whotsapp.view.activities.chats;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.whotsapp.R;
import com.example.android.whotsapp.adapter.ChatsAdapter;
import com.example.android.whotsapp.databinding.ActivityChatsBinding;
import com.example.android.whotsapp.interfaces.OnReadChatCallBack;
import com.example.android.whotsapp.managers.ChatService;
import com.example.android.whotsapp.model.chat.Chats;
import com.example.android.whotsapp.view.activities.profile.UserProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    public static final String TAG = "ChatsActivity";
    private ActivityChatsBinding binding;
    private String receiverId;
    private ChatsAdapter adapter;
    private List<Chats> list=new ArrayList<>();
    private String userProfile, userName;
    private boolean actionsShown = false;
    private ChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chats);
        initialise();
        initBtnClick();
        readChat();
    }

    private void initialise() {
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        receiverId = intent.getStringExtra("userId");
        userProfile = intent.getStringExtra("userProfile");

        chatService=new ChatService(this,receiverId);

        if (receiverId != null) {
            binding.tvUsername.setText(userName);
            if (userProfile == null || userProfile.equals("")) {
                binding.imageProfile.setImageResource(R.drawable.profile_placeholder);
            } else {
                Glide.with(this).load(userProfile).into(binding.imageProfile);
            }
        }

        binding.edMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(binding.edMessage.getText().toString())) {
                    binding.btnSend.setImageDrawable(getDrawable(R.drawable.ic_baseline_keyboard_voice_24));
                } else {
                    binding.btnSend.setImageDrawable(getDrawable(R.drawable.ic_baseline_send_24));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerview.setLayoutManager(linearLayoutManager);
        adapter=new ChatsAdapter(list,this);
        binding.recyclerview.setAdapter(adapter);

    }

    private void readChat() {
        chatService.readChatData(new OnReadChatCallBack() {
            @Override
            public void OnReadSuccess(List<Chats> list) {
                adapter.setList(list);
            }

            @Override
            public void OnReadFailed() {
                Log.d(TAG, "OnReadFailed: ");
            }
        });
    }

    private void initBtnClick() {
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(binding.edMessage.getText().toString())) {
                    chatService.sendTextMsg(binding.edMessage.getText().toString());
                    binding.edMessage.setText("");
                }
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatsActivity.this, UserProfileActivity.class)
                        .putExtra("userId", receiverId)
                        .putExtra("userProfile", userProfile)
                        .putExtra("userName", userName));
            }
        });
        binding.btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionsShown) {
                    actionsShown = false;
                    binding.layoutActions.setVisibility(View.GONE);
                } else {
                    actionsShown = true;
                    binding.layoutActions.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}