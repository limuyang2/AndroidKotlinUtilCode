package top.limuyang2.android.ktutilcode.core

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable


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