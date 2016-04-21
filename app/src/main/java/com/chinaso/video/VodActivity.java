package com.chinaso.video;


import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VodActivity extends AppCompatActivity {

    private VideoView video;
    MediaController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod);
        video=(VideoView)findViewById(R.id.video);

        String strPath = "http://192.168.74.31/video/test.m3u8";
        Uri uri = Uri.parse(strPath);
        controller=new MediaController(this);

        video.setVideoURI(uri);
        video.setMediaController(controller);
        //video.requestFocus();
        video.start();
    }
}
