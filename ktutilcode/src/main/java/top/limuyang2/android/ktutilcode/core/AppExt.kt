package top.limuyang2.android.ktutilcode.core

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File


/**
 * Return the application's icon.
 *
 * @param packageName The name of the package.
 * @return the application's icon
 */
fun Context.getAppIcon(packageName: String = this.packageName): Drawable? {
    if (packageName.isBlank()) return null
    return try {
        val pm = packageManager
        val pi = pm.getPackageInfo(packageName, 0)
        pi?.applicationInfo?.loadIcon(pm)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }
}

/**
 * Return the application's name.
 *
 * @param packageName The name of the package.
 * @return the application's name
 */
fun Context.getAppName(packageName: String = this.packageName): String {
    if (packageName.isBlank()) return ""
    return try {
        val pm = packageManager
        val pi = pm.getPackageInfo(packageName, 0)
        pi?.applicationInfo?.loadLabel(pm)?.toString() ?: ""
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        ""
    }
}

/**
 * Return the application's path.
 *
 * @param packageName The name of the package.
 * @return the application's path
 */
fun Context.getAppPath(packageName: String = this.packageName): String {
    if (packageName.isBlank()) return ""
    return try {
        val pm = packageManager
        val pi = pm.getPackageInfo(packageName, 0)
        pi?.applicationInfo?.sourceDir ?: ""
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        ""
    }
}

/**
 * Return the application's version name.
 *
 * @param packageName The name of the package.
 * @return the application's version name
 */
fun Context.getAppVersionName(packageName: String = this.packageName): String {
    if (packageName.isBlank()) return ""
    return try {
        val pm = packageManager
        pm.getPackageInfo(packageName, 0)?.versionName ?: ""
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        ""
    }
}

/**
 * Return the application's version code.
 *
 * @param packageName The name of the package.
 * @return the application's version code
 */
fun Context.getAppVersionCode(packageName: String = this.packageName): Int {
    if (packageName.isBlank()) return -1
    return try {
        val pm = packageManager
        val pi = pm.getPackageInfo(packageName, 0)
        pi?.versionCode ?: -1
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        -1
    }
}

/**
 * 安装app
 * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}
 *
 * @receiver Context
 * @param file File
 */
fun Context.installApp(file: File) {
    if (!file.exists()) return
    this.startActivity(getInstallAppIntent(file, true))
}

/**
 * 安装app
 * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}
 *
 * @receiver Activity
 * @param file File
 * @param requestCode Int 请求码
 */
fun Activity.installApp(file: File, requestCode: Int) {
    if (!file.exists()) return
    this.startActivityForResult(getInstallAppIntent(file), requestCode)
}

private fun Context.getInstallAppIntent(file: File, isNewTask: Boolean = false): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    val data: Uri
    val type = "application/vnd.android.package-archive"
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        data = Uri.fromFile(file)
    } else {
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val authority = this.packageName + ".ktutilcode.provider"
        data = FileProvider.getUriForFile(this, authority, file)
    }
    grantUriPermission(packageName, data, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.setDataAndType(data, type)
    return if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
}

/**
 * 获取app签名
 * @receiver Context
 * @param packageName String
 * @return Array<Signature>?
 */
@SuppressLint("PackageManagerGetSignatures")
fun Context.getAppSignature(packageName: String = this.packageName): Array<Signature>? {
    if (packageName.isBlank()) return null
    return try {
        packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)?.signatures
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }
}

private fun Context.getAppSignatureSHA(algorithm: String, packageName: String): String {
    if (packageName.isBlank()) return ""
    val signature = getAppSignature(packageName)
    return if (signature == null || signature.isEmpty()) "" else signature[0].toByteArray().hashTemplate(algorithm).convertToHexString().replace("(?<=[0-9A-F]{2})[0-9A-F]{2}", ":$0")
}

fun Context.getAppSignatureSHA1(packageName: String = this.packageName): String {
    return getAppSignatureSHA("SHA-1", packageName)
}

fun Context.getAppSignatureSHA256(packageName: String = this.packageName): String {
    return getAppSignatureSHA("SHA-256", packageName)
}

fun Context.getAppSignatureMD5(packageName: String = this.packageName): String {
    return getAppSignatureSHA("MD5", packageName)
}

/**
 * 判断 App 是否处于前台
 * @receiver Context
 * @return Boolean
 */
fun Context.isAppForeground(): Boolean {
    val am = this.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            ?: return false
    val info = am.runningAppProcesses
    if (info.isNullOrEmpty()) return false
    for (aInfo in info) {
        if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            return aInfo.processName == this.packageName
        }
    }
    return false
}