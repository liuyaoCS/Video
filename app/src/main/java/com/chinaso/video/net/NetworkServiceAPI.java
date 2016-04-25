package com.chinaso.video.net;

import com.chinaso.video.net.recordlist.RecordVideoList;
import com.chinaso.video.net.recordoper.OperRecordResponse;


import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface NetworkServiceAPI {

	
	//start record
	@GET("/api/easyrecordmodule")
	void startRecord(@Query("name") String name, @Query("url") String url,Callback<OperRecordResponse> cb);

	//stop record
	@GET("/api/easyrecordmodule")
	void stopRecord(@Query("name") String name, @Query("cmd") String cmd,Callback<OperRecordResponse> cb);

	//list record
	@GET("/api/easyrecordmodule")
	void listRecord(@Query("name") String name, @Query("cmd") String cmd, @Query("begin") String begin, @Query("end") String end,Callback<RecordVideoList> cb);

}
