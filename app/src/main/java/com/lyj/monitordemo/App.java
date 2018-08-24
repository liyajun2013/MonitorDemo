package com.lyj.monitordemo;

import android.app.Application;

import com.foolchen.lib.tracker.Tracker;
import com.foolchen.lib.tracker.data.TrackerMode;
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
        Monitor.init(this);
        initTraker();
    }

    private void initTraker() {
        Tracker.INSTANCE.setChannelId("channelId");
        // 设定上报数据的项目名称
        Tracker.INSTANCE.setProjectName("test");
        // 设定上报数据的模式
        if (BuildConfig.DEBUG) {
            Tracker.INSTANCE.setMode(TrackerMode.DEBUG_TRACK);
        } else {
            Tracker.INSTANCE.setMode(TrackerMode.RELEASE);
        }
        Tracker.INSTANCE.setService("http://172.20.100.209:8165/", "11");
        // 初始化AndroidTracker
        Tracker.INSTANCE.initialize(this);
    }
}
