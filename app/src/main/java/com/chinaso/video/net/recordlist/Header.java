
package com.chinaso.video.net.recordlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Header {

    @SerializedName("MessageType")
    @Expose
    private String MessageType;
    @SerializedName("Version")
    @Expose
    private String Version;

    /**
     * 
     * @return
     *     The MessageType
     */
    public String getMessageType() {
        return MessageType;
    }

    /**
     * 
     * @param MessageType
     *     The MessageType
     */
    public void setMessageType(String MessageType) {
        this.MessageType = MessageType;
    }

    /**
     * 
     * @return
     *     The Version
     */
    public String getVersion() {
        return Version;
    }

    /**
     * 
     * @param Version
     *     The Version
     */
    public void setVersion(String Version) {
        this.Version = Version;
    }

}
