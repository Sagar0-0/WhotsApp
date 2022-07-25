package com.example.android.whotsapp.tools;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

public class AudioService {
    private Context context;
    private MediaPlayer tmpMediaPlayer;

    public AudioService(Context context) {
        this.context = context;
    }
    public void playAudioFromUrl(String url,final OnPlayCallback onPlayCallback){
        if(tmpMediaPlayer!=null){
            tmpMediaPlayer.stop();
        }
        MediaPlayer mediaPlayer=new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();

            tmpMediaPlayer=mediaPlayer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                onPlayCallback.OnFinished();
            }
        });
    }
    public interface OnPlayCallback{
        void OnFinished();
    }
}
