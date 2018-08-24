package com.foolchen.lib.tracker.utils;

import android.app.Activity;
import android.util.Log;

import com.foolchen.lib.tracker.lifecycle.TrackerActivityLifeCycle;


public class ActivityLifeUtil {
    private final static String TAG = "TraceUtil";

    /**
     * 当Activity执行了onCreate时触发
     *
     * @param activity
     */
    public static void onActivityCreate(Activity activity) {
        Log.d(TAG, activity.getClass().getSimpleName() + "---onCreate---");
        TrackerActivityLifeCycle.INSTANCE.onActivityCreated(activity);
    }

    /**
     * 当Activity执行了onStart时触发
     *
     * @param activity
     */
    public static void onActivityStart(Activity activity) {
        Log.d(TAG, activity.getClass().getSimpleName() + "---onStart---");
        TrackerActivityLifeCycle.INSTANCE.onActivityStarted(activity);
    }

    /**
     * 当Activity执行了onResume时触发
     *
     * @param activity
     */
    public static void onActivityResume(Activity activity) {
        Log.d(TAG, activity.getClass().getSimpleName() + "---onResume---");
        TrackerActivityLifeCycle.INSTANCE.onActivityResumed(activity);
    }

    /**
     * 当Activity执行了onPause时触发
     *
     * @param activity
     */
    public static void onActivityPause(Activity activity) {
        Log.d(TAG, activity.getClass().getSimpleName() + "---onPause---");
        TrackerActivityLifeCycle.INSTANCE.onActivityPaused(activity);
    }

    /**
     * 当Activity执行了onStop时触发
     *
     * @param activity
     */
    public static void onActivityStop(Activity activity) {
        Log.d(TAG, activity.getClass().getSimpleName() + "---onStop---");
        TrackerActivityLifeCycle.INSTANCE.onActivityStopped(activity);
    }

    /**
     * 当Activity执行了onDestroy时触发
     *
     * @param activity
     */
    public static void onActivityDestroy(Activity activity) {
        Log.d(TAG, activity.getClass().getSimpleName() + "---onDestroy---");
        TrackerActivityLifeCycle.INSTANCE.onActivityDestroyed(activity);
    }


}
