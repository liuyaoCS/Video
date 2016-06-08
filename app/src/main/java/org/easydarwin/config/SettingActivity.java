/*
	Copyright (c) 2013-2016 EasyDarwin.ORG.  All rights reserved.
	Github: https://github.com/EasyDarwin
	WEChat: EasyDarwin
	Website: http://www.easydarwin.org
*/

package org.easydarwin.config;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.easydarwin.easypusher.R.layout.activity_setting);
        final EditText txtIp = (EditText) findViewById(org.easydarwin.easypusher.R.id.edt_server_address);
        final EditText txtPort = (EditText) findViewById(org.easydarwin.easypusher.R.id.edt_server_port);
        final EditText txtId = (EditText) findViewById(org.easydarwin.easypusher.R.id.edt_stream_id);
        final EditText txtName = (EditText) findViewById(org.easydarwin.easypusher.R.id.edt_record_name);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String ip = sharedPreferences.getString(Config.SERVER_IP, Config.DEFAULT_SERVER_IP);
        String port = sharedPreferences.getString(Config.SERVER_PORT, Config.DEFAULT_SERVER_PORT);
        String id = sharedPreferences.getString(Config.STREAM_ID, Config.DEFAULT_STREAM_ID);
        String name = sharedPreferences.getString(Config.RECORD_NAME, Config.DEFAULT_RECORD_NAME);

        txtIp.setText(ip);
        txtPort.setText(port);
        txtId.setText(id);
        txtName.setText(name);

        Button btnSave = (Button) findViewById(org.easydarwin.easypusher.R.id.btn_save);
        assert btnSave != null;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipValue = txtIp.getText().toString();
                String portValue = txtPort.getText().toString();
                String idValue = txtId.getText().toString();
                String nameValue = txtName.getText().toString();

                if (TextUtils.isEmpty(ipValue)) {
                    ipValue = Config.DEFAULT_SERVER_IP;
                }

                if (TextUtils.isEmpty(portValue)) {
                    portValue = Config.DEFAULT_SERVER_PORT;
                }

                if (TextUtils.isEmpty(idValue)) {
                    idValue = Config.DEFAULT_STREAM_ID;
                }

                if (TextUtils.isEmpty(nameValue)) {
                    nameValue = Config.DEFAULT_RECORD_NAME;
                }

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(SettingActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Config.SERVER_IP, ipValue);
                editor.putString(Config.SERVER_PORT, portValue);
                editor.putString(Config.STREAM_ID, idValue);
                editor.putString(Config.RECORD_NAME, nameValue);
                editor.putString(Config.VOD_SERVER_IP, "http://"+ipValue+":10000/");
                editor.commit();
                onBackPressed();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
