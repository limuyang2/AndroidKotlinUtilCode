package top.limuyang2.android.ktutilcode.core

import android.support.annotation.Size
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

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

//////////////////////////////////////////////////
//                    DES                       //
//////////////////////////////////////////////////

/**
 * 加密。密码字节数不能小于8
 * @receiver String
 * @param key String
 * @return String
 */
fun String.encryptDES(@Size(min = 8) key: String): String {
    return this.encryptDES(key.toByteArray()).convertToHexString()
}

fun String.encryptDES(@Size(min = 8) key: ByteArray): ByteArray {
    return this.toByteArray().encryptDES(key)
}

fun ByteArray.encryptDES(@Size(min = 8) key: ByteArray): ByteArray {
    return this.desTemplate(key, true)
}

/**
 * 解密
 */
fun String.decryptDES(@Size(min = 8) key: String): String {
    return this.decryptDES(key.toByteArray()).convertToHexString()
}

fun String.decryptDES(@Size(min = 8) key: ByteArray): ByteArray {
    return this.toByteArray().decryptDES(key)
}

fun ByteArray.decryptDES(@Size(min = 8) key: ByteArray): ByteArray {
    return this.desTemplate(key, false)
}

private fun ByteArray.desTemplate(key: ByteArray, isEncrypt: Boolean): ByteArray {
    try {
        //1.初始化SecretKeyFactory（参数1：加密/解密模式）
        val kf = SecretKeyFactory.getInstance("DES")
        val keySpe = DESKeySpec(key)
        val secretKey = kf.generateSecret(keySpe)

        //2.创建cipher对象
        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
        //加密模式
        cipher.init(if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE, secretKey)
        //3.加密/解密
        return cipher.doFinal(this)
    } catch (e: Exception) {
        e.printStackTrace()
        return ByteArray(0)
    }
}

//////////////////////////////////////////////////
//                    AES                       //
//////////////////////////////////////////////////



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


