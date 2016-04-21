package com.chinaso.video;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.easydarwin.easypusher.StreameActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button push,live,vod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init(){
        push= (Button) findViewById(R.id.push);
        live= (Button) findViewById(R.id.live);
        vod= (Button) findViewById(R.id.vod);
        push.setOnClickListener(this);
        live.setOnClickListener(this);
        vod.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.push:
                Intent intent=new Intent(MainActivity.this, StreameActivity.class);
                startActivity(intent);
                break;
            case R.id.live:
                Intent intent2=new Intent(MainActivity.this, LiveActivity.class);
                startActivity(intent2);
                break;
            case R.id.vod:
                Intent intent3=new Intent(MainActivity.this, VodActivity.class);
                startActivity(intent3);
                break;
            default:
                break;
        }
    }
}
