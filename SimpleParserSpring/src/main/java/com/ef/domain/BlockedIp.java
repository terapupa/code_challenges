package com.ef.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public class BlockedIp implements Serializable {

    @EmbeddedId
    private BlockedIpId id;
    private String reason;

    public BlockedIpId getId() {
        return id;
    }

    public void setId(BlockedIpId id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
