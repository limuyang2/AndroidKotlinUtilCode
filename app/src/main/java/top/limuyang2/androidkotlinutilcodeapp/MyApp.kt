package top.limuyang2.androidkotlinutilcodeapp

import android.app.Application
import top.limuyang2.android.ktutilcode.KtUtilsCode

/**
 * @name：MyApp
 * @author 李沐阳
 * @date：2018/10/8 10:29
 * @Deprecated
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        KtUtilsCode.init(this)
    }
}