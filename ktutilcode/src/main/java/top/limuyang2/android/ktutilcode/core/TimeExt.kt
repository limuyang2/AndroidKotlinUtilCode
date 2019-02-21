package top.limuyang2.android.ktutilcode.core

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author: limuyang
 * @date: 2019/1/23
 * @Description: 时间相关扩展
 */

/**
 * 时间戳转为string字符串。
 * @receiver Long
 * @param format DateFormat 默认格式：yyyy-MM-dd HH:mm:ss
 * @return String
 */
fun Long.timestampToStr(formatStr: String = "yyyy-MM-dd HH:mm:ss"): String {
    return SimpleDateFormat(formatStr, Locale.getDefault()).format(Date(this))
}

fun String.timestampToStr(formatStr: String = "yyyy-MM-dd HH:mm:ss"): String {
    return this.toLong().timestampToStr(formatStr)
}


/**
 * 日期字符串转换为时间戳
 * @receiver String
 * @param format DateFormat 默认格式：yyyy-MM-dd HH:mm:ss
 * @return Long
 */
fun String.toTimestamp(format: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())): Long {
    try {
        return format.parse(this).time
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return -1
}

fun String.toTimestamp(formatStr: String = "yyyy-MM-dd HH:mm:ss"): Long {
    return this.toTimestamp(SimpleDateFormat(formatStr, Locale.getDefault()))
}