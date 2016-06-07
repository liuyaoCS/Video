/*
	Copyright (c) 2013-2016 EasyDarwin.ORG.  All rights reserved.
	Github: https://github.com/EasyDarwin
	WEChat: EasyDarwin
	Website: http://www.easydarwin.org
*/

package org.easydarwin.config;

/**
 * 类Config的实现描述：//TODO 类实现描述
 *
 * @author HELONG 2016/3/7 19:02
 */
public class Config {
    //push
    public static final String SERVER_IP="serverIp";
    public static final String SERVER_PORT="serverPort";
    public static final String STREAM_ID="streamId";
    public static final String DEFAULT_SERVER_IP="120.25.237.210";
    public static final String DEFAULT_SERVER_PORT="554";
    public static final String DEFAULT_STREAM_ID="101";

    //live video
    public static final String DEFAULT_VIDEO_STREAM="rtsp://"+DEFAULT_SERVER_IP+":"+DEFAULT_SERVER_PORT+"/"+DEFAULT_STREAM_ID+".sdp";

    //vod video
    public static final String VOD_SERVER_IP="vodServerIp";
    public static final String DEFAULT_VOD_SERVER_IP="http://"+DEFAULT_SERVER_IP+":10000/";

    public static final String RECORD_NAME="recordName";
    public static final String DEFAULT_RECORD_NAME = "vod1";

    public static final String DEFAULT_RECORD_BEGIN = "20160523104129";
    public static final String DEFAULT_RECORD_END = "20160918104532";

    public static final String REPLACE_LOCAL="www.easydarwin.org//home/liuyao/video/Record";
    public static final String REPLACE_REMOTE="www.easydarwin.org//home/video/Record";

}
