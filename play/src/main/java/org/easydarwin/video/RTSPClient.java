package org.easydarwin.video;

import android.content.Context;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RTSPClient implements Closeable {
    public static final int EASY_SDK_VIDEO_FRAME_FLAG = 1;
    public static final int EASY_SDK_AUDIO_FRAME_FLAG = 2;
    public static final int EASY_SDK_EVENT_FRAME_FLAG = 4;
    public static final int EASY_SDK_RTP_FRAME_FLAG = 8;
    public static final int EASY_SDK_SDP_FRAME_FLAG = 16;
    public static final int EASY_SDK_MEDIA_INFO_FLAG = 32;
    public static final int TRANSTYPE_TCP = 1;
    public static final int TRANSTYPE_UDP = 2;
    private static final String TAG = RTSPClient.class.getSimpleName();
    private int mCtx;
    private final RTSPSourceCallBack mCallback;

    public RTSPClient(Context context, String key, RTSPSourceCallBack callBack) {
        if(key == null) {
            throw new NullPointerException();
        } else {
            this.mCtx = this.init(context, key);
            Log.i("ly","init result->"+mCtx);
            this.mCallback = callBack;

            if(this.mCtx == 0) {
                throw new IllegalArgumentException("初始化失败，KEY不合法！");
            }
        }
    }

    public static int getLastErrorCode() {
        return getErrorCode();
    }

    public int openStream(int channel, String url, int type, int mediaType, String user, String pwd) {
        return this.openStream(this.mCtx, channel, url, type, mediaType, user, pwd);
    }

    public void closeStream() {
        this.closeStream(this.mCtx);
    }

    private static native int getErrorCode();

    private native int init(Context var1, String var2);

    private native int deInit(int var1);

    private int openStream(int context, int channel, String url, int trans_type, int mediaType, String user, String pwd) {
        return this.openStream(context, channel, url, trans_type, mediaType, user, pwd, 0, 1000, 0);
    }

    private native int openStream(int var1, int var2, String var3, int var4, int var5, String var6, String var7, int var8, int var9, int var10);

    private native int closeStream(int var1);

    private void onRTSPSourceCallBack(int _channelId, int _channelPtr, int _frameType, byte[] pBuf, byte[] frameBuffer) {
        if(_frameType == 0) {
            this.mCallback.onRTSPSourceCallBack(_channelId, _channelPtr, _frameType, (FrameInfo)null);
        } else {
            ByteBuffer buffer = ByteBuffer.wrap(frameBuffer);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            FrameInfo fi = new FrameInfo();
            fi.codec = buffer.getInt();
            fi.type = buffer.getInt();
            fi.fps = buffer.get();
            buffer.get();
            fi.width = buffer.getShort();
            fi.height = buffer.getShort();
            buffer.getInt();
            buffer.getInt();
            buffer.getShort();
            fi.sample_rate = buffer.getInt();
            fi.channels = buffer.getInt();
            fi.length = buffer.getInt();
            fi.timestamp_usec = (long)buffer.getInt();
            fi.timestamp_sec = (long)buffer.getInt();
            long sec = fi.timestamp_sec < 0L?0L + fi.timestamp_sec:fi.timestamp_sec;
            long usec = fi.timestamp_usec < 0L?0L + fi.timestamp_usec:fi.timestamp_usec;
            fi.stamp = sec * 1000000L + usec;
            fi.buffer = pBuf;
            this.mCallback.onRTSPSourceCallBack(_channelId, _channelPtr, _frameType, fi);
        }
    }

    public void close() throws IOException {
        if(this.mCtx == 0) {
            throw new IOException("not opened or already closed");
        } else {
            this.deInit(this.mCtx);
            this.mCtx = 0;
        }
    }

    static {
        System.loadLibrary("EasyRTSPClient");
    }

    public interface RTSPSourceCallBack {
        void onRTSPSourceCallBack(int var1, int var2, int var3, FrameInfo var4);
    }

    public static final class FrameInfo {
        int codec;
        int type;
        byte fps;
        int width;
        int height;
        int sample_rate;
        int channels;
        int length;
        long timestamp_usec;
        long timestamp_sec;
        long stamp;
        float bitrate;
        float losspacket;
        byte[] buffer;
        boolean audio;

        public FrameInfo() {
        }
    }
}
