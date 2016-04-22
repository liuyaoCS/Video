package org.easydarwin.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Process;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Surface;

import org.easydarwin.audio.AudioCodec;
import org.easydarwin.video.RTSPClient.FrameInfo;
import org.easydarwin.video.RTSPClient.RTSPSourceCallBack;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class EasyRTSPClient implements RTSPSourceCallBack {
    private static final long LEAST_FRAME_INTERVAL = 33333L;
    public static final int RESULT_VIDEO_DISPLAYED = 1;
    public static final int RESULT_VIDEO_SIZE = 2;
    public static final int RESULT_TIMEOUT = 3;
    private static final String TAG = EasyRTSPClient.class.getSimpleName();
    public static final String EXTRA_VIDEO_WIDTH = "extra-video-width";
    public static final String EXTRA_VIDEO_HEIGHT = "extra-video-height";
    private final String mKey;
    private Surface mSurface;
    private volatile Thread mThread;
    private volatile Thread mAudioThread;
    private final ResultReceiver mRR;
    private long[] mLastQueuedTimeUs = new long[4];
    private RTSPClient mClient;
    private PriorityBlockingQueue<FrameInfo> mQueue = new PriorityBlockingQueue(100, new Comparator<FrameInfo>() {
        public int compare(FrameInfo frameInfo, FrameInfo t1) {
            return (int)(frameInfo.stamp - t1.stamp);
        }
    });
    private final Context mContext;
    private long mNewestVideoTimeStample;
    private boolean mWaitingKeyFrame;
    private boolean mTimeout;

    public EasyRTSPClient(Context context, String key, Surface surface, ResultReceiver receiver) {
        this.mSurface = surface;
        this.mContext = context;
        this.mKey = key;
        this.mRR = receiver;
    }

    public int start(int channel, String url, int type, int mediaType, String user, String pwd) {
        this.mNewestVideoTimeStample = 0L;

        for(int i = 0; i < this.mLastQueuedTimeUs.length; ++i) {
            this.mLastQueuedTimeUs[i] = 0L;
        }

        this.mWaitingKeyFrame = true;
        this.mQueue.clear();
        this.startCodec();
        this.startAudio();
        this.mTimeout = false;
        this.mClient = new RTSPClient(this.mContext, this.mKey, this);
        return this.mClient.openStream(channel, url, type, mediaType, user, pwd);
    }

    private void startAudio() {
        this.mAudioThread = new Thread() {
            AudioTrack at = null;

            @TargetApi(16)
            public void run() {
                long handle = 0L;
                AudioManager am = (AudioManager)EasyRTSPClient.this.mContext.getSystemService(Context.AUDIO_SERVICE);
                OnAudioFocusChangeListener l = new OnAudioFocusChangeListener() {
                    public void onAudioFocusChange(int focusChange) {
                        AudioTrack audioTrack;
                        if(focusChange == 1) {
                            audioTrack = at;
                            if(audioTrack != null) {
                                audioTrack.setStereoVolume(1.0F, 1.0F);
                                if(audioTrack.getPlayState() == 2) {
                                    audioTrack.flush();
                                    audioTrack.play();
                                }
                            }
                        } else if(focusChange == -1) {
                            audioTrack = at;
                            if(audioTrack != null && audioTrack.getPlayState() == 3) {
                                audioTrack.pause();
                            }
                        } else if(focusChange == -3) {
                            audioTrack = at;
                            if(audioTrack != null) {
                                audioTrack.setStereoVolume(0.5F, 0.5F);
                            }
                        }

                    }
                };

                try {
                    FrameInfo frameInfo;
                    do {
                        frameInfo = (FrameInfo)EasyRTSPClient.this.mQueue.peek();
                        if(frameInfo == null || !frameInfo.audio) {
                            Thread.sleep(1L);
                            frameInfo = null;
                        }
                    } while(EasyRTSPClient.this.mAudioThread != null && frameInfo == null);

                    if(frameInfo != null) {
                        Thread ex = Thread.currentThread();
                        int requestCode = am.requestAudioFocus(l, 3, 1);
                        if(requestCode == 1) {
                            boolean i = false;
                            if(this.at == null) {
                                int mBufferReuse = frameInfo.sample_rate;
                                int outLen = frameInfo.channels == 1?4:12;
                                byte ms = 2;
                                int bfSize = AudioTrack.getMinBufferSize(mBufferReuse, outLen, ms);
                                this.at = new AudioTrack(3, mBufferReuse, outLen, ms, bfSize, 1);
                            }

                            this.at.play();
                            handle = (long)AudioCodec.create(frameInfo.codec, frameInfo.sample_rate, frameInfo.channels, 16);
                            byte[] mBufferReuse1 = new byte[16000];
                            int[] outLen1 = new int[1];

                            while(true) {
                                while(EasyRTSPClient.this.mAudioThread != null) {
                                    frameInfo = (FrameInfo)EasyRTSPClient.this.mQueue.peek();
                                    if(frameInfo != null && frameInfo.audio) {
                                        EasyRTSPClient.this.mQueue.remove();
                                        outLen1[0] = mBufferReuse1.length;
                                        long ms1 = SystemClock.currentThreadTimeMillis();
                                        int nRet = AudioCodec.decode((int)handle, frameInfo.buffer, 0, frameInfo.length, mBufferReuse1, outLen1);
                                        if(nRet == 0) {
                                            ms1 = SystemClock.currentThreadTimeMillis();
                                            nRet = this.at.write(mBufferReuse1, 0, outLen1[0]);
                                            if(nRet == -2 || nRet == -3) {
                                                return;
                                            }
                                        }
                                    } else {
                                        Thread.sleep(1L);
                                    }
                                }

                                return;
                            }
                        }

                        return;
                    }
                } catch (Exception var17) {
                    var17.printStackTrace();
                    return;
                } finally {
                    am.abandonAudioFocus(l);
                    if(handle != 0L) {
                        AudioCodec.close((int)handle);
                    }

                    if(this.at != null) {
                        this.at.release();
                        this.at = null;
                    }

                }

            }
        };
        this.mAudioThread.start();
    }

    private static void save2path(byte[] buffer, int offset, int length, String path, boolean append) {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(path, append);
            fos.write(buffer, offset, length);
        } catch (FileNotFoundException var17) {
            var17.printStackTrace();
        } catch (IOException var18) {
            var18.printStackTrace();
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException var16) {
                    var16.printStackTrace();
                }
            }

        }

    }

    public void stop() {
        Thread t = this.mThread;
        this.mThread = null;
        t.interrupt();

        try {
            t.join();
        } catch (InterruptedException var5) {
            var5.printStackTrace();
        }

        t = this.mAudioThread;
        this.mAudioThread = null;
        t.interrupt();

        try {
            t.join();
        } catch (InterruptedException var4) {
            var4.printStackTrace();
        }

        this.mClient.closeStream();

        try {
            this.mClient.close();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        this.mClient = null;
        this.mNewestVideoTimeStample = 0L;

        for(int i = 0; i < this.mLastQueuedTimeUs.length; ++i) {
            this.mLastQueuedTimeUs[i] = 0L;
        }

        this.mQueue.clear();
    }

    private static int getXPS(byte[] data, int offset, int length, byte[] dataOut, int[] outLen, int type) {
        int pos0 = -1;

        int i;
        for(i = offset; i < length - 4; ++i) {
            if(0 == data[i] && 0 == data[i + 1] && 1 == data[i + 2] && type == (15 & data[i + 3])) {
                pos0 = i;
                break;
            }
        }

        if(-1 == pos0) {
            return -1;
        } else {
            int pos1 = -1;

            for(i = pos0 + 4; i < length - 4; ++i) {
                if(0 == data[i] && 0 == data[i + 1] && 0 == data[i + 2]) {
                    pos1 = i;
                    break;
                }
            }

            if(-1 == pos1) {
                return -2;
            } else if(pos1 - pos0 + 1 > outLen[0]) {
                return -3;
            } else {
                dataOut[0] = 0;
                System.arraycopy(data, pos0, dataOut, 1, pos1 - pos0);
                outLen[0] = pos1 - pos0 + 1;
                return 0;
            }
        }
    }

    private void startCodec() {
        this.mThread = new Thread("VIDEO_CONSUMER") {
            @TargetApi(16)
            public void run() {
                Process.setThreadPriority(0);
                FrameInfo frameInfo = null;
                ByteBuffer mCSD0 = null;
                ByteBuffer mCSD1 = null;
                MediaCodec mCodec = null;

                while(true) {
                    try {
                        do {
                            frameInfo = (FrameInfo)EasyRTSPClient.this.mQueue.peek();
                            if(frameInfo != null && !frameInfo.audio) {
                                EasyRTSPClient.this.mQueue.remove();
                            } else {
                                Thread.sleep(1L);
                                frameInfo = null;
                            }
                        } while(EasyRTSPClient.this.mThread != null && frameInfo == null);

                        if(frameInfo != null) {
                            boolean e = true;
                            byte[] pBuf = frameInfo.buffer;
                            byte[] info = new byte[128];
                            int[] index = new int[]{128};
                            int firstFrame = EasyRTSPClient.getXPS(pBuf, 0, pBuf.length, info, index, 7);
                            ByteBuffer pBuf1;
                            if(firstFrame == 0) {
                                pBuf1 = ByteBuffer.allocate(index[0]);
                                pBuf1.put(info, 0, index[0]);
                                pBuf1.clear();
                                mCSD0 = pBuf1;
                            }

                            index[0] = 128;
                            firstFrame = EasyRTSPClient.getXPS(pBuf, 0, pBuf.length, info, index, 8);
                            if(firstFrame == 0) {
                                pBuf1 = ByteBuffer.allocate(index[0]);
                                pBuf1.put(info, 0, index[0]);
                                pBuf1.clear();
                                mCSD1 = pBuf1;
                            }

                            if(mCSD0 != null || mCSD1 != null) {
                                MediaFormat pBuf2 = new MediaFormat();
                                pBuf2.setInteger("width", frameInfo.width);
                                pBuf2.setInteger("height", frameInfo.height);
                                pBuf2.setInteger("push-blank-buffers-on-shutdown", e?1:0);
                                if(mCSD0 != null) {
                                    pBuf2.setByteBuffer("csd-0", mCSD0);
                                }

                                if(mCSD1 != null) {
                                    pBuf2.setByteBuffer("csd-1", mCSD1);
                                }

                                pBuf2.setString("mime", "video/avc");
                                MediaCodec buffer = MediaCodec.createDecoderByType("video/avc");
                                buffer.configure(pBuf2, EasyRTSPClient.this.mSurface, /*(MediaCrypto)*/null, 0);
                                buffer.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                                buffer.start();
                                mCodec = buffer;
                            }

                            if(mCodec == null) {
                                continue;
                            }

                            long e2 = 0L;
                            BufferInfo info1 = new BufferInfo();
                            boolean index1 = false;
                            boolean firstFrame1 = true;

                            label323:
                            while(true) {
                                while(true) {
                                    if(EasyRTSPClient.this.mThread == null) {
                                        break label323;
                                    }

                                    if(frameInfo == null) {
                                        frameInfo = (FrameInfo)EasyRTSPClient.this.mQueue.peek();
                                        if(frameInfo == null || frameInfo.audio) {
                                            Thread.sleep(10L);
                                            frameInfo = null;
                                            continue;
                                        }

                                        EasyRTSPClient.this.mQueue.remove();
                                    }

                                    byte[] pBuf3 = frameInfo.buffer;
                                    index1 = false;

                                    int index2;
                                    do {
                                        index2 = mCodec.dequeueInputBuffer(2000L);
                                        if(EasyRTSPClient.this.mThread == null) {
                                            break label323;
                                        }
                                    } while(index2 < 0);

                                    ByteBuffer buffer1 = mCodec.getInputBuffers()[index2];
                                    buffer1.clear();
                                    if(pBuf3.length > buffer1.remaining()) {
                                        mCodec.queueInputBuffer(index2, 0, 0, frameInfo.stamp, 0);
                                    } else {
                                        buffer1.put(pBuf3);
                                        mCodec.queueInputBuffer(index2, 0, buffer1.position(), frameInfo.stamp, 0);
                                    }

                                    index2 = mCodec.dequeueOutputBuffer(info1, 33333L);
                                    switch(index2) {
                                    default:
                                        mCodec.releaseOutputBuffer(index2, true);
                                        if(firstFrame1) {
                                            firstFrame1 = false;
                                            ResultReceiver audio_usec = EasyRTSPClient.this.mRR;
                                            if(audio_usec != null) {
                                                audio_usec.send(1, (Bundle)null);
                                            }
                                        }

                                        if(e2 == 0L) {
                                            e2 = info1.presentationTimeUs;
                                        } else {
                                            long audio_usec1 = EasyRTSPClient.this.mLastQueuedTimeUs[2];
                                            long frameInterval = info1.presentationTimeUs - e2;
                                            e2 = info1.presentationTimeUs;
                                            long ms;
                                            if(audio_usec1 != 0L) {
                                                ms = EasyRTSPClient.this.mLastQueuedTimeUs[0] - audio_usec1;
                                                long e1 = EasyRTSPClient.this.mLastQueuedTimeUs[1] - EasyRTSPClient.this.mLastQueuedTimeUs[3];
                                                long delay = ms - e1;
                                                long differ = frameInterval;
                                                Log.d(EasyRTSPClient.TAG, String.format("delay: %d  stamp differ :%d, time_differ:%d", new Object[]{Long.valueOf(delay), Long.valueOf(ms), Long.valueOf(e1)}));
                                                Log.d(EasyRTSPClient.TAG, String.format("video usec: %d, audio usec :%d", new Object[]{Long.valueOf(EasyRTSPClient.this.mLastQueuedTimeUs[0]), Long.valueOf(audio_usec1)}));
                                                Log.d(EasyRTSPClient.TAG, String.format("video timestample differ : %d", new Object[]{Long.valueOf(frameInterval)}));
                                                if(frameInterval > 0L) {
                                                    if(frameInterval > 500000L) {
                                                        differ = 500000L;
                                                    }

                                                    long timeIntervalInCache = EasyRTSPClient.this.mNewestVideoTimeStample - EasyRTSPClient.this.mLastQueuedTimeUs[0];
                                                    if(timeIntervalInCache < 0L) {
                                                        timeIntervalInCache = 0L;
                                                    }

                                                    long newDiffer = EasyRTSPClient.fixSleepTime(differ, delay, 0L);
                                                    long ms1 = PreferenceManager.getDefaultSharedPreferences(EasyRTSPClient.this.mContext).getBoolean("enable_syc", true)?newDiffer / 1000L:frameInterval / 20000L;
                                                    if(ms1 > 60L) {
                                                        ms1 = 60L;
                                                    }

                                                    ms1 = 1L;

                                                    try {
                                                        Thread.sleep(ms1);
                                                    } catch (InterruptedException var37) {
                                                        var37.printStackTrace();
                                                    }
                                                }
                                            } else {
                                                ms = frameInterval / 20000L;

                                                try {
                                                    if(ms > 60L) {
                                                        ms = 60L;
                                                    }

                                                    ms = 1L;
                                                    Thread.sleep(ms);
                                                } catch (InterruptedException var36) {
                                                    var36.printStackTrace();
                                                }
                                            }
                                        }
                                    case -3:
                                    case -2:
                                    case -1:
                                        frameInfo = null;
                                    }
                                }
                            }

                            mCodec.stop();
                            break;
                        }
                    } catch (Exception var38) {
                        var38.printStackTrace();
                        break;
                    } finally {
                        if(mCodec != null) {
                            mCodec.release();
                            mCodec = null;
                        }

                    }

                    return;
                }

            }
        };
        this.mThread.start();
    }

    private static final long fixSleepTime(long sleepTimeUs, long totalTimestampDifferUs, long delayUs) {
        double dValue = (double)(delayUs - totalTimestampDifferUs) / 1000000.0D;
        double radio = Math.exp(dValue);
        double r = (double)sleepTimeUs * radio + 0.5D;
        Log.d(TAG, String.format("fixSleepTime : %d.%d.%d result:%d", new Object[]{Long.valueOf(sleepTimeUs), Long.valueOf(totalTimestampDifferUs), Long.valueOf(delayUs), Long.valueOf((long)r)}));
        return (long)r;
    }

    @TargetApi(16)
    public void onRTSPSourceCallBack(int _channelId, int _channelPtr, int _frameType, FrameInfo frameInfo) {
        if(_frameType == 1) {
            if(frameInfo.width == 0 || frameInfo.height == 0) {
                return;
            }

            boolean rr = this.mNewestVideoTimeStample == 0L;
            this.mNewestVideoTimeStample = frameInfo.stamp;
            frameInfo.audio = false;
            this.mLastQueuedTimeUs[0] = frameInfo.stamp;
            this.mLastQueuedTimeUs[1] = System.nanoTime() / 1000L;
            if(rr) {
                ResultReceiver rr1 = this.mRR;
                Bundle bundle = new Bundle();
                bundle.putInt("extra-video-width", frameInfo.width);
                bundle.putInt("extra-video-height", frameInfo.height);
                Log.i(TAG, "width:" + frameInfo.width + ",height:" + frameInfo.height);
                if(rr1 != null) {
                    rr1.send(2, bundle);
                }
            }

            if(this.mWaitingKeyFrame) {
                if(frameInfo.buffer[4] != 103) {
                    return;
                }

                this.mWaitingKeyFrame = false;
            }

            this.mQueue.offer(frameInfo);
        } else if(_frameType == 2) {
            frameInfo.audio = true;
            if(this.mWaitingKeyFrame) {
                return;
            }

            this.mLastQueuedTimeUs[2] = frameInfo.stamp;
            this.mLastQueuedTimeUs[3] = System.nanoTime() / 1000L;
            Log.i(TAG, String.format("audio video time differ:%d", new Object[]{Long.valueOf(this.mLastQueuedTimeUs[2] - this.mLastQueuedTimeUs[0] - (this.mLastQueuedTimeUs[3] - this.mLastQueuedTimeUs[1]))}));
            this.mQueue.offer(frameInfo);
        } else if(_frameType == 0 && !this.mTimeout) {
            this.mTimeout = true;
            ResultReceiver rr2 = this.mRR;
            if(rr2 != null) {
                rr2.send(3, (Bundle)null);
            }
        }

    }
}