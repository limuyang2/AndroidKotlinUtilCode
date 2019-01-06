package top.limuyang2.android.ktutilcode.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

/**
 * get SharedPreferences
 * @receiver Context
 * @param spName String
 * @param mode Int
 */
fun Context.getSP(spName: String = "shared_preferences", mode: Int = Context.MODE_PRIVATE): SharedPreferences =
        getSharedPreferences(spName, mode)

@SuppressLint("ApplySharedPref")
inline fun SharedPreferences.edit(
        commit: Boolean = false,
        action: SharedPreferences.Editor.() -> Unit
) {
    val editor = edit()
    action(editor)
    if (commit) {
        editor.commit()
    } else {
        editor.apply()
    }
}

inline fun SharedPreferences.read(action: SharedPreferences.() -> Unit){
    action(this)
}
