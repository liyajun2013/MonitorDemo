package com.lyj.libmonitor;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;

/**
 * author : lyj
 * time   : 2018/07/17
 * desc   :
 * version: 1.0
 */
public class ViewPointUtil {

    public static String getPath(Context context, View childView) {
        StringBuffer buffer = new StringBuffer();
        String viewType = childView.getClass().getSimpleName();
        View parentView = childView;
        int index = 0;
        do {
            int id = childView.getId();
            index = 0;
            ViewGroup viewparnet = ((ViewGroup) childView.getParent());
            int childIndex = viewparnet.indexOfChild(childView);
            for (int i = 0; i < childIndex; i++) {
                if (viewparnet.getChildAt(i).getClass().getSimpleName().equals(viewType)) {
                    index++;
                }
            }
            if (childView.getParent() instanceof RecyclerView) {
                index = ((RecyclerView) childView.getParent()).getChildAdapterPosition(childView);
            } else if (childView.getParent() instanceof AdapterView) {
                index = ((AdapterView) childView.getParent()).getPositionForView(childView);
            } else if (childView.getParent() instanceof ViewPager) {
                index = ((ViewPager) childView.getParent()).getCurrentItem();
            }
            buffer.insert(0, getResourceId(context, childView.getId()));
            buffer.insert(0, "]");
            buffer.insert(0, index);
            buffer.insert(0, "[");
            buffer.insert(0, viewType);
            parentView = (ViewGroup) parentView.getParent();
            viewType = parentView.getClass().getSimpleName();
            childView = parentView;
            buffer.insert(0, "/");
        } while (parentView.getParent() instanceof ContentFrameLayout);

        buffer.insert(0, getResourceId(context, childView.getId()));
        buffer.insert(0, viewType);
        return buffer.toString();
    }


    /**
     * 通过资源id来获取xml中设置的id名字
     */
    private static String getResourceId(Context context, int viewId) {
        String resourceName = "";
        try {
            resourceName = context.getResources().getResourceEntryName(viewId);
            resourceName = "#" + resourceName;
        } catch (Exception e) {
        }
        return resourceName;
    }

    /**
     * 从View中利用context获取所属Activity的名字
     */
    public static String getActivityName(View view) {
        Context context = view.getContext();
        if (context instanceof Activity) {
            //context本身是Activity的实例
            return context.getClass().getCanonicalName();
        } else if (context instanceof ContextWrapper) {
            //Activity有可能被系统＂装饰＂，看看context.base是不是Activity
            Activity activity = getActivityFromContextWrapper(context);
            if (activity != null) {
                return activity.getClass().getCanonicalName();
            } else {
                //如果从view.getContext()拿不到Activity的信息（比如view的context是Application）,则返回当前栈顶Activity的名字
                return getTopActivity(context);
            }
        }
        return "";
    }

    private static Activity getActivityFromContextWrapper(Context context) {
        context = ((ContextWrapper) context).getBaseContext();
        if (context instanceof Activity) {
            //context本身是Activity的实例
            return (Activity) context;
        } else {
            return null;
        }
    }

    /**
     * 获得栈中最顶层的Activity
     */
    private static String getTopActivity(Context context) {
        android.app.ActivityManager manager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null) {
            return (runningTaskInfos.get(0).topActivity).getShortClassName();
        } else {
            return null;
        }
    }
}
