package top.limuyang2.androidkotlinutilcodeapp

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import top.limuyang2.android.ktutilcode.utils.setStatusBarColor
import top.limuyang2.android.ktutilcode.utils.setStatusBarVisibility

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setStatusBarColor(resources.getColor(R.color.colorPrimary),0,true)
//        setStatusBarVisibility(false)

    }
}
