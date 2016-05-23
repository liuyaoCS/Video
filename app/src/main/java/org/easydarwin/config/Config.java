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
    public static final String DEFAULT_SERVER_IP="192.168.74.90";
    public static final String DEFAULT_SERVER_PORT="554";
    public static final String DEFAULT_STREAM_ID="101";

    //live video
    public static final String DEFAULT_VIDEO_STREAM="rtsp://192.168.74.90:554/101.sdp";

    //vod video
    public static final String DEFAULT_VOD_SERVER_IP="http://192.168.74.90:10000/";
    public static final String RECORD_NAME="recordName";
    public static final String DEFAULT_RECORD_NAME = "vod1";
    public static final String DEFAULT_RECORD_BEGIN = "20160523104129";
    public static final String DEFAULT_RECORD_END = "20160918104532";

}
