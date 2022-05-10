package com.example.android.whotsapp.menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.whotsapp.R;
import com.example.android.whotsapp.adapter.CallListAdapter;
import com.example.android.whotsapp.adapter.ChatListAdapter;
import com.example.android.whotsapp.model.CallList;
import com.example.android.whotsapp.model.ChatList;

import java.util.ArrayList;
import java.util.List;

public class CallsFragment extends Fragment {

    private List<CallList> list=new ArrayList<>();
    private RecyclerView recyclerView;
    public CallsFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_calls, container, false);
        recyclerView=view.findViewById(R.id.calls_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        getCallsList();
        return view;
    }

    private void getCallsList() {
        if(list.size()==0){
            list.add(new CallList("10","Robert","25/04/2022 , 9:20 pm","income","https://www.cheatsheet.com/wp-content/uploads/2020/11/Marvel-star-Robert-Downey-Jr.jpg"));
            list.add(new CallList("11","Steve","24/04/2022 , 9:20 pm","missed","https://www.cheatsheet.com/wp-content/uploads/2021/02/sebastian-stan-chris-evans.jpg"));
            list.add(new CallList("12","Thor","23/04/2022 , 9:20 pm","missed","https://upload.wikimedia.org/wikipedia/commons/e/e8/Chris_Hemsworth_by_Gage_Skidmore_2_%28cropped%29.jpg"));
            list.add(new CallList("13","Quill","22/04/2022 , 9:20 pm","out","https://i.pinimg.com/originals/66/ac/7e/66ac7eb0de50093a5b3ec435dd1092cf.jpg"));
            list.add(new CallList("14","Drax","21/04/2022 , 9:20 pm","income","https://wallpaperaccess.com/full/1227835.jpg"));
        }
        recyclerView.setAdapter(new CallListAdapter(list,getContext()));
    }
}