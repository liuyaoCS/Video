package com.chinaso.video.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.easydarwin.config.Config;

import retrofit.RestAdapter;

public class NetworkService {
	static NetworkServiceAPI instance;

	static public NetworkServiceAPI getInstance(){
		if (instance != null)
			return instance;

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String vodIp = sharedPreferences.getString(Config.VOD_SERVER_IP, Config.DEFAULT_VOD_SERVER_IP);
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint(/*Config.*/vodIp)
	    .build();
		instance = restAdapter.create(NetworkServiceAPI.class);
		
		return instance;
	}
	static public void reConfigInstance(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String vodIp = sharedPreferences.getString(Config.VOD_SERVER_IP, Config.DEFAULT_VOD_SERVER_IP);
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setEndpoint(vodIp)
				.build();
		instance = restAdapter.create(NetworkServiceAPI.class);
	}
	private static Context mContext;
	public static void init(Context context){
		mContext=context;
	}
}
