package com.foolchen.lib.tracker.utils;

import android.view.View;

import com.foolchen.lib.tracker.Tracker;

public class ViewEventUtil {
    public static void onClick(View view) {
        Tracker.INSTANCE.trackView(view);
    }

}
