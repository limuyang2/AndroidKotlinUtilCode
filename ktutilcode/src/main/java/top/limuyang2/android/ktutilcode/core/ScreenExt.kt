package top.limuyang2.android.ktutilcode.core

import android.Manifest.permission.WRITE_SETTINGS
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresPermission
import android.util.DisplayMetrics
import android.view.WindowManager


/**
 * 获取屏幕宽度
 */
inline val Context.screenWidth: Int
    get() {
        val wm: WindowManager? = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        wm?.apply {
            val point = Point()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wm.defaultDisplay.getRealSize(point)
            } else {
                wm.defaultDisplay.getSize(point)
            }
            return point.x
        }
        return 0
    }

/**
 * 获取屏幕高度
 */
inline val Context.screenHeight: Int
    get() {
        val wm: WindowManager? = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        wm?.apply {
            val point = Point()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wm.defaultDisplay.getRealSize(point)
            } else {
                wm.defaultDisplay.getSize(point)
            }
            return point.y
        }
        return 0
    }

/**
 * 全屏
 */
inline var Activity.fullScreen: Boolean
    set(value) {
        if (value) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
    get() {
        val fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
        return window.attributes.flags and fullScreenFlag == fullScreenFlag
    }

/**
 * 是否横屏
 */
inline var Activity.landscape: Boolean
    set(value) {
        if (value) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
    get() {
        return this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

/**
 * 是否竖屏
 */
inline var Activity.portrait: Boolean
    set(value) {
        if (value) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }
    get() {
        return this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

/**
 * 判断是否锁屏
 */
inline val Context.isScreenLock: Boolean
    get() {
        val km = this.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        //noinspection ConstantConditions
        return km.inKeyguardRestrictedInputMode()
    }

/**
 * 进入休眠时长
 */
inline var Context.sleepDuration: Int
    @RequiresPermission(WRITE_SETTINGS)
    set(value) {
        Settings.System.putInt(
                this.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT,
                value
        )
    }
    get() {
        return try {
            Settings.System.getInt(
                    this.contentResolver,
                    Settings.System.SCREEN_OFF_TIMEOUT
            )
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            -123
        }
    }



/**
 * 截屏
 *
 * @param isDeleteStatusBar True to delete status bar, false otherwise.
 * @return the bitmap of screen
 */
fun Activity.screenShot(isDeleteStatusBar: Boolean = false): Bitmap? {
    val decorView = window.decorView
    decorView.isDrawingCacheEnabled = true
    decorView.setWillNotCacheDrawing(false)
    val bmp = decorView.drawingCache ?: return null
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    val ret = if (isDeleteStatusBar) {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = resources.getDimensionPixelSize(resourceId)
        Bitmap.createBitmap(
                bmp,
                0,
                statusBarHeight,
                dm.widthPixels,
                dm.heightPixels - statusBarHeight
        )
    } else {
        Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels)
    }
    decorView.destroyDrawingCache()
    return ret
}