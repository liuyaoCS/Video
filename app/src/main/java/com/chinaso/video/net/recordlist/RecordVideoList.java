
package com.chinaso.video.net.recordlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecordVideoList {

    @SerializedName("EasyDarwin")
    @Expose
    private com.chinaso.video.net.recordlist.EasyDarwin EasyDarwin;

    /**
     * 
     * @return
     *     The EasyDarwin
     */
    public com.chinaso.video.net.recordlist.EasyDarwin getEasyDarwin() {
        return EasyDarwin;
    }

    /**
     * 
     * @param EasyDarwin
     *     The EasyDarwin
     */
    public void setEasyDarwin(com.chinaso.video.net.recordlist.EasyDarwin EasyDarwin) {
        this.EasyDarwin = EasyDarwin;
    }

}
