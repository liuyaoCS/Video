package com.chinaso.video;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;

public class LiveActivity extends AppCompatActivity {
    EditText url;
    Button play;
    VideoView liveVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        url= (EditText) findViewById(R.id.url);
        play= (Button) findViewById(R.id.play);
        liveVideo= (VideoView) findViewById(R.id.live_video);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path=url.getEditableText().toString();
                //path="rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp";
                PlayRtspStream(path);
            }
        });
    }
    private void PlayRtspStream(String rtspUrl){
        liveVideo.setVideoURI(Uri.parse(rtspUrl));
        liveVideo.requestFocus();
        liveVideo.start();
    }
}
