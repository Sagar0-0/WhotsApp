package com.example.android.whotsapp.activities.dailog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.example.android.whotsapp.R;
import com.example.android.whotsapp.common.Common;
import com.example.android.whotsapp.model.ChatList;
import com.example.android.whotsapp.activities.chats.ChatsActivity;
import com.example.android.whotsapp.activities.display.ViewImageActivity;
import com.example.android.whotsapp.activities.profile.UserProfileActivity;

public class DialogViewUser {
    private Context context;
    public DialogViewUser(Context context, ChatList chatList){
        this.context=context;
        initialise(chatList);
    }
    public void initialise(ChatList chatList){
        Dialog dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.dialog_view_user);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width=WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height=WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        ImageButton btnChat,btncall,btnVideocall,btnInfo;
        ImageView profile;
        TextView userName;

        btnChat=dialog.findViewById(R.id.btn_chat);
        btncall=dialog.findViewById(R.id.btn_call);
        btnVideocall=dialog.findViewById(R.id.btn_video);
        btnInfo=dialog.findViewById(R.id.btn_info);
        profile=dialog.findViewById(R.id.image_profile);
        userName=dialog.findViewById(R.id.tv_username);

        userName.setText(chatList.getUserName());
        Glide.with(context).load(chatList.getUrlProfile()).into(profile);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userId",chatList.getUserId())
                        .putExtra("userName",chatList.getUserName())
                        .putExtra("userProfile",chatList.getUrlProfile()));
                dialog.dismiss();
            }
        });
        btncall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Calls Clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        btnVideocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Video call Clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UserProfileActivity.class)
                        .putExtra("userId", chatList.getUserId())
                        .putExtra("userProfile", chatList.getUrlProfile())
                        .putExtra("userName", chatList.getUserName()));
                dialog.dismiss();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.invalidate();
                Drawable dr=profile.getDrawable();
                Common.IMAGE_BITMAP=((BitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,profile,"image");
                Intent intent=new Intent(context, ViewImageActivity.class);
                context.startActivity(intent,activityOptionsCompat.toBundle());

            }
        });

        dialog.show();
    }
}
