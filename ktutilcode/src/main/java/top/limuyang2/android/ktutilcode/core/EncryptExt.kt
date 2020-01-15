package top.limuyang2.android.ktutilcode.core

import androidx.annotation.Size
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

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

private const val DEF_DES_TF = "DES/ECB/ZeroBytePadding"

/**
 * DES加密。密码字节数不能小于8。
 * @receiver String
 * @param key String
 * @param transformation String 模式选择，默认为: DES/ECB/ZeroBytePadding
 *                          https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
 * @return String
 */
fun String.encryptDES(@Size(min = 8) key: String, transformation: String = DEF_DES_TF): String {
    return this.encryptDES(key.toByteArray(), transformation).convertToHexString()
}

fun String.encryptDES(@Size(min = 8) key: ByteArray, transformation: String = DEF_DES_TF): ByteArray {
    return this.toByteArray().encryptDES(key, transformation)
}

fun ByteArray.encryptDES(@Size(min = 8) key: ByteArray, transformation: String = DEF_DES_TF): ByteArray {
    return this.desTemplate(key, true, transformation)
}

/**
 * DES解密
 */
fun String.decryptDES(@Size(min = 8) key: String, transformation: String = DEF_DES_TF): String {
    return String(this.decryptDES(key.toByteArray(), transformation), Charsets.UTF_8)
}

fun String.decryptDES(@Size(min = 8) key: ByteArray, transformation: String = DEF_DES_TF): ByteArray {
    return this.convertToBytes().decryptDES(key, transformation)
}

fun ByteArray.decryptDES(@Size(min = 8) key: ByteArray, transformation: String = DEF_DES_TF): ByteArray {
    return this.desTemplate(key, false, transformation)
}

private fun ByteArray.desTemplate(key: ByteArray, isEncrypt: Boolean, transformation: String): ByteArray {
    return try {
        //1.初始化SecretKeyFactory
        val kf = SecretKeyFactory.getInstance("DES")
        val keySpe = DESKeySpec(key)
        val secretKey = kf.generateSecret(keySpe)

        //创建cipher对象 https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
        val cipher = Cipher.getInstance(transformation)
        //加密模式
        if (transformation.contains("ECB")) {
            cipher.init(if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE, secretKey)
        } else {
            val iv = IvParameterSpec(key)
            cipher.init(if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE, secretKey, iv)
        }
        //3.加密/解密
        cipher.doFinal(this)
    } catch (e: Exception) {
        e.printStackTrace()
        ByteArray(0)
    }
}

//////////////////////////////////////////////////
//                    AES                       //
//////////////////////////////////////////////////

private const val DEF_AES_TF = "AES/ECB/ZeroBytePadding"

/**
 * AES加密。密码字节数不能小于16。
 * @receiver String
 * @param key String
 * @param transformation String 模式选择，默认为: AES/ECB/ZeroBytePadding
 *                          https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
 * @return String
 */
fun String.encryptAES(@Size(min = 16) key: String, transformation: String = DEF_AES_TF): String {
    return this.encryptAES(key.toByteArray(), transformation).convertToHexString()
}

fun String.encryptAES(@Size(min = 16) key: ByteArray, transformation: String = DEF_AES_TF): ByteArray {
    return this.toByteArray().encryptAES(key, transformation)
}

fun ByteArray.encryptAES(@Size(min = 16) key: ByteArray, transformation: String = DEF_AES_TF): ByteArray {
    return this.aesTemplate(key, true, transformation)
}

/**
 * AES解密
 */
fun String.decryptAES(@Size(min = 16) key: String, transformation: String = DEF_AES_TF): String {
    return String(this.decryptAES(key.toByteArray(), transformation), Charsets.UTF_8)
}

fun String.decryptAES(@Size(min = 16) key: ByteArray, transformation: String = DEF_AES_TF): ByteArray {
    return this.convertToBytes().decryptAES(key, transformation)
}

fun ByteArray.decryptAES(@Size(min = 16) key: ByteArray, transformation: String = DEF_AES_TF): ByteArray {
    return this.aesTemplate(key, false, transformation)
}

private fun ByteArray.aesTemplate(key: ByteArray, isEncrypt: Boolean, transformation: String): ByteArray {
    return try {
        //1.生产秘钥
        val keySpec = SecretKeySpec(key, "AES")

        //2.创建cipher对象 https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
        val cipher = Cipher.getInstance(transformation)
        //加密模式
        if (transformation.contains("ECB")) {
            cipher.init(if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE, keySpec)
        } else {
            val iv = IvParameterSpec(key)
            cipher.init(if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE, keySpec, iv)
        }
        //3.加密/解密
        cipher.doFinal(this)
    } catch (e: Exception) {
        e.printStackTrace()
        ByteArray(0)
    }
}

///////////////////////////////////////////////////////////////////////////
// other utils methods
///////////////////////////////////////////////////////////////////////////

fun ByteArray.hashTemplate(algorithm: String): ByteArray {
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
