package top.limuyang2.android.ktutilcode.core

import android.Manifest.permission.WRITE_SETTINGS
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.PixelCopy
import android.view.WindowManager
import androidx.annotation.DimenRes
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission


//returns dip(dp) dimension value in pixels
fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()

//return sp dimension value in pixels
fun Context.sp(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()

fun Context.sp(value: Float): Int = (value * resources.displayMetrics.scaledDensity).toInt()

//converts px value into dip or sp
fun Context.px2dip(px: Int): Float = px.toFloat() / resources.displayMetrics.density

fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity

fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)

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
        return km.isKeyguardLocked
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
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)

    val bmp = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.RGB_565)
    val canvas = Canvas(bmp)
//    canvas.drawColor(Color.WHITE)
    window.decorView.draw(canvas)


    return if (isDeleteStatusBar) {
        val statusBarHeight = statusBarHeight
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
}

@RequiresApi(Build.VERSION_CODES.O)
fun Activity.screenShot(isDeleteStatusBar: Boolean = false, listener: (Int, Bitmap) -> Unit) {

    val rect = Rect()
    windowManager.defaultDisplay.getRectSize(rect)

    if (isDeleteStatusBar) {
        val statusBarHeight = this.statusBarHeight

        rect.set(rect.left, rect.top + statusBarHeight, rect.right, rect.bottom)
    }
    val bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888)

    PixelCopy.request(this.window, rect, bitmap, {
        listener(it, bitmap)
    }, Handler(this.mainLooper))
}