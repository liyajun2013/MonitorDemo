package com.foolchen.lib.tracker.utils

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.ContentFrameLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.data.*
import com.foolchen.lib.tracker.lifecycle.ITrackerHelper
import com.foolchen.lib.tracker.service.TrackerService
import com.google.gson.Gson
import com.google.gson.GsonBuilder

val PRETTY_GSON: Gson by lazy {
    GsonBuilder().setPrettyPrinting().create()
}
val GSON: Gson by lazy {
    Gson()
}

const val TAG = "AndroidTracker"

/**
 * 用于获取Activity的名字
 */
internal fun Activity.getTrackName(): String {
    var name: String? = null
    if (this is ITrackerHelper) {
        name = this.getTrackName(Tracker.trackContext.getApplicationContext())
    }
    if (name.isNullOrEmpty()) {
        name = this.javaClass.canonicalName
    }
    if (name.isNullOrEmpty()) {
        name = this.toString()
    }
    return name!!
}

/**
 * 用于获取Fragment的名字
 */
internal fun Fragment.getTrackName(): String {
    var name: String? = null
    if (this is ITrackerHelper) {
        name = this.getTrackName(Tracker.trackContext.getApplicationContext())
    }
    if (name.isNullOrEmpty()) {
        name = this.javaClass.canonicalName
    }
    if (name.isNullOrEmpty()) {
        name = this.toString()
    }
    return name!!
}

internal fun Activity?.getTrackTitle(): String = this?.title?.toString() ?: ""

internal fun Fragment.getTrackTitle(): String = activity?.getTrackTitle() ?: ""

/**
 * 获取Activity中需要的附加属性
 */
internal fun Activity.getTrackProperties(): Map<String, Any> {
    val properties = HashMap<String, Any>()
    if (this is ITrackerHelper) {
        this.getTrackProperties(Tracker.trackContext.getApplicationContext())?.let {
            it.filter { it.value != null }.forEach {
                properties[it.key] = it.value!!
            }
        }
    }
    return properties
}

/**
 * 获取Fragment中需要的附加属性
 */
internal fun Fragment.getTrackProperties(): Map<String, Any> {
    val properties = HashMap<String, Any>()
    if (this is ITrackerHelper) {
        this.getTrackProperties(Tracker.trackContext.getApplicationContext())?.let {
            it.filter { it.value != null }.forEach {
                properties[it.key] = it.value!!
            }
        }
    }
    return properties
}

@Suppress("UNUSED_PARAMETER")
internal fun View.getTrackProperties(): Map<String, Any> {
    // 首先获取元素本身的属性
    val properties = HashMap<String, Any>()
    properties[ELEMENT_TYPE] = this.javaClass.name
    if (this is TextView) {
        properties[ELEMENT_CONTENT] = this.text?.toString() ?: ""
    }
    var position = getChildPosition(this)
    //如果 position = -1 表示不为 RecyclerView 下布局
    if (position == -1) {
        properties[VIEW_ID] = getResourceId(this.context, this.id)
    } else {
        properties[VIEW_ID] = getResourceId(this.context, this.id) + "[" + position + "]"
    }
    /*ev?.let {
      properties.put(ELEMENT_X, ev.x)
      properties.put(ELEMENT_Y, ev.y)
    }*/

    // 然后获取开发者附加的属性
    val additionalProperties = Tracker.elementsProperties[this]
    additionalProperties?.filter { it.value != null }?.forEach {
        properties[it.key] = it.value!!
    }
    Tracker.elementsProperties.remove(this)
    return properties
}

/**
 * 是RecyclerView等下布局得到是第几个position的view
 * 不是RecyclerView等下布局返回-1
 */
fun getChildPosition(view: View): Int {
    var childView = view
    var parentView: ViewGroup? = childView.parent as ViewGroup
    var index = -1
    while (parentView != null && childView !is ContentFrameLayout) {
        if (parentView is RecyclerView) {
            index = parentView.getChildAdapterPosition(childView)
            break
        } else if (parentView is AdapterView<*>) {
            index = parentView.getPositionForView(childView)
            break
        } else if (parentView is ViewPager) {
            index = parentView.currentItem
            break
        } else {
            index = -1
        }
        childView = parentView
        parentView = childView.getParent() as ViewGroup
    }
    return index
}


/**
 * 获取到id的名称
 */
private fun getResourceId(context: Context, viewId: Int): String {
    var resourceName = ""
    try {
        resourceName = context.resources.getResourceEntryName(viewId)
        resourceName = resourceName
    } catch (e: Exception) {
    }

    return resourceName
}

/**
 * 对事件进行统计
 *
 * @param event 要统计的事件
 * @param background 当前事件是否为切换到后台
 * @param foreground 当前事件是否为切换到前台
 */
internal fun trackEvent(event: TrackerEvent, background: Boolean = false,
                        foreground: Boolean = false) {
    // 此处尝试对数据进行上报
    // 具体的上报策略由TrackerService掌控
    TrackerService.report(event, background, foreground)
    // 打印日志
    log(event)
}

internal fun log(event: TrackerEvent) {
    if (Tracker.mode == TrackerMode.DEBUG_ONLY) {
        log(event.toPrettyJson())
    } else if (Tracker.mode == TrackerMode.DEBUG_TRACK) {
        log(event.toPrettyJson())
    }
}

private fun log(s: String) {
    Log.d(TAG, s)
}
