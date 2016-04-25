
package com.chinaso.video.net.recordlist;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Body {

    @SerializedName("Records")
    @Expose
    private List<Record> Records = new ArrayList<Record>();

    /**
     * 
     * @return
     *     The Records
     */
    public List<Record> getRecords() {
        return Records;
    }

    /**
     * 
     * @param Records
     *     The Records
     */
    public void setRecords(List<Record> Records) {
        this.Records = Records;
    }

}
