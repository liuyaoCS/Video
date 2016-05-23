package com.chinaso.video;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.easydarwin.config.Config;
import org.easydarwin.easyplayer.PlayActivity;
import org.esaydarwin.rtsp.player.R;
import org.json.JSONArray;
import org.json.JSONException;


public class VodNameListActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private RecyclerView mRecyclerView;
    private JSONArray mArray;
    private final String spKeyName="vodName_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_playlist);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name = sharedPreferences.getString(Config.RECORD_NAME, Config.DEFAULT_RECORD_NAME);


        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        try {
            mArray = new JSONArray(preferences.getString(spKeyName, "['"+ name +"']"));
        } catch (JSONException e) {
            e.printStackTrace();
            preferences.edit().putString(spKeyName, "["+ Config.DEFAULT_RECORD_NAME+"]").apply();
            mArray = new JSONArray();
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new PlayListViewHolder(getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                PlayListViewHolder plvh = (PlayListViewHolder) holder;
                plvh.mTextView.setText(mArray.optString(position));
            }

            @Override
            public int getItemCount() {
                return mArray.length();
            }
        });

    }


    @Override
    public boolean onLongClick(View view) {
        PlayListViewHolder holder = (PlayListViewHolder) view.getTag();
        final int pos = holder.getAdapterPosition();
        if (pos != -1) {

            new AlertDialog.Builder(this).setItems(new CharSequence[]{"修改", "删除"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        String vod_name = mArray.optString(pos);
                        final EditText edit = new EditText(VodNameListActivity.this);
                        final int hori = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
                        final int verti = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
                        edit.setPadding(hori, verti, hori, verti);
                        edit.setText(vod_name);
                        final AlertDialog alertDialog = new AlertDialog.Builder(VodNameListActivity.this).setView(edit).setTitle("请输入点播名称").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String vodName = String.valueOf(edit.getText());
                                if (TextUtils.isEmpty(vodName)) {
                                    return;
                                }
                                try {
                                    mArray.put(pos, vodName);

                                    final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                                    preferences.edit().putString(spKeyName, String.valueOf(mArray)).apply();
                                    mRecyclerView.getAdapter().notifyItemChanged(pos);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("取消", null).create();
                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });
                        alertDialog.show();
                    } else {
                        new AlertDialog.Builder(VodNameListActivity.this).setMessage("确定要删除该名称吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    //mArray.put(pos, null);
                                    mRecyclerView.getAdapter().notifyItemRemoved(pos);

                                    mArray=remove(mArray,pos);
                                    final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                                    preferences.edit().putString(spKeyName, String.valueOf(mArray)).apply();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                    }
                }
            }).show();
        }
        return true;
    }
    public JSONArray remove(JSONArray jsonArray,int index) throws JSONException{
        JSONArray mJsonArray = new JSONArray();
        if(index<0) return mJsonArray;
        if(index>jsonArray.length()) return mJsonArray;
        int j=0;
        for( int i=0;i< jsonArray.length();i++){
            if(i!=index){
                mJsonArray.put(j,jsonArray.getString(i));
                j++;
            }
        }
        return mJsonArray;
    }
    class PlayListViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;

        public PlayListViewHolder(View itemView) {
            super(itemView);
            itemView.setBackgroundResource(android.R.drawable.list_selector_background);
            mTextView = (TextView) itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(VodNameListActivity.this);
            itemView.setOnLongClickListener(VodNameListActivity.this);
            itemView.setTag(this);
        }

    }


    @Override
    public void onClick(View view) {
        PlayListViewHolder holder = (PlayListViewHolder) view.getTag();
        int pos = holder.getAdapterPosition();
        if (pos != -1) {
            String vodName = mArray.optString(pos);
            if (!TextUtils.isEmpty(vodName)) {
                Intent i = new Intent(VodNameListActivity.this, VodListActivity.class);
                i.putExtra("vod_name", vodName);
                startActivity(i);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_url, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_url) {
            final EditText edit = new EditText(this);
            final int hori = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
            final int verti = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
            edit.setPadding(hori, verti, hori, verti);
            edit.setText("vod");
            edit.setSelection("vod".length());
            final AlertDialog dlg = new AlertDialog.Builder(this).setView(edit).setTitle("请输入点播名称").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String mRTSPUrl = String.valueOf(edit.getText());
                    if (TextUtils.isEmpty(mRTSPUrl)) {
                        return;
                    }
                    mArray.put(mRTSPUrl);

                    final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                    preferences.edit().putString(spKeyName, String.valueOf(mArray)).apply();
                    mRecyclerView.getAdapter().notifyItemInserted(mArray.length() - 1);
                }
            }).setNegativeButton("取消", null).create();
            dlg.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
                }
            });
            dlg.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
