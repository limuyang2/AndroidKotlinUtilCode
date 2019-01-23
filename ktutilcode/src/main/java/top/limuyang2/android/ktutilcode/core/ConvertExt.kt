package top.limuyang2.android.ktutilcode.core

import java.util.*

/**
 * @author: limuyang
 * @date: 2019/1/23
 * @Description: 转换相关
 */

/**
 * byte数组转换为十六进制字符串
 * @receiver ByteArray
 * @return String
 */
fun ByteArray.convertToHexString(): String {
    val stringBuilder = StringBuilder()
    this.forEach {
        val value = it.toInt() and 0xff
        var hexString = Integer.toHexString(value)
        if (hexString.length < 2) {
            hexString = "0$hexString"
        }
        stringBuilder.append(hexString)
    }
    return stringBuilder.toString()
}

/**
 * 十六进制字符串转换成Byte数组
 *
 * @return ByteArray
 */
fun String.convertToBytes(): ByteArray {
    if (this == "") {
        return ByteArray(0)
    }
    val newHexString = this.trim().toUpperCase()
    val length = newHexString.length / 2
    val hexChars = newHexString.toCharArray()
    val d = ByteArray(length)
    for (i in 0 until length) {
        val pos = i * 2
        d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
    }
    return d
}

/**
 * char转byte
 * @param c char
 * *
 * @return byte
 */
fun charToByte(c: Char): Byte {
    return "0123456789ABCDEF".indexOf(c).toByte()
}

/**
 * 字节数 转 B\KB\MB\GB (保留小数点后两位)
 * @return 数据大小
 */
fun Long.toFitMemorySize(): String {
    return when {
        this < 0 -> "shouldn't be less than zero!"
        this < 1024 -> String.format(Locale.getDefault(), "%.2fB", this.toDouble())
        this < 1048576 -> String.format(Locale.getDefault(), "%.2fKB", this.toDouble() / 1024)
        this < 1073741824 -> String.format(Locale.getDefault(), "%.2fMB", this.toDouble() / 1048576)
        else -> String.format(Locale.getDefault(), "%.2fGB", this.toDouble() / 1073741824)
    }
}