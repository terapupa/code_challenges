package com.ef;

public class RequestItem {
    private String ipAddress;
    private String dateTimeStr;

    public RequestItem(String ipAddress, String dateTimeStr) {
        this.ipAddress = ipAddress;
        this.dateTimeStr = dateTimeStr;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDateTimeStr() {
        return dateTimeStr;
    }

    public void setDateTimeStr(String dateTimeStr) {
        this.dateTimeStr = dateTimeStr;
    }

}
