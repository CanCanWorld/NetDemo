package com.zrq.netdemo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkConnect";

    private OnStateChange onStateChange;
    private static TelephonyManager tm;

    public void setOnWifiStateChange(OnStateChange onStateChange) {
        this.onStateChange = onStateChange;
    }


    private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, intent.getAction());
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 监听wifi的打开与关闭，与wifi的连接无关
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            onStateChange.wifiStateChange(wifiState);
        }
        // 监听wifi的连接状态即是否连上了一个有效无线路由
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                // 获取联网状态的NetWorkInfo对象
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                //获取的State对象则代表着连接成功与否等状态
                NetworkInfo.State state = networkInfo.getState();
                //判断网络是否已经连接
                boolean isConnected = state == NetworkInfo.State.CONNECTED;
                onStateChange.netConnectedListener(isConnected);
            }
        }
        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            //获取联网状态的NetworkInfo对象
            NetworkInfo info = intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        onStateChange.netConnectedType("网络状态：连上（wifi）");
                    } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        onStateChange.netConnectedType("网络状态：连上（数据流量）");
                    }
                } else {
                    onStateChange.netConnectedType("网络状态：断开");
                }
            }
        }

        if (ACTION_SIM_STATE_CHANGED.equals(intent.getAction())) {
            Log.e(TAG, "sim 卡状态改变");
            tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            int state = tm.getSimState();
//            switch (state) {
//                case TelephonyManager.SIM_STATE_READY:
//                    String operator = tm.getNetworkOperator();
//                    @SuppressLint("HardwareIds")
//                    String simNumber = tm.getSimSerialNumber();
//                    onStateChange.simStateChange(operator);
//                    onStateChange.simNumberChange(simNumber);
//                    break;
//                case TelephonyManager.SIM_STATE_CARD_IO_ERROR:
//                case TelephonyManager.SIM_STATE_CARD_RESTRICTED:
//                case TelephonyManager.SIM_STATE_NOT_READY:
//                case TelephonyManager.SIM_STATE_PERM_DISABLED:
//                case TelephonyManager.SIM_STATE_UNKNOWN:
//                case TelephonyManager.SIM_STATE_ABSENT:
//                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
//                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
//                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
//                    onStateChange.simStateChange("未知");
//                    onStateChange.simNumberChange("未知");
//                    break;
            if (state == TelephonyManager.SIM_STATE_READY) {
                String operator = tm.getNetworkOperator();
                @SuppressLint("HardwareIds")
                String simNumber = tm.getSimSerialNumber();
                onStateChange.simStateChange(operator);
                onStateChange.simNumberChange(simNumber);
            } else {
                Log.e(TAG, "sim 未知");
                onStateChange.simStateChange("未知");
                onStateChange.simNumberChange("未知");
            }
        }
    }

}
