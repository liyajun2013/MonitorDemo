package com.lyj.libmonitor;

import android.app.Activity;
import android.util.Log;
import android.view.View;


public class TraceUtil {
    private final static String TAG = "TraceUtil";

    /**
     * 点击事件触发
     * @param view
     */
    public static void onClick(View view) {
        String activtyName = ViewPointUtil.getActivityName(view);
        String viewPath = ViewPointUtil.getPath(Monitor.context, view);
        Log.d(TAG, "activtyName--" + activtyName);
        Log.d(TAG, "viewPath--" + viewPath);
    }

    /**
     * 当Activity执行了onCreate时触发
     *
     * @param activity
     */
    public static void onActivityCreate(Activity activity) {

        Log.d(TAG, activity.getClass().getSimpleName() + "---onCreate---");
    }

    /**
     * 当Activity执行了onResume时触发
     *
     * @param activity
     */
    public static void onActivityResume(Activity activity) {
        Log.d(TAG, activity.getClass().getSimpleName() + "---onResume---");
    }
    /**
     * 当Activity执行了onPause时触发
     *
     * @param activity
     */
    public static void onActivityPause(Activity activity) {
        Log.d(TAG, activity.getClass().getSimpleName() + "---onPause---");
    }
    /**
     * 当Activity执行了onDestroy时触发
     *
     * @param activity
     */
    public static void onActivityDestroy(Activity activity) {
        Log.d(TAG, activity.getClass().getSimpleName() + "---onDestroy---");
    }
}
