package top.limuyang2.android.ktutilcode.core

import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.Manifest.permission.CHANGE_WIFI_STATE
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission


/**
 * @author: limuyang
 * @date: 2019/1/25
 * @Description: 网络相关
 */

@RequiresPermission(ACCESS_NETWORK_STATE)
fun Context.getActiveNetworkInfo(): NetworkInfo? {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return null
    return cm.activeNetworkInfo
}

/**
 * 判断网络是否连接
 */
inline val Context.isNetworkConnected: Boolean
    @RequiresPermission(ACCESS_NETWORK_STATE)
    get() {
        val info = getActiveNetworkInfo()
        return info != null && info.isConnected
    }

/**
 * 判断网络是否是移动数据
 */
inline val Context.isMobileNetwork: Boolean
    @RequiresPermission(ACCESS_NETWORK_STATE)
    get() {
        val info = getActiveNetworkInfo()
        return (info != null
                && info.isAvailable
                && info.type == ConnectivityManager.TYPE_MOBILE)
    }

/**
 * wifi开闭状态
 */
inline var Context.wifiEnabled: Boolean
    @RequiresPermission(CHANGE_WIFI_STATE)
    set(value) {
        val manager = this.getSystemService(WIFI_SERVICE) as? WifiManager ?: return
        if (value == manager.isWifiEnabled) return
        manager.isWifiEnabled = value
    }
    @RequiresPermission(CHANGE_WIFI_STATE)
    get() {
        val manager = this.getSystemService(WIFI_SERVICE) as? WifiManager ?: return false
        return manager.isWifiEnabled
    }

/**
 * 判断 wifi 是否连接状态
 */
inline val Context.isWifiConnected: Boolean
    @RequiresPermission(ACCESS_NETWORK_STATE)
    get() {
        val info = getActiveNetworkInfo()
        return info != null && info.type == ConnectivityManager.TYPE_WIFI
    }

/**
 * 获取移动网络运营商名称
 */
inline val Context.networkOperatorName: String
    get() {
        val tm = this.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
                ?: return ""
        return tm.networkOperatorName
    }