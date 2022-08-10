package com.example.android.whotsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.whotsapp.R;
import com.example.android.whotsapp.model.Chats;
import com.example.android.whotsapp.tools.AudioService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private List<Chats> list;
    private FirebaseUser firebaseUser;
    private Context context;
    private ImageButton tmpBtnPlay;
    private AudioService audioService;

    public ChatsAdapter(List<Chats> list, Context context) {
        this.list = list;
        this.context = context;
        this.audioService=new AudioService(context);
    }

    public void setList(List<Chats> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textMessage;
        private LinearLayout layoutText, layoutVoice;
        private CardView layoutImage;
        private ImageView imageMessage;
        private ImageButton btnPlay;
        private ViewHolder tempHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutVoice = itemView.findViewById(R.id.layout_voice);
            textMessage = itemView.findViewById(R.id.tv_text_message);
            layoutImage = itemView.findViewById(R.id.layout_image);
            layoutText = itemView.findViewById(R.id.layout_text);
            imageMessage = itemView.findViewById(R.id.image_chat);
            btnPlay = itemView.findViewById(R.id.btn_play_chat);
        }

        void bind(Chats chats) {
            switch (chats.getType()) {
                case "TEXT":
                    layoutText.setVisibility(View.VISIBLE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.GONE);
                    textMessage.setText(chats.getTextMessage());
                    break;
                case "IMAGE":
                    layoutText.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.VISIBLE);
                    layoutVoice.setVisibility(View.GONE);
                    Glide.with(context).load(chats.getUrl()).into(imageMessage);
                    break;
                case "VOICE":
                    layoutText.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.VISIBLE);

                    layoutVoice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(tmpBtnPlay!=null){
                                tmpBtnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_24));
                            }

                            btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_pause_circle_24));
                            audioService.playAudioFromUrl(chats.getUrl(), new AudioService.OnPlayCallback() {
                                @Override
                                public void OnFinished() {
                                    btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_24));
                                }
                            });
                            tmpBtnPlay=btnPlay;
                        }
                    });
                    break;
            }
        }
    }
}
