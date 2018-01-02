package com.ef.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Embeddable
public class BlockedIpId implements Serializable {

    private String ip;

    @Column(name = "request_number")
    private int requestNumber;

    @Column(name = "mesured_at", nullable = false, columnDefinition = "TIMESTAMP(3)")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mesuredAt;

    private String duration;
    private int threshold;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Date getMesuredAt() {
        return mesuredAt;
    }

    public void setMesuredAt(Date mesuredAt) {
        this.mesuredAt = mesuredAt;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
