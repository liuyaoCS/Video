package com.chinaso.video;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.chinaso.video.net.NetworkService;
import com.chinaso.video.net.recordlist.Record;
import com.chinaso.video.net.recordlist.RecordVideoList;

import org.easydarwin.config.Config;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VodListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView videoList;
    List<Record> videoLists;
    ListAdapter videoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_list);
        videoList= (ListView) findViewById(R.id.video_list);
        videoList.setOnItemClickListener(this);


        NetworkService.getInstance().listRecord(Config.DEFAULT_RECORD_NAME,"list",Config.DEFAULT_RECORD_BEGIN,Config.DEFAULT_RECORD_END, new Callback<RecordVideoList>() {
            @Override
            public void success(RecordVideoList recordVideoList, Response response) {
                Toast.makeText(VodListActivity.this,"get video list success",Toast.LENGTH_SHORT).show();
                videoLists=recordVideoList.getEasyDarwin().getBody().getRecords();
                videoAdapter=new VideoAdapter(VodListActivity.this,videoLists);

                videoList.setAdapter(videoAdapter);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(VodListActivity.this,"get video list err",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(VodListActivity.this,VodActivity.class);
        intent.putExtra("url",videoLists.get(position).getUrl());
        startActivity(intent);
    }
}
