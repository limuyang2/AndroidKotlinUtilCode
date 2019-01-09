package top.limuyang2.android.ktutilcode.core

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout

/**
 * Show the soft input.
 */
fun Activity.showSoftInput() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    var view = currentFocus
    if (view == null) {
        view = View(this)
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
    }
    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
}

/**
 * Show the soft input.
 */
fun View.showSoftInput() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    isFocusable = true
    isFocusableInTouchMode = true
    requestFocus()
    imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

/**
 * Hide the soft input.
 */
fun Activity.hideSoftInput() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    var view = currentFocus
    if (view == null) view = window.decorView
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Hide the soft input.
 */
fun View.hideSoftInput() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    imm.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * Toggle the soft input display or not.
 */
fun Activity.toggleSoftInput() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}


///////////////////////////////////////////
//      键盘弹出监听 相关(实验性质)
///////////////////////////////////////////
private var sDecorViewDelta = 0

private fun Activity.getDecorViewInvisibleHeight(): Int {
    val decorView = window.decorView ?: return sDecorViewInvisibleHeightPre
    val outRect = Rect()
    decorView.getWindowVisibleDisplayFrame(outRect)
    val delta = Math.abs(decorView.bottom - outRect.bottom)
    if (delta <= navBarHeight) {
        sDecorViewDelta = delta
        return 0
    }
    return delta - sDecorViewDelta
}

private var onGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
private var sDecorViewInvisibleHeightPre: Int = 0
private var onSoftInputChangedListener: OnSoftInputChangedListener? = null

private typealias OnSoftInputChangedListener = (height: Int) -> Unit

/**
 * Register soft input changed listener.
 *
 * @param listener The soft input changed listener.
 */
fun Activity.registerSoftInputChangedListener(listener: OnSoftInputChangedListener) {
    val flags = window.attributes.flags
    if (flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS != 0) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
    val contentView = findViewById<FrameLayout>(android.R.id.content)
    sDecorViewInvisibleHeightPre = getDecorViewInvisibleHeight()
    onSoftInputChangedListener = listener
    onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        onSoftInputChangedListener?.let {
            val height = getDecorViewInvisibleHeight()
            if (sDecorViewInvisibleHeightPre != height) {
                it.invoke(height)
                sDecorViewInvisibleHeightPre = height
            }
        }
    }
    contentView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
}

/**
 * Unregister soft input changed listener.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
fun Activity.unregisterSoftInputChangedListener() {
    val contentView = findViewById<View>(android.R.id.content)
    contentView.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
    onSoftInputChangedListener = null
    onGlobalLayoutListener = null
}