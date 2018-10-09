package top.limuyang2.android.ktutilcode.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * @name：ScreenUtils
 * @author lmy
 * @date：2018/10/9 9:21
 * @Deprecated
 */

/**
 * Return the width of screen, in pixel.
 *
 * @return the width of screen, in pixel
 */
fun getScreenWidth(): Int {
    val wm = app().getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        wm.defaultDisplay.getRealSize(point)
    } else {
        wm.defaultDisplay.getSize(point)
    }
    return point.x
}

/**
 * Return the height of screen, in pixel.
 *
 * @return the height of screen, in pixel
 */
fun getScreenHeight(): Int {
    val wm = app().getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        wm.defaultDisplay.getRealSize(point)
    } else {
        wm.defaultDisplay.getSize(point)
    }
    return point.y
}

/**
 * Set full screen.
 */
fun Activity.setFullScreen() {
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

/**
 * Set non full screen.
 */
fun Activity.setNonFullScreen() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

/**
 * Return whether screen is full.
 * @return `true`: yes<br></br>`false`: no
 */
fun Activity.isFullScreen(): Boolean {
    val fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
    return window.attributes.flags and fullScreenFlag == fullScreenFlag
}

/**
 * Return the bitmap of screen.
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
    val ret: Bitmap
    ret = if (isDeleteStatusBar) {
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