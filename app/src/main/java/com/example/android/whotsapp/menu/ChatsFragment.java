package com.example.android.whotsapp.menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.whotsapp.R;
import com.example.android.whotsapp.adapter.ChatListAdapter;
import com.example.android.whotsapp.model.ChatList;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private List<ChatList> list=new ArrayList<>();
    private RecyclerView recyclerView;
    public ChatsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView=view.findViewById(R.id.chats_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getChatList();
        return view;
    }

    private void getChatList() {
        if(list.size()==0){
            list.add(new ChatList("10","Robert","Some message here","25/04/2022","https://www.cheatsheet.com/wp-content/uploads/2020/11/Marvel-star-Robert-Downey-Jr.jpg"));
            list.add(new ChatList("11","Steve","Some message here","24/04/2022","https://www.cheatsheet.com/wp-content/uploads/2021/02/sebastian-stan-chris-evans.jpg"));
            list.add(new ChatList("12","Thor","Some message here","23/04/2022","https://upload.wikimedia.org/wikipedia/commons/e/e8/Chris_Hemsworth_by_Gage_Skidmore_2_%28cropped%29.jpg"));
            list.add(new ChatList("13","Quill","Some message here","22/04/2022","https://i.pinimg.com/originals/66/ac/7e/66ac7eb0de50093a5b3ec435dd1092cf.jpg"));
            list.add(new ChatList("14","Drax","Some message here","21/04/2022","https://wallpaperaccess.com/full/1227835.jpg"));
        }
        recyclerView.setAdapter(new ChatListAdapter(list,getContext()));
    }
}