@file:Suppress("unused")

package top.limuyang2.android.ktutilcode.core

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

/**
 * @author: limuyang
 * @date: 2019/2/25
 * @Description:
 */

/**
 * 添加fragment
 * @receiver FragmentManager
 * @param addFragment Fragment
 * @param containerId Int 容器控件id
 * @param isHide Boolean 是否隐藏
 * @param isAddStack Boolean 是否添加到回退栈
 */
fun FragmentManager.add(addFragment: Fragment,
                        @IdRes containerId: Int,
                        isHide: Boolean = false,
                        isAddStack: Boolean = false,
                        tag: String = addFragment::class.java.name) {
    val ft = this.beginTransaction()
    val fragmentByTag = this.findFragmentByTag(tag)
    if (fragmentByTag != null && fragmentByTag.isAdded) {
        ft.remove(fragmentByTag)
    }
    ft.add(containerId, addFragment, tag)
    if (isHide) ft.hide(addFragment)
    if (isAddStack) ft.addToBackStack(tag)

    ft.commit()
}

/**
 * 添加fragment list
 * @receiver FragmentManager
 * @param addList List<Fragment>
 * @param containerId Int
 * @param showIndex Int
 */
fun FragmentManager.add(addList: List<Fragment>,
                        @IdRes containerId: Int,
                        showIndex: Int = 0) {
    val ft = this.beginTransaction()
    for (i in 0 until addList.size) {
        val addFragment = addList[i]
        val tag = addFragment::class.java.name
        val fragmentByTag = this.findFragmentByTag(tag)
        if (fragmentByTag != null && fragmentByTag.isAdded) {
            ft.remove(fragmentByTag)
        }
        ft.add(containerId, addFragment, tag)

        if (showIndex != i) ft.hide(addFragment)
    }
    ft.commit()
}

fun FragmentManager.hide(vararg hideFragment: Fragment) {
    val ft = this.beginTransaction()
    for (fragment in hideFragment) {
        ft.hide(fragment)
    }
    ft.commit()
}

fun FragmentManager.hide(hideFragment: List<Fragment>) {
    val ft = this.beginTransaction()
    for (fragment in hideFragment) {
        ft.hide(fragment)
    }
    ft.commit()
}

fun Fragment.hide() {
    this.fragmentManager?.hide(this)
}

fun FragmentManager.show(showFragment: Fragment) {
    val ft = this.beginTransaction()
    ft.show(showFragment)
    ft.commit()
}

fun Fragment.show() {
    this.fragmentManager?.show(this)
}

fun FragmentManager.remove(vararg removeFragment: Fragment) {
    val ft = this.beginTransaction()
    for (fragment in removeFragment) {
        ft.remove(fragment)
    }
    ft.commit()
}

fun Fragment.remove() {
    this.fragmentManager?.remove(this)
}

fun FragmentManager.removeTo(removeTo: Fragment, isIncludeSelf: Boolean = false) {
    val ft = this.beginTransaction()
    val fragments = this.getFmFragments()
    for (i in (fragments.size - 1)..0) {
        val fragment = fragments[i]
        if (fragment == removeTo && isIncludeSelf) {
            ft.remove(fragment)
            break
        }
        ft.remove(fragment)
    }
    ft.commit()
}

fun FragmentManager.showHide(
        showFragment: Fragment,
        vararg hideFragment: Fragment,
        transaction: Int = FragmentTransaction.TRANSIT_NONE) {
    val ft = this.beginTransaction().setTransition(transaction)

    ft.show(showFragment)
    for (fragment in hideFragment) {
        if (fragment != showFragment) {
            ft.hide(fragment)
        }
    }

    ft.commit()
}

fun Fragment.showHide(vararg hideFragment: Fragment,
                      transaction: Int = FragmentTransaction.TRANSIT_NONE) {
    this.fragmentManager?.showHide(this, *hideFragment, transaction = transaction)
}

fun FragmentManager.replace(fragment: Fragment,
                            @IdRes containerId: Int,
                            isAddStack: Boolean = false,
                            tag: String = fragment::class.java.name) {
    val ft = this.beginTransaction()

    ft.replace(containerId, fragment, tag)
    if (isAddStack) ft.addToBackStack(tag)

    ft.commit()
}

fun FragmentManager.switch(showFragment: Fragment,
                           @IdRes containerId: Int,
                           transaction: Int = FragmentTransaction.TRANSIT_NONE) {
    val ft = this.beginTransaction().setTransition(transaction)

    val tag = showFragment::class.java.name
    val fragmentByTag = this.findFragmentByTag(tag)
    if (fragmentByTag != null && fragmentByTag.isAdded) {
        ft.show(fragmentByTag)
    } else {
        ft.add(containerId, showFragment, tag)
    }

    for (tempF in this.getFmFragments()) {
        if (tempF != fragmentByTag) {
            ft.hide(tempF)
        }
    }
    ft.commit()
}

fun FragmentManager.getTop(): Fragment? {
    val frgs = getFmFragments()
    if (frgs.isEmpty()) {
        return null
    }
    return frgs[frgs.size - 1]
}

/**
 * Return the fragments in manager.
 *
 * @return the fragments in manager
 */
fun FragmentManager.getFmFragments(): List<Fragment> {
    return this.fragments ?: emptyList()
}
