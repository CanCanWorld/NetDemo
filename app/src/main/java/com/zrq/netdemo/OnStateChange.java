package com.zrq.netdemo;

public interface OnStateChange {
    void wifiStateChange(int wifiState);

    void netConnectedListener(boolean isConnected);

    void netConnectedType(String info);

    void simStateChange(String name);

    void simNumberChange(String number);
}
