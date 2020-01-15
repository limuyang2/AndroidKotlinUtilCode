package top.limuyang2.android.ktutilcode.core

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.ColorInt
import top.limuyang2.android.ktutilcode.KtUtilCode

@Suppress("DEPRECATION")
@ColorInt
fun Int.getColor(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        KtUtilCode.app.resources.getColor(this, KtUtilCode.app.theme)
    } else {
        KtUtilCode.app.resources.getColor(this)
    }
}

fun Int.getDimension(): Float {
    return KtUtilCode.app.resources.getDimension(this)
}

fun Int.getString(): String {
    return KtUtilCode.app.resources.getString(this)
}

@Suppress("DEPRECATION")
fun Int.getDrawable(): Drawable {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        KtUtilCode.app.resources.getDrawable(this, KtUtilCode.app.theme)
    } else {
        KtUtilCode.app.resources.getDrawable(this)
    }
}

fun Context.readAssetsFile(fileName: String): String {
    this.assets.open(fileName).use {
        return it.reader().readText()
    }
}