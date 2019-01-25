package top.limuyang2.android.ktutilcode.core

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.IntRange
import android.support.annotation.RequiresApi
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout


private const val TAG_COLOR = "TAG_COLOR"
const val TAG_OFFSET = "TAG_OFFSET"
private const val KEY_OFFSET = -123

/**
 * 获取状态栏的高度
 */
inline val Context.statusBarHeight: Int
    get() {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

/**
 * status bar visible attribute.
 */
inline var Activity.statusBarVisible: Boolean
    set(value) {
        this.window.statusBarVisible = value
    }
    get() = this.window.statusBarVisible

inline var Window.statusBarVisible: Boolean
    set(value) {
        if (value) {
            clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            showColorView()

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
            val withTag = decorView.findViewWithTag<View>(TAG_OFFSET) ?: return
            withTag.addMarginTopEqualStatusBarHeight()
        } else {
            this.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            hideColorView()

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
            val withTag = decorView.findViewWithTag<View>(TAG_OFFSET) ?: return
            withTag.subtractMarginTopEqualStatusBarHeight()
        }
    }
    get() {
        val flags = attributes.flags
        return flags and WindowManager.LayoutParams.FLAG_FULLSCREEN == 0
    }

/**
 * Set the status bar's light mode.
 *
 */
inline var Activity.statusBarLightMode: Boolean
    set(value) {
        window.statusBarLightMode = value
    }
    get() = window.statusBarLightMode

inline var Window.statusBarLightMode: Boolean
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (decorView != null) {
                var vis = decorView.systemUiVisibility
                vis = if (value) {
                    this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
                decorView.systemUiVisibility = vis
            }
        }
    }
    get() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (decorView != null) {
                val vis = decorView.systemUiVisibility
                return vis == vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
        return false
    }

/**
 * Add the top margin size equals status bar's height for view.
 *
 */
fun View.addMarginTopEqualStatusBarHeight() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    this.tag = TAG_OFFSET
    val haveSetOffset = getTag(KEY_OFFSET)
    if (haveSetOffset != null && haveSetOffset as Boolean) return
    val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(layoutParams.leftMargin,
            layoutParams.topMargin + context.statusBarHeight,
            layoutParams.rightMargin,
            layoutParams.bottomMargin)
    setTag(KEY_OFFSET, true)
}

/**
 * Subtract the top margin size equals status bar's height for view.
 *
 */
fun View.subtractMarginTopEqualStatusBarHeight() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    val haveSetOffset = this.getTag(KEY_OFFSET)
    if (haveSetOffset == null || !(haveSetOffset as Boolean)) return
    val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(layoutParams.leftMargin,
            layoutParams.topMargin - context.statusBarHeight,
            layoutParams.rightMargin,
            layoutParams.bottomMargin)
    this.setTag(KEY_OFFSET, false)
}

/**
 * Set the status bar's color.
 *
 * @param color    The status bar's color.
 * @param alpha    The status bar's alpha which isn't the same as alpha in the color.
 * @param isDecor  True to add fake status bar in DecorView,
 * false to add fake status bar in ContentView.
 */
fun Activity.setStatusBarColor(@ColorInt color: Int,
                               @IntRange(from = 0, to = 255) alpha: Int = 0,
                               isDecor: Boolean = true) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    transparentStatusBar()

//    if (isDecor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        window.statusBarColor = getStatusBarColor(color, alpha)
//        return
//    }

    val parent = if (isDecor)
        window.decorView as ViewGroup
    else
        findViewById<View>(android.R.id.content) as ViewGroup
    val fakeStatusBarView = parent.findViewWithTag<View>(TAG_COLOR)
    if (fakeStatusBarView != null) {
        if (fakeStatusBarView.visibility == View.GONE) {
            fakeStatusBarView.visibility = View.VISIBLE
        }
        fakeStatusBarView.setBackgroundColor(createStatusBarColor(color, alpha))
    } else {
        parent.addView(createColorStatusBarView(this, color, alpha))
    }
}

