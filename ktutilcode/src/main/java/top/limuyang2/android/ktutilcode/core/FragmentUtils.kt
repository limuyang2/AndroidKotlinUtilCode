package top.limuyang2.android.ktutilcode.core

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

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

/**
 * 隐藏fragment
 * @receiver FragmentManager
 * @param hideFragment Array<out Fragment> 需要隐藏的fragment
 */
fun FragmentManager.hide(vararg hideFragment: Fragment) {
    hide(hideFragment.toList())
}

/**
 * 隐藏fragment
 * @receiver FragmentManager
 * @param hideFragment List<Fragment>
 */
fun FragmentManager.hide(hideFragment: List<Fragment>) {
    val ft = this.beginTransaction()
    for (fragment in hideFragment) {
        ft.hide(fragment)
    }
    ft.commit()
}

/**
 * 显示fragment
 * @receiver FragmentManager
 * @param showFragment Fragment
 */
fun FragmentManager.show(showFragment: Fragment) {
    val ft = this.beginTransaction()
    ft.show(showFragment)
    ft.commit()
}

/**
 * 移除fragment
 * @receiver FragmentManager
 * @param removeFragment Array<out Fragment>
 */
fun FragmentManager.remove(vararg removeFragment: Fragment) {
    val ft = this.beginTransaction()
    for (fragment in removeFragment) {
        ft.remove(fragment)
    }
    ft.commit()
}

/**
 * 移除到指定 fragment
 * @receiver FragmentManager
 * @param removeTo Fragment 指定的fragment
 * @param isIncludeSelf Boolean 移除列表中，是否包含指定的fragment自己本身
 */
fun FragmentManager.removeTo(removeTo: Fragment, isIncludeSelf: Boolean = false) {
    val ft = this.beginTransaction()
    val fragments = this.fragments
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

/**
 * 移除全部fragment
 * @receiver FragmentManager
 */
fun FragmentManager.removeAll() {
    val frg = fragments
    if (frg.isEmpty()) return

    val ft = this.beginTransaction()
    for (fragment in frg) {
        ft.remove(fragment)
    }
    ft.commit()
}

/**
 * 现显示，再隐藏
 * @receiver FragmentManager
 * @param showFragment Fragment
 * @param hideFragment Array<out Fragment>
 * @param transaction Int 变换动画
 */
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

/**
 * 替换 fragment
 * @receiver FragmentManager
 * @param fragment Fragment 需要显示的fragment
 * @param containerId Int 容器控件id
 * @param isAddStack Boolean 是否添加到回退栈
 * @param tag String
 */
fun FragmentManager.replace(fragment: Fragment,
                            @IdRes containerId: Int,
                            isAddStack: Boolean = false,
                            tag: String = fragment::class.java.name) {
    val ft = this.beginTransaction()

    ft.replace(containerId, fragment, tag)
    if (isAddStack) ft.addToBackStack(tag)

    ft.commit()
}

/**
 * 切换 fragment
 * 显示指定的fragment，隐藏其他fragment。（如果需要显示的fragment未被添加过，则会先添加）
 * 适用场景：例如 主页多个fragment的切换
 * @receiver FragmentManager
 * @param showFragment Fragment 需要显示的fragment
 * @param containerId Int 容器控件id
 * @param transaction Int 变换动画
 */
fun FragmentManager.switch(showFragment: Fragment,
                           @IdRes containerId: Int,
                           transaction: Int = FragmentTransaction.TRANSIT_NONE) {
    val ft = this.beginTransaction().setTransition(transaction)

    val tag = showFragment::class.java.name + showFragment.hashCode()
    val fragmentByTag = this.findFragmentByTag(tag)
    if (fragmentByTag != null && fragmentByTag.isAdded) {
        ft.show(fragmentByTag)
    } else {
        ft.add(containerId, showFragment, tag)
    }

    for (tempF in this.fragments) {
        if (tempF != fragmentByTag) {
            ft.hide(tempF)
        }
    }
    ft.commit()
}

fun FragmentManager.getTop(): Fragment? {
    val frg = this.fragments
    return frg.ifEmpty { return null }[frg.size - 1]
}


inline fun <reified T : Fragment> FragmentManager.findFragment(): Fragment? {
    return this.findFragmentByTag(T::class.java.name)
}
