package com.foolchen.lib.tracker.utils;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.foolchen.lib.tracker.lifecycle.TrackerFragmentLifeCycle;

/**
 * author : lyj
 * time   : 2018/08/22
 * desc   :
 * version: 1.0
 */
public class FragmentLifeUtil {
    private final static String TAG = "TraceUtil";

    /**
     * 当Fragment执行了onResume时触发
     *
     * @param fragment
     */
    public static void onFragmentResume(Fragment fragment) {
        Log.d(TAG, fragment.getClass().getSimpleName() + "---onResume---");
        TrackerFragmentLifeCycle.INSTANCE.onFragmentResumed(fragment);
    }

    /**
     * 当Fragment执行了onPause时触发
     *
     * @param fragment
     */
    public static void onFragmentPause(Fragment fragment) {
        Log.d(TAG, fragment.getClass().getSimpleName() + "---onPause---");
        TrackerFragmentLifeCycle.INSTANCE.onFragmentPaused(fragment);
    }


    /**
     * 当Fragment执行了onHiddenChanged时触发
     * 首页一个Activity承载多个Fragment Tab的情况，
     * 此时tab间切换并不会触发Fragment的OnResume/OnPause．
     * 触发的回调函数是onHiddenChanged(boolean hidden)
     *
     * @param fragment
     */
    public static void onFragmentHiddenChanged(Fragment fragment, boolean hidden) {
        Log.d(TAG, fragment.getClass().getSimpleName() + "---onHiddenChanged-----hidden = " + hidden);
        TrackerFragmentLifeCycle.INSTANCE.onFragmentVisibilityChanged(!hidden, fragment);
    }

    /**
     * 当Fragment执行了setUserVisibleHint时触发
     * 一个ViewPager承载多个页面的Fragment时
     * a.当第一个Fragment1显示时，虽然第二个Fragment2此时尚未显示，但是Fragment2的OnResume却以及执行，处于resumed的状态．
     * b.ViewPager页面切换OnResume/OnPause/onHiddenChanged均未触发，触发的回调是setUserVisibleHint
     * 此时判断Fragment　Show/Hide应该用setUserVisibleHint，而非OnResume/OnPause
     *
     * @param fragment
     */
    public static void onFragmentSetUserVisibleHint(Fragment fragment, boolean isVisibleToUser) {
        Log.d(TAG, fragment.getClass().getSimpleName() + "---onHiddenChanged-----isVisibleToUser = " + isVisibleToUser);
        TrackerFragmentLifeCycle.INSTANCE.onFragmentVisibilityChanged(isVisibleToUser, fragment);
    }

}
