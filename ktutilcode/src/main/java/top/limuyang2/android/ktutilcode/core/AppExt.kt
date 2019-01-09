package top.limuyang2.android.ktutilcode.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
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
 * @receiver Context
 * @param file File
 */
fun Context.installApp(file: File) {
    if (!file.exists()) return
    this.startActivity(getInstallAppIntent(file, true))
}

/**
 * 安装app
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
    intent.setDataAndType(data, type)
    return if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
}
