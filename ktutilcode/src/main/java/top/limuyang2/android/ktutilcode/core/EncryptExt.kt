package top.limuyang2.android.ktutilcode.core

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @author: limuyang
 * @date: 2019/1/10
 * @Description: 加密解密相关
 */

//////////////////////////////////////////////////
//                    MD5                       //
//////////////////////////////////////////////////

/**
 * 32位md5
 * @receiver String
 * @param salt String?
 * @return String
 */
fun String.md5(salt: String? = null): String {
    if (this.isBlank()) return ""
    val str = salt?.let { this + it } ?: this

    return str.toByteArray().hashTemplate("MD5").convertToHexString()
}

fun File.md5(): String {
    var fis: FileInputStream? = null
    try {
        fis = FileInputStream(this)
        var md = MessageDigest.getInstance("MD5")
        val digestInputStream = DigestInputStream(fis, md)
        val buffer = ByteArray(256 * 1024)
        while (true) {
            if (digestInputStream.read(buffer) <= 0) break
        }
        md = digestInputStream.messageDigest
        return md.digest().convertToHexString()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
        return ""
    } catch (e: IOException) {
        e.printStackTrace()
        return ""
    } finally {
        try {
            fis?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}


//////////////////////////////////////////////////
//                    SHA                       //
//////////////////////////////////////////////////

fun String.sha1(): String {
    return this.toByteArray().hashTemplate("SHA-1").convertToHexString()
}

fun String.sha256(): String {
    return this.toByteArray().hashTemplate("SHA-256").convertToHexString()
}

fun String.sha384(): String {
    return this.toByteArray().hashTemplate("SHA-384").convertToHexString()
}

fun String.sha512(): String {
    return this.toByteArray().hashTemplate("SHA-512").convertToHexString()
}

///////////////////////////////////////////////////////////////////////////
// other utils methods
///////////////////////////////////////////////////////////////////////////

private fun ByteArray.hashTemplate(algorithm: String): ByteArray {
    return try {
        val md = MessageDigest.getInstance(algorithm)
        md.update(this)
        val tc1: MessageDigest = md.clone() as MessageDigest
        return tc1.digest()
    } catch (e: CloneNotSupportedException) {
        e.printStackTrace()
        ByteArray(0)
    }
}

private fun ByteArray.convertToHexString(): String {
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


