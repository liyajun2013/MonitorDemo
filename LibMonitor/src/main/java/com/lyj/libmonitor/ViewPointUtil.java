package com.lyj.libmonitor;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        ViewGroup parentView = (ViewGroup) childView.getParent();
        while (parentView != null && !(childView instanceof ContentFrameLayout)) {
            int index = 0;
            if (parentView instanceof RecyclerView) {
                index = ((RecyclerView) parentView).getChildAdapterPosition(childView);
            } else if (parentView instanceof AdapterView) {
                index = ((AdapterView) parentView).getPositionForView(childView);
            } else if (parentView instanceof ViewPager) {
                index = ((ViewPager) parentView).getCurrentItem();
            } else {
                int childIndex = parentView.indexOfChild(childView);
                for (int i = 0; i < childIndex; i++) {
                    if (parentView.getChildAt(i).getClass().getSimpleName().equals(viewType)) {
                        index++;
                    }
                }
            }
            String fragmentValue = buildFragmentSegment(TraceUtil.sAliveFragMap, childView, "[" + index + "]");
            if (TextUtils.isEmpty(fragmentValue)) {
                StringBuilder element = new StringBuilder();
                element.append("/").append(viewType).append("[").append(index).append("]")
                        .append(getResourceId(context, childView.getId()));
                buffer.insert(0, element.toString());
            } else {
                buffer.insert(0, fragmentValue);
            }
            childView = parentView;
            viewType = childView.getClass().getSimpleName();
            parentView = (ViewGroup) childView.getParent();
        }
        return buffer.toString();
    }

    public static String getPath1(Context context, View childView) {
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
            String fragmentValue = buildFragmentSegment(TraceUtil.sAliveFragMap, viewparnet, "[" + index + "]");
            if (!TextUtils.isEmpty(fragmentValue)) {
                buffer.insert(0, fragmentValue);
            }
            buffer.insert(0, getResourceId(context, childView.getId()));
            buffer.insert(0, "[" + index + "]");
            buffer.insert(0, viewType);
            parentView = (ViewGroup) parentView.getParent();
            viewType = parentView.getClass().getSimpleName();
            childView = parentView;
            buffer.insert(0, "/");
        }
        while (parentView.getParent() != null && !(parentView.getParent() instanceof ContentFrameLayout));

        buffer.insert(0, getResourceId(context, childView.getId()));
        buffer.insert(0, viewType);
        return buffer.toString();
    }

    private static String buildFragmentSegment(HashMap<Integer, String> aliveFragments, View child, String validIndexSegment) {
        // deal with Fragment
        StringBuilder element = new StringBuilder();
        if (aliveFragments != null) {
            Iterator<Map.Entry<Integer, String>> iterator = aliveFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, String> entry = iterator.next();
                int viewCode = entry.getKey();
                String fragName = entry.getValue();
                if (viewCode == child.hashCode()) {
                    element.append("/")
                            .append(fragName)
                            .append(validIndexSegment);
                    break;
                }
            }
        }
        return element.toString();
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
