package top.limuyang2.android.ktutilcode.core

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import top.limuyang2.android.ktutilcode.KtUtilCode
import java.io.IOException

@Suppress("DEPRECATION")
@ColorInt
fun Int.toColor(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        KtUtilCode.app.resources.getColor(this, KtUtilCode.app.theme)
    } else {
        KtUtilCode.app.resources.getColor(this)
    }
}

fun Int.toDimension(): Float {
    return KtUtilCode.app.resources.getDimension(this)
}

fun Int.toString(): String {
    return KtUtilCode.app.resources.getString(this)
}

@Suppress("DEPRECATION")
fun Int.toDrawable(): Drawable {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        KtUtilCode.app.resources.getDrawable(this, KtUtilCode.app.theme)
    } else {
        KtUtilCode.app.resources.getDrawable(this)
    }
}

fun Context.readAssetsFile(fileName: String): String {
    return try {
        this.assets.open(fileName).reader().readText()
    } catch (e: IOException) {
        e.printStackTrace()
        ""
    }
}