package com.lyj.monitordemo;

import android.app.Application;

import com.lyj.libmonitor.Monitor;

/**
 * author : lyj
 * time   : 2018/07/18
 * desc   :
 * version: 1.0
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Monitor.init(getApplicationContext());
    }
}