/**
 * Set the status bar's color.
 *
 * @param color         The status bar's color.
 * @param alpha         The status bar's alpha which isn't the same as alpha in the color.
 */
fun View.setStatusBarColor(@ColorInt color: Int,
                           @IntRange(from = 0, to = 255) alpha: Int = 0) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    this.visibility = View.VISIBLE
    (context as Activity).transparentStatusBar()
    val layoutParams = layoutParams
    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    layoutParams.height = context.statusBarHeight
    setBackgroundColor(createStatusBarColor(color, alpha))
}


fun Window.hideColorView() {
    val decorView = decorView as ViewGroup
    val fakeStatusBarView = decorView.findViewWithTag<View>(TAG_COLOR) ?: return
    fakeStatusBarView.visibility = View.GONE
}


fun Window.showColorView() {
    val decorView = decorView as ViewGroup
    val fakeStatusBarView = decorView.findViewWithTag<View>(TAG_COLOR) ?: return
    fakeStatusBarView.visibility = View.VISIBLE
}


private fun createStatusBarColor(color: Int, alpha: Int): Int {
    if (alpha == 0) return color
    val a = 1 - alpha / 255f
    var red = color shr 16 and 0xff
    var green = color shr 8 and 0xff
    var blue = color and 0xff
    red = (red * a + 0.5).toInt()
    green = (green * a + 0.5).toInt()
    blue = (blue * a + 0.5).toInt()
    return Color.argb(255, red, green, blue)
}

private fun createColorStatusBarView(context: Context,
                                     color: Int,
                                     alpha: Int): View {
    val statusBarView = View(context)
    statusBarView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, context.statusBarHeight)
    statusBarView.setBackgroundColor(createStatusBarColor(color, alpha))
    statusBarView.tag = TAG_COLOR
    return statusBarView
}


fun Activity.transparentStatusBar() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT
    } else {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
}

///////////////////////////////////////////////////////////////////////////
// action bar
///////////////////////////////////////////////////////////////////////////

/**
 * Return the action bar's height.
 *
 * @return the action bar's height
 */
inline val Activity.actionBarHeight: Int
    get() {
        val tv = TypedValue()
        return if (this.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            TypedValue.complexToDimensionPixelSize(
                    tv.data, this.resources.displayMetrics
            )
        } else 0
    }


///////////////////////////////////////////////////////////////////////////
// navigation bar
///////////////////////////////////////////////////////////////////////////

/**
 * Return the navigation bar's height.
 *
 * @return the navigation bar's height
 */
inline val Context.navBarHeight: Int
    get() {
        val res = this.resources
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId != 0) {
            res.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }


/**
 * Set the navigation bar's visibility.
 *
 */
inline var Activity.navBarVisible: Boolean
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    set(value) {
        window.navBarVisible = value
    }
    get() = window.navBarVisible


inline var Window.navBarVisible: Boolean
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    set(value) {
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        if (value) {
            decorView.systemUiVisibility = decorView.systemUiVisibility and uiOptions.inv()
        } else {
            decorView.systemUiVisibility = decorView.systemUiVisibility or uiOptions
        }
    }
    get() {
        val visibility = decorView.systemUiVisibility
        return visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION == 0
    }

/**
 * Set the navigation bar's color.
 *
 */
inline var Activity.navBarColor: Int
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    set(value) {
        window.navBarColor = value
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    get() = window.navBarColor

inline var Window.navBarColor: Int
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    set(value) {
        navigationBarColor = value
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    get() = navigationBarColor

/**
 * Return whether the navigation bar visible.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun Context.isSupportNavBar(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val display = wm.defaultDisplay
        val size = Point()
        val realSize = Point()
        display.getSize(size)
        display.getRealSize(realSize)
        return realSize.y != size.y || realSize.x != size.x
    }
    val menu = ViewConfiguration.get(this).hasPermanentMenuKey()
    val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
    return !menu && !back
}