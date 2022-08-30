package com.zrq.netdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.zrq.netdemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding mainBinding;
    private Context context;
    NetworkConnectChangedReceiver networkBroadcast;

    private TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        context = this;
        setContentView(mainBinding.getRoot());
        checkPermission();
        initData();
        getInfo();
    }

    private void initData() {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        networkBroadcast = new NetworkConnectChangedReceiver();
        registerReceiver(networkBroadcast, intentFilter);
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }
    }

    @SuppressLint({"HardwareIds", "SetTextI18n"})
    private void getInfo() {
//        mainBinding.tvPhone1.setText(getPhone(telephonyManager.getNetworkOperator()));
//        mainBinding.tvPhone2.setText("SIM卡序列号：" + telephonyManager.getSimSerialNumber());

        mainBinding.btn.setOnClickListener(v -> {
                    mainBinding.tvPhone1.setText(getPhone(telephonyManager.getNetworkOperator()));
                    mainBinding.tvPhone2.setText("SIM卡序列号：" + telephonyManager.getSimSerialNumber());
                }
        );

        networkBroadcast.setOnWifiStateChange(new OnStateChange() {
            @Override
            public void wifiStateChange(int wifiState) {
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        mainBinding.tvPhone3.setText("当前wifi启动状态：已关闭wifi");
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        mainBinding.tvPhone3.setText("当前wifi启动状态：正在关闭wifi");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        mainBinding.tvPhone3.setText("当前wifi启动状态：wifi正在打开");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        mainBinding.tvPhone3.setText("当前wifi启动状态：wifi已打开");
                        break;
                }
            }

            @Override
            public void netConnectedListener(boolean isConnected) {
                if (isConnected)
                    mainBinding.tvPhone4.setText("当前wifi连接状态：连接上wifi网络了");
                else
                    mainBinding.tvPhone4.setText("当前wifi连接状态：当前未连接上wifi网络");
            }

            @Override
            public void netConnectedType(String info) {
                mainBinding.tvPhone5.setText(info);
            }

            @Override
            public void simStateChange(String name) {
                mainBinding.tvPhone1.setText(getPhone(name == null ? "未知": name));
            }

            @Override
            public void simNumberChange(String number) {
                mainBinding.tvPhone2.setText(number == null ? "未知": number);
            }
        });

    }

    private String getPhone(String operator) {
        String str;
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007") || operator.equals("46004")
                    || operator.equals("46008")) {
                str = "中国移动";
            } else if (operator.equals("46001") || operator.equals("46006") || operator.equals("46009") || operator.equals("46010")) {
                str = "中国联通";
            } else if (operator.equals("46003") || operator.equals("46005") || operator.equals("46011")) {
                str = "中国电信";
            } else if (operator.equals("46020")) {
                str = "中国铁通";
            } else if (operator.equals("")) {
                str = "无sim卡";
            } else {
                str = "未知sim卡";
            }
        } else {
            str = "SIM卡错误";
        }
        return str;
    }


}