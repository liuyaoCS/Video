package com.chinaso.video.vod;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.chinaso.video.R;
import com.chinaso.video.net.NetworkService;
import com.chinaso.video.net.recordlist.Body;
import com.chinaso.video.net.recordlist.Record;
import com.chinaso.video.net.recordlist.RecordVideoList;

import org.easydarwin.config.Config;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VodListActivity extends AppCompatActivity {
    ListView videoList;
    List<Record> videoLists;
    ListAdapter videoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_list);
        videoList= (ListView) findViewById(R.id.video_list);
        //videoList.setOnItemClickListener(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name = sharedPreferences.getString(Config.RECORD_NAME, Config.DEFAULT_RECORD_NAME);
        if(!TextUtils.isEmpty(getIntent().getStringExtra("vod_name"))){
            name=getIntent().getStringExtra("vod_name");
        }

        NetworkService.getInstance().listRecord(name,"list",Config.DEFAULT_RECORD_BEGIN,Config.DEFAULT_RECORD_END, new Callback<RecordVideoList>() {
            @Override
            public void success(RecordVideoList recordVideoList, Response response) {
                Body body=recordVideoList.getEasyDarwin().getBody();
                if(body!=null){
                    videoLists=body.getRecords();
                    videoAdapter=new VideoAdapter(VodListActivity.this,videoLists);

                    videoList.setAdapter(videoAdapter);
                    Toast.makeText(VodListActivity.this,"get video list success",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(VodListActivity.this," no video found",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(VodListActivity.this,"get video list err",Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent=new Intent(VodListActivity.this,VodActivity.class);
//        intent.putExtra("url",videoLists.get(position).getUrl());
//        startActivity(intent);
//    }
}
