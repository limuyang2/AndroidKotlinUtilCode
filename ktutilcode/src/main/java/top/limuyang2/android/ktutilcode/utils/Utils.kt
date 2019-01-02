package top.limuyang2.android.ktutilcode.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.v4.content.FileProvider
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * @name：Utils
 * @author lmy
 * @date：2018/10/8 9:55
 * @Deprecated
 */

val ACTIVITY_LIFECYCLE = ActivityLifecycleImpl()


private var mApplication: Application? = null
    set(value) {
        field = value ?: getApplicationByReflect()
        field?.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE)
    }
    get() {
        if (field == null) {
            mApplication = getApplicationByReflect()
        }
        return field
    }

fun Context?.utilsInit() {
    if (this == null) {
        getApplicationByReflect().utilsInit()
        return
    }
    (this.applicationContext as Application).utilsInit()
}

fun Application?.utilsInit() {
    mApplication = this
}

fun app() = mApplication!!

fun getActivityList() = ACTIVITY_LIFECYCLE.mActivityList

private fun getApplicationByReflect(): Application {
    try {
        @SuppressLint("PrivateApi")
        val activityThread = Class.forName("android.app.ActivityThread")
        val thread = activityThread.getMethod("currentActivityThread").invoke(null)
        val app = activityThread.getMethod("getApplication").invoke(thread)
                  ?: throw NullPointerException("u should init first")
        return app as Application
    } catch (e: NoSuchMethodException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }

    throw NullPointerException("u should init first")
}

class ActivityLifecycleImpl : Application.ActivityLifecycleCallbacks {

    val mActivityList = LinkedList<Activity>()
    private val mStatusListenerMap = HashMap<Any, OnAppStatusChangedListener?>()

    private var mForegroundCount = 0
    private var mConfigCount = 0

    var topActivity: Activity?
        get() {
            if (!mActivityList.isEmpty()) {
                val topActivity = mActivityList.last
                if (topActivity != null) {
                    return topActivity
                }
            }
            val topActivityByReflect = topActivityByReflect
            if (topActivityByReflect != null) {
                topActivity = topActivityByReflect
            }
            return topActivityByReflect
        }
        private set(activity) {
            //            if (activity.javaClass == PermissionUtils.PermissionActivity::class.java) return
            if (mActivityList.contains(activity)) {
                if (mActivityList.last != activity) {
                    mActivityList.remove(activity)
                    mActivityList.addLast(activity)
                }
            } else {
                mActivityList.addLast(activity)
            }
        }

    private val topActivityByReflect: Activity?
        get() {
            try {
                @SuppressLint("PrivateApi")
                val activityThreadClass = Class.forName("android.app.ActivityThread")
                val activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null)
                val activitiesField = activityThreadClass.getDeclaredField("mActivityList")
                activitiesField.isAccessible = true
                val activities = activitiesField.get(activityThread) as? Map<*, *> ?: return null
                for (activityRecord in activities.values) {
                    val activityRecordClass = activityRecord!!.javaClass
                    val pausedField = activityRecordClass.getDeclaredField("paused")
                    pausedField.isAccessible = true
                    if (!pausedField.getBoolean(activityRecord)) {
                        val activityField = activityRecordClass.getDeclaredField("activity")
                        activityField.isAccessible = true
                        return activityField.get(activityRecord) as Activity
                    }
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }

            return null
        }

    fun addListener(`object`: Any, listener: OnAppStatusChangedListener) {
        mStatusListenerMap[`object`] = listener
    }

    fun removeListener(`object`: Any) {
        mStatusListenerMap.remove(`object`)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {
        topActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        topActivity = activity
        if (mForegroundCount <= 0) {
            postStatus(true)
        }
        if (mConfigCount < 0) {
            ++mConfigCount
        } else {
            ++mForegroundCount
        }
    }

    override fun onActivityResumed(activity: Activity) {
        topActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {/**/
    }

    override fun onActivityStopped(activity: Activity) {
        if (activity.isChangingConfigurations) {
            --mConfigCount
        } else {
            --mForegroundCount
            if (mForegroundCount <= 0) {
                postStatus(false)
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {/**/
    }

    override fun onActivityDestroyed(activity: Activity) {
        mActivityList.remove(activity)
    }

    private fun postStatus(isForeground: Boolean) {
        if (mStatusListenerMap.isEmpty()) return
        for (onAppStatusChangedListener in mStatusListenerMap.values) {
            if (onAppStatusChangedListener == null) return
            if (isForeground) {
                onAppStatusChangedListener.onForeground()
            } else {
                onAppStatusChangedListener.onBackground()
            }
        }
    }
}

interface OnAppStatusChangedListener {
    fun onForeground()

    fun onBackground()
}

//typealias OnAppStatusChangedListener = (
//        onForeground: () -> Unit,
//        onBackground: () -> Unit
//) -> Unit
//
//typealias onForeground = () -> Unit