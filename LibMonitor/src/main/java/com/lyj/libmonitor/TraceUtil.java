package com.lyj.libmonitor;

import android.app.Activity;
import android.util.Log;
import android.view.View;


public class TraceUtil {
    private final static String TAG = "TraceUtil";

    /**
     * 点击事件触发
     *
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

    /**
     * 当Fragment执行了onResume时触发
     *
     * @param fragment
     */
    public static void onFragmentResume(Object fragment) {
        Log.d(TAG, fragment.getClass().getSimpleName() + "---onResume---");
    }

    /**
     * 当Fragment执行了onPause时触发
     *
     * @param fragment
     */
    public static void onFragmentPause(Object fragment) {
        Log.d(TAG, fragment.getClass().getSimpleName() + "---onPause---");
    }


    /**
     * 当Fragment执行了onHiddenChanged时触发
     * 首页一个Activity承载多个Fragment Tab的情况，
     * 此时tab间切换并不会触发Fragment的OnResume/OnPause．
     * 触发的回调函数是onHiddenChanged(boolean hidden)
     * @param fragment
     */
    public static void onFragmentHiddenChanged(Object fragment, boolean hidden) {
        Log.d(TAG, fragment.getClass().getSimpleName() + "---onHiddenChanged-----hidden = " + hidden);
    }

    /**
     * 当Fragment执行了setUserVisibleHint时触发
     *一个ViewPager承载多个页面的Fragment时　　　
     * a.当第一个Fragment1显示时，虽然第二个Fragment2此时尚未显示，但是Fragment2的OnResume却以及执行，处于resumed的状态．　　　　
     * b.ViewPager页面切换OnResume/OnPause/onHiddenChanged均未触发，触发的回调是setUserVisibleHint　　
     * 此时判断Fragment　Show/Hide应该用setUserVisibleHint，而非OnResume/OnPause
     * @param fragment
     */
    public static void onFragmentSetUserVisibleHint(Object fragment, boolean isVisibleToUser) {
        Log.d(TAG, fragment.getClass().getSimpleName() + "---onHiddenChanged-----isVisibleToUser = " + isVisibleToUser);
    }
}
