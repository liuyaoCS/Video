package org.easydarwin.audio;

public class AudioCodec {
    public AudioCodec() {
    }

    public static native int create(int var0, int var1, int var2, int var3);

    public static native int decode(int var0, byte[] var1, int var2, int var3, byte[] var4, int[] var5);

    public static native void close(int var0);

    static {
        System.loadLibrary("AudioCodecer");
    }
}