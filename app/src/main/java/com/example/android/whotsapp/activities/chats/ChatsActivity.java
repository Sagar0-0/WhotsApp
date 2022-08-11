package com.example.android.whotsapp.activities.chats;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.example.android.whotsapp.R;
import com.example.android.whotsapp.adapter.ChatsAdapter;
import com.example.android.whotsapp.databinding.ActivityChatsBinding;
import com.example.android.whotsapp.interfaces.OnReadChatCallBack;
import com.example.android.whotsapp.managers.ChatService;
import com.example.android.whotsapp.model.Chats;
import com.example.android.whotsapp.service.FirebaseService;
import com.example.android.whotsapp.activities.dailog.DialogReviewSendImage;
import com.example.android.whotsapp.activities.profile.UserProfileActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatsActivity extends AppCompatActivity {

    public static final String TAG = "ChatsActivity";
    private static final int PERMISSION_REQUEST_CODE = 332;
    private final int IMAGE_GALLERY_REQUEST = 111;
    private ActivityChatsBinding binding;
    private String receiverId;
    private ChatsAdapter adapter;
    private List<Chats> list = new ArrayList<>();
    private String userProfile, userName;
    private boolean actionsShown = false;
    private ChatService chatService;
    private Uri imageUri;
    private MediaRecorder mediaRecorder;
    private String audio_path;
    private String sTime;

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

        chatService = new ChatService(this, receiverId);

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
                    binding.btnSend.setVisibility(View.INVISIBLE);
                    binding.recordButton.setVisibility(View.VISIBLE);
                } else {
                    binding.btnSend.setVisibility(View.VISIBLE);
                    binding.recordButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerview.setLayoutManager(linearLayoutManager);
        adapter = new ChatsAdapter(list, this);
        binding.recyclerview.setAdapter(adapter);

        binding.recordButton.setRecordView(binding.recordView);
        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                binding.btnEmoji.setVisibility(View.INVISIBLE);
                binding.btnFile.setVisibility(View.INVISIBLE);
                binding.btnCamera.setVisibility(View.INVISIBLE);
                binding.edMessage.setVisibility(View.INVISIBLE);
                //start recording
                if (checkAudioPermission()) {
                    startRecord();
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(70);
                } else {
                    requestPermission();
                }
            }

            @Override
            public void onCancel() {
                try {
                    mediaRecorder.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                binding.btnFile.setVisibility(View.VISIBLE);
                binding.btnCamera.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                binding.btnEmoji.setVisibility(View.VISIBLE);
                binding.btnFile.setVisibility(View.VISIBLE);
                binding.btnCamera.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);

                try {
                    sTime = getHumanTimeText(recordTime);
                    stopRecord();
                    chatService.sendVoice(audio_path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLessThanSecond() {
                binding.btnEmoji.setVisibility(View.VISIBLE);
                binding.btnFile.setVisibility(View.VISIBLE);
                binding.btnCamera.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);
                stopRecord();
            }
        });
        binding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                binding.btnEmoji.setVisibility(View.VISIBLE);
            }
        });
    }

    private void stopRecord() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long recordTime) {
        return String.format("%02d",
                TimeUnit.MILLISECONDS.toSeconds(recordTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(recordTime)));
    }

    private void startRecord() {
        setUpMediaRecorder();
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Please restart your app", Toast.LENGTH_SHORT).show();
        }

    }

    private void setUpMediaRecorder() {
        String path_save = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "audio_record.m4a";
        audio_path = path_save;

        mediaRecorder = new MediaRecorder();
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(path_save);
        } catch (Exception e) {
            Log.d(TAG, "setUpMediaRecorder: " + e.getMessage());
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, PERMISSION_REQUEST_CODE);
    }

    private boolean checkAudioPermission() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }


    private void readChat() {
        chatService.readChatData(new OnReadChatCallBack() {
            @Override
            public void OnReadSuccess(List<Chats> list) {
//                adapter.setList(list);
                binding.recyclerview.setAdapter(new ChatsAdapter(list,ChatsActivity.this));
            }

            @Override
            public void OnReadFailed() {
                Log.d(TAG, "OnReadFailed: ");
            }
        });
    }

    private void initBtnClick() {
        binding.btnVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatsActivity.this, "Coming soon..", Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnVoiceCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatsActivity.this, "Coming soon..", Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatsActivity.this, "Coming soon..", Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnSend.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.edMessage.getText().toString())) {
                chatService.sendTextMsg(binding.edMessage.getText().toString());
                binding.edMessage.setText("");
            }
        });
        binding.llBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.llUserInfo.setOnClickListener(new View.OnClickListener() {
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
//                    binding.layoutActions.animate().
                }
            }
        });

        binding.btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
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
//            uploadToFirebase();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                reviewImage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void reviewImage(Bitmap bitmap) {
        new DialogReviewSendImage(ChatsActivity.this, bitmap).show(new DialogReviewSendImage.OnCallBack() {
            @Override
            public void OnButtonSendClick() {
                if (imageUri != null) {
                    final ProgressDialog progressDialog = new ProgressDialog(ChatsActivity.this);
                    progressDialog.setMessage("Sending..");
                    progressDialog.show();
                    binding.layoutActions.setVisibility(View.GONE);
                    actionsShown = false;

                    new FirebaseService(ChatsActivity.this).uploadImageToFireBaseStorage(imageUri, new FirebaseService.OnCallBack() {
                        @Override
                        public void onUploadSuccess(String imageUri) {
                            chatService.sendImage(imageUri);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void OnUploadFailed(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

}