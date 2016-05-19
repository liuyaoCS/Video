package com.chinaso.video;


import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

import org.easydarwin.config.Config;

public class VodActivity extends AppCompatActivity {

    private VideoView video;
    private EditText urlDT;
    private View play;
    MediaController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod);

        urlDT= (EditText) findViewById(R.id.url);
        String url=getIntent().getStringExtra("url");
        if(!TextUtils.isEmpty(url)){
            String tmp="www.easydarwin.org//home/liuyao/video/Record";
            String ret=url.replace(tmp,Config.DEFAULT_SERVER_IP);
            urlDT.setText(ret);
        }else{
            //urlDT.setText(Config.DEFAULT_VOD_URL);
        }


        video=(VideoView)findViewById(R.id.video);
        play=findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(urlDT.getText().toString());
                controller=new MediaController(VodActivity.this);

                video.setVideoURI(uri);
                video.setMediaController(controller);
                //video.requestFocus();
                video.start();
            }
        });

    }
}
