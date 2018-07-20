package com.lyj.monitor

class MonitorConfig {
    public static final ArrayList<String> sActivityMethod = new ArrayList<>()
    public static final String ACTIVITY_METHOD_ONCREATE = "onCreate"
    public static final String ACTIVITY_METHOD_ONRESUME = "onResume"
    public static final String ACTIVITY_METHOD_ONPAUSE = "onPause"
    public static final String ACTIVITY_METHOD_ONDESTROY = "onDestroy"
    static {
        sActivityMethod.add(ACTIVITY_METHOD_ONCREATE)
        sActivityMethod.add(ACTIVITY_METHOD_ONRESUME)
        sActivityMethod.add(ACTIVITY_METHOD_ONPAUSE)
        sActivityMethod.add(ACTIVITY_METHOD_ONDESTROY)
    }

}