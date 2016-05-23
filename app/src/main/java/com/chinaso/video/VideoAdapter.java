package com.chinaso.video;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.chinaso.video.net.recordlist.Record;

import org.easydarwin.config.Config;

import java.util.List;

/**
 * Created by Administrator on 2016/4/25 0025.
 */
public class VideoAdapter extends BaseAdapter {
    List<Record> mDataSets;
    Context mContext;
    public VideoAdapter(Context context,List<Record> datas){
        mDataSets =datas;
        mContext=context;
    }
    @Override
    public int getCount() {
        return mDataSets.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.video_item_view,null,false);
            viewHolder=new ViewHolder();
            viewHolder.time=(TextView)convertView.findViewById(R.id.video_time);
            viewHolder.url=(TextView)convertView.findViewById(R.id.video_url);

            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.time.setText(mDataSets.get(position).getTime());
        String fetchUrl=mDataSets.get(position).getUrl();
        if(!TextUtils.isEmpty(fetchUrl)){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String ip = sharedPreferences.getString(Config.SERVER_IP, Config.DEFAULT_SERVER_IP);
            String tmp="www.easydarwin.org//home/liuyao/video/Record";
            final String ret=fetchUrl.replace(tmp, ip);
            viewHolder.url.setText(ret);
            viewHolder.url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext,VodActivity.class);
                    intent.putExtra("url",ret);
                    mContext.startActivity(intent);
                }
            });
        }


        return convertView;
    }
    class ViewHolder{
        TextView time;
        TextView url;
    }
}
