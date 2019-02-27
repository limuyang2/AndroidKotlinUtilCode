package top.limuyang2.android.ktutilcode.core

import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan

/**
 * @author: limuyang
 * @date: 2019/1/23
 * @Description: Spannable相关
 */

fun CharSequence.setBackgroundColor(color: Int): CharSequence {
    val s = SpannableString(this)
    s.setSpan(BackgroundColorSpan(color), 0, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return s
}

fun CharSequence.setForegroundColor(color: Int): CharSequence {
    val s = SpannableString(this)
    s.setSpan(ForegroundColorSpan(color), 0, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return s
}