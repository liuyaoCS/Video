package com.chinaso.video.net;

import org.easydarwin.config.Config;

import retrofit.RestAdapter;

public class NetworkService {
	static NetworkServiceAPI instance;

	static public NetworkServiceAPI getInstance(){
		if (instance != null)
			return instance;

		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint(Config.DEFAULT_VOD_SERVER_IP)
	    .build();
		instance = restAdapter.create(NetworkServiceAPI.class);
		
		return instance;
	}
}
