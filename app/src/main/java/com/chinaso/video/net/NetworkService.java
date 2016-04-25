package com.chinaso.video.net;

import retrofit.RestAdapter;

public class NetworkService {
	static NetworkServiceAPI instance;

	static public NetworkServiceAPI getInstance(){
		if (instance != null)
			return instance;

		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint("http://192.168.74.73:10000/")
	    .build();
		instance = restAdapter.create(NetworkServiceAPI.class);
		
		return instance;
	}
}
