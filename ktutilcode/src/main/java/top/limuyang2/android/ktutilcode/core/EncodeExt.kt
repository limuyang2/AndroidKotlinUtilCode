package top.limuyang2.android.ktutilcode.core

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder


/**
 * @author: limuyang
 * @date: 2019/1/9
 * @Description: 编解码相关
 */

/**
 * url 编码
 * @receiver String
 * @param charsetName String 默认 UTF-8 编码
 * @return String
 */
fun String?.urlEncode(charsetName: String = "UTF-8"): String {
    if (this.isNullOrEmpty()) return ""
    try {
        return URLEncoder.encode(this, charsetName)
    } catch (e: UnsupportedEncodingException) {
        throw AssertionError(e)
    }
}

/**
 * url 解码
 * @receiver String
 * @param charsetName The name of charset. 默认 UTF-8 编码
 * @return String
 */
fun String?.urlDecode(charsetName: String = "UTF-8"): String {
    if (this.isNullOrEmpty()) return ""
    try {
        return URLDecoder.decode(this, charsetName)
    } catch (e: UnsupportedEncodingException) {
        throw AssertionError(e)
    }
}

/**
 * base64 编码
 * @receiver ByteArray
 * @return ByteArray
 */
fun ByteArray.base64Encode(): ByteArray {
    return if (this.isEmpty())
        ByteArray(0)
    else
        Base64.encode(this, Base64.NO_WRAP)
}

fun String.base64Encode(): ByteArray {
    return this.toByteArray().base64Encode()
}

fun ByteArray.base64EncodeToString(): String {
    return if (this.isEmpty())
        ""
    else
        Base64.encodeToString(this, Base64.NO_WRAP)
}

fun String.base64EncodeToString(): String {
    return this.toByteArray().base64EncodeToString()
}

/**
 * base64 解码
 * @receiver ByteArray
 * @return ByteArray
 */
fun ByteArray.base64Decode(): ByteArray {
    return if (this.isEmpty()) ByteArray(0) else Base64.decode(this, Base64.NO_WRAP)
}

fun String.base64Decode(): ByteArray {
    if (this.isEmpty()) return ByteArray(0)
    return Base64.decode(this, Base64.NO_WRAP)
}

