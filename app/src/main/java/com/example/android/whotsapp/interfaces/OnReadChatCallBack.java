package com.example.android.whotsapp.interfaces;

import com.example.android.whotsapp.model.Chats;

import java.util.List;

public interface OnReadChatCallBack {
    void OnReadSuccess(List<Chats> list);
    void OnReadFailed();
}
