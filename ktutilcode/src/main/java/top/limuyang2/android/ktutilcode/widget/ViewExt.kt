package top.limuyang2.android.ktutilcode.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.view.ViewGroup

//View 扩展
fun View.setHeight(value: Int) {
    val lp = layoutParams
    lp?.let {
        lp.height = value
        layoutParams = lp
    }
}

fun View.setWidth(value: Int) {
    val lp = layoutParams
    lp?.let {
        lp.width = value
        layoutParams = lp
    }
}

fun View.resize(width: Int, height: Int) {
    val lp = layoutParams
    lp?.let {
        lp.width = width
        lp.height = height
        layoutParams = lp
    }
}

//////////////////////////////////////////////////
//                visibility                    //
//////////////////////////////////////////////////
inline var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

inline var View.invisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) {
        visibility = if (value) View.INVISIBLE else View.VISIBLE
    }

inline var View.gone: Boolean
    get() = visibility == View.GONE
    set(value) {
        visibility = if (value) View.GONE else View.VISIBLE
    }

//////////////////////////////////////////////////
//                  Margin                      //
//////////////////////////////////////////////////
/**
 * 设置view的margin值
 * code Example :
 *      view.margin {
 *          bottomMargin = 20
 *          topMargin = 20
 *          ...
 *      }
 *
 * @receiver View
 * @param margin ViewGroup.MarginLayoutParams.() -> Unit
 */
inline fun View.margin(margin: ViewGroup.MarginLayoutParams.() -> Unit) {
    margin(this.layoutParams as ViewGroup.MarginLayoutParams)
}

inline var View.bottomMargin: Int
    get():Int {
        return (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
    }
    set(value) {
        (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = value
    }

inline var View.topMargin: Int
    get():Int {
        return (layoutParams as ViewGroup.MarginLayoutParams).topMargin
    }
    set(value) {
        (layoutParams as ViewGroup.MarginLayoutParams).topMargin = value
    }

inline var View.rightMargin: Int
    get():Int {
        return (layoutParams as ViewGroup.MarginLayoutParams).rightMargin
    }
    set(value) {
        (layoutParams as ViewGroup.MarginLayoutParams).rightMargin = value
    }

inline var View.leftMargin: Int
    get():Int {
        return (layoutParams as ViewGroup.MarginLayoutParams).leftMargin
    }
    set(value) {
        (layoutParams as ViewGroup.MarginLayoutParams).leftMargin = value
    }

fun View.setMargin(left: Int, top: Int, right: Int, bottom: Int) {
    (layoutParams as ViewGroup.MarginLayoutParams).setMargins(left, top, right, bottom)
}

//////////////////////////////////////////////////
//                  Padding                     //
//////////////////////////////////////////////////
inline var View.leftPadding: Int
    get() = paddingLeft
    set(value) = setPadding(value, paddingTop, paddingRight, paddingBottom)

inline var View.topPadding: Int
    get() = paddingTop
    set(value) = setPadding(paddingLeft, value, paddingRight, paddingBottom)

inline var View.rightPadding: Int
    get() = paddingRight
    set(value) = setPadding(paddingLeft, paddingTop, value, paddingBottom)

inline var View.bottomPadding: Int
    get() = paddingBottom
    set(value) = setPadding(paddingLeft, paddingTop, paddingRight, value)


//////////////////////////////////////////////////
//                   click                      //
//////////////////////////////////////////////////
/**
 *  延时触发 即多少时间内重复点击无反应
 */
private inline var View.triggerDelay: Long
    get() = if (getTag(1123461123) != null) getTag(1123461123) as Long else -1
    set(value) {
        setTag(1123461123, value)
    }

/**
 *  上次点击事件时间
 */
private inline var View.triggerLastTime: Long
    get() = if (getTag(1123460103) != null) getTag(1123460103) as Long else 0
    set(value) {
        setTag(1123460103, value)
    }

/**
 * 计算是否过了延时期
 */
fun View.delayOver(): Boolean {
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        triggerLastTime = currentClickTime
        return true
    }
    return false
}

fun View.click(time: Long = 500, block: (View) -> Unit) {
    triggerDelay = time

    setOnClickListener {
        if (delayOver()) {
            block(it)
        }
    }
}

/**
 * 长按监听
 */
fun View.longClick(block: (view: View) -> Boolean) {
    setOnLongClickListener(block)
}

/**
 * view 转 Bitmap
 * @receiver View
 * @return Bitmap
 */
fun View.toBitmap(): Bitmap {
    val ret = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(ret)
    val bgDrawable = this.background
    if (bgDrawable != null) {
        bgDrawable.draw(canvas)
    } else {
        canvas.drawColor(Color.WHITE)
    }
    this.draw(canvas)
    return ret
}