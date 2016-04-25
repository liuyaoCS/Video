
package com.chinaso.video.net.recordoper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EasyDarwin {

    @SerializedName("Header")
    @Expose
    private com.chinaso.video.net.recordoper.Header Header;

    /**
     * 
     * @return
     *     The Header
     */
    public com.chinaso.video.net.recordoper.Header getHeader() {
        return Header;
    }

    /**
     * 
     * @param Header
     *     The Header
     */
    public void setHeader(com.chinaso.video.net.recordoper.Header Header) {
        this.Header = Header;
    }

}
