package com.chinaso.video;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.chinaso.video.vod.VodNameListActivity;

import org.easydarwin.config.SettingActivity;
import org.easydarwin.easyplayer.PlaylistActivity;
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
                Intent intent2=new Intent(MainActivity.this, PlaylistActivity.class);
                startActivity(intent2);
                break;
            case R.id.vod:
                Intent intent3=new Intent(MainActivity.this, VodNameListActivity.class);
                startActivity(intent3);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.setting){
            startActivity(new Intent(this, SettingActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
