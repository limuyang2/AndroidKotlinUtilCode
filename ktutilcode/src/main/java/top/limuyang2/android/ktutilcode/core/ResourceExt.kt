package top.limuyang2.android.ktutilcode.core

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import top.limuyang2.android.ktutilcode.KtUtilsCode
import java.io.IOException

@Suppress("DEPRECATION")
@ColorInt
fun Int.toColor(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        KtUtilsCode.app.resources.getColor(this, KtUtilsCode.app.theme)
    } else {
        KtUtilsCode.app.resources.getColor(this)
    }
}

fun Int.toDimension(): Float {
    return KtUtilsCode.app.resources.getDimension(this)
}

fun Int.toString(): String {
    return KtUtilsCode.app.resources.getString(this)
}

@Suppress("DEPRECATION")
fun Int.toDrawable(): Drawable {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        KtUtilsCode.app.resources.getDrawable(this, KtUtilsCode.app.theme)
    } else {
        KtUtilsCode.app.resources.getDrawable(this)
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