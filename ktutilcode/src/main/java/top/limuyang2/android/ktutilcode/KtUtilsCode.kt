package top.limuyang2.android.ktutilcode

import android.annotation.SuppressLint
import android.app.Application
import java.lang.reflect.InvocationTargetException

object KtUtilsCode {

    private lateinit var application: Application

    fun init(application: Application) {
        this.application = application
    }

    val app: Application
        get() {
            return if (::application.isInitialized) {
                application
            } else {
                getApplicationByReflect()
            }
        }

    private fun getApplicationByReflect(): Application {
        try {
            @SuppressLint("PrivateApi")
            val activityThread = Class.forName("android.app.ActivityThread")
            val thread = activityThread.getMethod("currentActivityThread").invoke(null)
            val app = activityThread.getMethod("getApplication").invoke(thread)
                    ?: throw NullPointerException("u should init 'KT Utils Code' first")
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

        throw NullPointerException("u should init 'KT Utils Code' first")
    }
}

