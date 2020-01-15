package top.limuyang2.android.ktutilcode.core

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

/**
 * @author: limuyang
 * @date: 2019/2/26
 * @Description: activity属性相关扩展
 */

fun Activity.setBackgroundColor(@ColorInt color: Int) {
    window.setBackgroundDrawable(ColorDrawable(color))
}

//////////////////////////////////////////////////
//                 Fragment相关                  //
//////////////////////////////////////////////////
fun FragmentActivity.addFragment(addFragment: Fragment,
                                 @IdRes containerId: Int,
                                 isHide: Boolean = false,
                                 isAddStack: Boolean = false,
                                 tag: String = addFragment::class.java.name) {
    supportFragmentManager.add(addFragment, containerId, isHide, isAddStack, tag)
}

fun FragmentActivity.addFragment(addList: List<Fragment>,
                                 @IdRes containerId: Int,
                                 showIndex: Int = 0) {
    supportFragmentManager.add(addList, containerId, showIndex)
}

fun FragmentActivity.replaceFragment(fragment: Fragment,
                                     @IdRes containerId: Int,
                                     isAddStack: Boolean = false,
                                     tag: String = fragment::class.java.name) {
    supportFragmentManager.replace(fragment, containerId, isAddStack, tag)
}

fun FragmentActivity.showFragment(fragment: Fragment) {
    supportFragmentManager.show(fragment)
}

fun FragmentActivity.hideFragment(vararg fragment: Fragment) {
    supportFragmentManager.hide(*fragment)
}

fun FragmentActivity.removeFragment(vararg fragment: Fragment) {
    supportFragmentManager.remove(*fragment)
}

fun FragmentActivity.removeAllFragment() {
    supportFragmentManager.removeAll()
}

fun FragmentActivity.switchFragment(showFragment: Fragment,
                                    @IdRes containerId: Int,
                                    transaction: Int = FragmentTransaction.TRANSIT_NONE) {
    supportFragmentManager.switch(showFragment, containerId, transaction)
}


