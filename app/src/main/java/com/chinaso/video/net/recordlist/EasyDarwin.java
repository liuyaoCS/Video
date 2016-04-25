
package com.chinaso.video.net.recordlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class EasyDarwin {

    @SerializedName("Body")
    @Expose
    private com.chinaso.video.net.recordlist.Body Body;
    @SerializedName("Header")
    @Expose
    private com.chinaso.video.net.recordlist.Header Header;

    /**
     * 
     * @return
     *     The Body
     */
    public com.chinaso.video.net.recordlist.Body getBody() {
        return Body;
    }

    /**
     * 
     * @param Body
     *     The Body
     */
    public void setBody(com.chinaso.video.net.recordlist.Body Body) {
        this.Body = Body;
    }

    /**
     * 
     * @return
     *     The Header
     */
    public com.chinaso.video.net.recordlist.Header getHeader() {
        return Header;
    }

    /**
     * 
     * @param Header
     *     The Header
     */
    public void setHeader(com.chinaso.video.net.recordlist.Header Header) {
        this.Header = Header;
    }

}
