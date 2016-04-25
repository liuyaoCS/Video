
package com.chinaso.video.net.recordoper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OperRecordResponse {

    @SerializedName("EasyDarwin")
    @Expose
    private com.chinaso.video.net.recordoper.EasyDarwin EasyDarwin;

    /**
     * 
     * @return
     *     The EasyDarwin
     */
    public com.chinaso.video.net.recordoper.EasyDarwin getEasyDarwin() {
        return EasyDarwin;
    }

    /**
     * 
     * @param EasyDarwin
     *     The EasyDarwin
     */
    public void setEasyDarwin(com.chinaso.video.net.recordoper.EasyDarwin EasyDarwin) {
        this.EasyDarwin = EasyDarwin;
    }

}
