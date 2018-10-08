package top.limuyang2.android.ktutilcode.utils

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

/**
 * @name：BarUtils
 * @author lmy
 * @date：2018/10/8 16:21
 * @Deprecated
 */

///////////////////////////////////////////////////////////////////////////
// status bar
///////////////////////////////////////////////////////////////////////////


private const val TAG_COLOR = "TAG_COLOR"
private const val TAG_OFFSET = "TAG_OFFSET"
private const val KEY_OFFSET = -123


/**
 * Return the status bar's height.
 *
 * @return the status bar's height
 */
fun Context.getStatusBarHeight(): Int {
    val resources = this.resources
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

/**
 * Set the status bar's visibility.
 *
 * @param isVisible True to set status bar visible, false otherwise.
 */
fun Activity.setStatusBarVisibility(isVisible: Boolean) {
    window.setStatusBarVisibility(isVisible)
}

/**
 * Set the status bar's visibility.
 *
 * @param isVisible True to set status bar visible, false otherwise.
 */
fun Window.setStatusBarVisibility(isVisible: Boolean) {
    if (isVisible) {
        clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        showColorView(this)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
        val withTag = decorView.findViewWithTag<View>(TAG_OFFSET) ?: return
        withTag.addMarginTopEqualStatusBarHeight()
    } else {
        this.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        hideColorView(this)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
        val withTag = decorView.findViewWithTag<View>(TAG_OFFSET) ?: return
        withTag.subtractMarginTopEqualStatusBarHeight()
    }
}

/**
 * Return whether the status bar is visible.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun Activity.isStatusBarVisible(): Boolean {
    val flags = window.attributes.flags
    return flags and WindowManager.LayoutParams.FLAG_FULLSCREEN == 0
}

/**
 * Set the status bar's light mode.
 *
 * @param isLightMode True to set status bar light mode, false otherwise.
 */
fun Activity.setStatusBarLightMode(isLightMode: Boolean) {
    window.setStatusBarLightMode(isLightMode)
}

/**
 * Set the status bar's light mode.
 *
 * @param isLightMode True to set status bar light mode, false otherwise.
 */
fun Window.setStatusBarLightMode(isLightMode: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (decorView != null) {
            var vis = decorView.systemUiVisibility
            vis = if (isLightMode) {
                this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            decorView.systemUiVisibility = vis
        }
    }
}

/**
 * Add the top margin size equals status bar's height for view.
 *
 */
fun View.addMarginTopEqualStatusBarHeight() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    tag = TAG_OFFSET
    val haveSetOffset = getTag(KEY_OFFSET)
    if (haveSetOffset != null && haveSetOffset as Boolean) return
    val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(layoutParams.leftMargin,
            layoutParams.topMargin + context.getStatusBarHeight(),
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
            layoutParams.topMargin - context.getStatusBarHeight(),
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
                               isDecor: Boolean = false) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    transparentStatusBar()

    val parent = if (isDecor)
        window.decorView as ViewGroup
    else
        findViewById<View>(android.R.id.content) as ViewGroup
    val fakeStatusBarView = parent.findViewWithTag<View>(TAG_COLOR)
    if (fakeStatusBarView != null) {
        if (fakeStatusBarView.visibility == View.GONE) {
            fakeStatusBarView.visibility = View.VISIBLE
        }
        fakeStatusBarView.setBackgroundColor(getStatusBarColor(color, alpha))
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
    visibility = View.VISIBLE
    (context as Activity).transparentStatusBar()
    val layoutParams = layoutParams
    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    layoutParams.height = context.getStatusBarHeight()
    setBackgroundColor(getStatusBarColor(color, alpha))
}


private fun hideColorView(window: Window) {
    val decorView = window.decorView as ViewGroup
    val fakeStatusBarView = decorView.findViewWithTag<View>(TAG_COLOR) ?: return
    fakeStatusBarView.visibility = View.GONE
}


private fun showColorView(window: Window) {
    val decorView = window.decorView as ViewGroup
    val fakeStatusBarView = decorView.findViewWithTag<View>(TAG_COLOR) ?: return
    fakeStatusBarView.visibility = View.VISIBLE
}


private fun getStatusBarColor(color: Int, alpha: Int): Int {
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
            ViewGroup.LayoutParams.MATCH_PARENT, context.getStatusBarHeight())
    statusBarView.setBackgroundColor(getStatusBarColor(color, alpha))
    statusBarView.tag = TAG_COLOR
    return statusBarView
}


fun Activity.transparentStatusBar() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    val window = this.window
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
fun Activity.getActionBarHeight(): Int {
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
fun Context.getNavBarHeight(): Int {
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
 * @param isVisible True to set navigation bar visible, false otherwise.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
fun Activity.setNavBarVisibility(isVisible: Boolean) {
    window.setNavBarVisibility(isVisible)
}

/**
 * Set the navigation bar's visibility.
 *
 * @param isVisible True to set navigation bar visible, false otherwise.
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
fun Window.setNavBarVisibility(isVisible: Boolean) {
    val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    if (isVisible) {
        decorView.systemUiVisibility = decorView.systemUiVisibility and uiOptions.inv()
    } else {
        decorView.systemUiVisibility = decorView.systemUiVisibility or uiOptions
    }
}

/**
 * Return whether the navigation bar visible.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun Activity.isNavBarVisible(): Boolean {
    return window.isNavBarVisible()
}

/**
 * Return whether the navigation bar visible.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun Window.isNavBarVisible(): Boolean {
    val visibility = decorView.systemUiVisibility
    return visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION == 0
}

/**
 * Set the navigation bar's color.
 *
 * @param color    The navigation bar's color.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.setNavBarColor(@ColorInt color: Int) {
    window.setNavBarColor(color)
}

/**
 * Set the navigation bar's color.
 *
 * @param color  The navigation bar's color.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Window.setNavBarColor(@ColorInt color: Int) {
    navigationBarColor = color
}

/**
 * Return the color of navigation bar.
 *
 * @return the color of navigation bar
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.getNavBarColor(): Int = window.getNavBarColor()

/**
 * Return the color of navigation bar.
 *
 * @return the color of navigation bar
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Window.getNavBarColor(): Int = navigationBarColor


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
    val menu = ViewConfiguration.get(app()).hasPermanentMenuKey()
    val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
    return !menu && !back
}
