package top.limuyang2.android.ktutilcode.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*

/**
 * @author: limuyang
 * @date: 2019/1/25
 * @Description:
 */

object ActivityManageUtil {

    private val ACTIVITY_LIFECYCLE = ActivityLifecycleImpl()

    private class ActivityLifecycleImpl : Application.ActivityLifecycleCallbacks {

        internal val mActivityList = LinkedList<Activity>()

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            setTopActivity(activity)
        }

        override fun onActivityStarted(activity: Activity?) {
            setTopActivity(activity)
        }

        override fun onActivityResumed(activity: Activity?) {
            setTopActivity(activity)
        }

        override fun onActivityPaused(activity: Activity?) {}

        override fun onActivityStopped(activity: Activity?) {}

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

        override fun onActivityDestroyed(activity: Activity?) {
            mActivityList.remove(activity)
        }

        private fun setTopActivity(activity: Activity?) {
            if (mActivityList.contains(activity)) {
                if (mActivityList.last != activity) {
                    mActivityList.remove(activity)
                    mActivityList.addLast(activity)
                }
            } else {
                mActivityList.addLast(activity)
            }
        }
    }

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE)
    }

    /**
     * 判断 Activity 是否存在栈中
     * @param activity Activity
     * @return Boolean
     */
    fun isActivityExistsInStack(activity: Activity): Boolean {
        val activities = ACTIVITY_LIFECYCLE.mActivityList
        for (aActivity in activities) {
            if (aActivity == activity) {
                return true
            }
        }
        return false
    }

    /**
     * 移除某一类型activity
     * @param clz Class<out Activity>
     */
    fun removeActivity(clz: Class<out Activity>) {
        for (activity in ACTIVITY_LIFECYCLE.mActivityList) {
            if (activity.javaClass == clz) {
                activity.finish()
            }
        }
    }

    /**
     * 移除某一个activity
     * @param activity Activity
     */
    fun removeActivity(activity: Activity) {
        activity.finish()
    }

    /**
     * 移除全部activity
     */
    fun removeAllActivitys() {
        val activityList = ACTIVITY_LIFECYCLE.mActivityList
        for (i in activityList.indices.reversed()) {// remove from top
            val activity = activityList[i]
            // sActivityList remove the index activity at onActivityDestroyed
            activity.finish()
        }
    }

    /**
     * 除指定的activity，其他都移除
     * @param clz 需要保留的activity
     */
    fun removeOtherActivities(clz: Class<out Activity>) {
        val activities = ACTIVITY_LIFECYCLE.mActivityList
        for (i in activities.indices.reversed()) {
            val activity = activities[i]
            if (activity.javaClass != clz) {
                activity.finish()
            }
        }
    }

    fun getTopActivity(): Activity? {
        return if (ACTIVITY_LIFECYCLE.mActivityList.isNotEmpty()) {
            ACTIVITY_LIFECYCLE.mActivityList.last
        } else {
            null
        }
    }
}