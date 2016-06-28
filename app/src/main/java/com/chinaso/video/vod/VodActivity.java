package com.chinaso.video.vod;


import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.chinaso.video.R;

public class VodActivity extends AppCompatActivity {

    private VideoView video;
    private EditText urlDT;
    private View play;
    MediaController controller;
    private ProgressBar progressBar;
    private ProgressDialog mProgressDialog;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod);

        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(!video.isPlaying()){
                    handler.sendEmptyMessageDelayed(0,50);
                }else{
//                    if(mProgressDialog!=null && mProgressDialog.isShowing()){
//                        mProgressDialog.dismiss();
//                    }
                    progressBar.setVisibility(View.GONE);
                }
                return true;
            }
        });

        String url=getIntent().getStringExtra("url");
        urlDT= (EditText) findViewById(R.id.url);
        urlDT.setText(url);

        //urlDT.setText("http://192.168.74.145/test.avi");


        video=(VideoView)findViewById(R.id.video);
        progressBar= (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

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

                //mProgressDialog = ProgressDialog.show(VodActivity.this, null, "正在缓冲视频...");
                progressBar.setVisibility(View.VISIBLE);
                handler.sendEmptyMessage(0);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
//        if(mProgressDialog!=null && mProgressDialog.isShowing()){
//            mProgressDialog.dismiss();
//        }
        progressBar.setVisibility(View.GONE);
    }

}
