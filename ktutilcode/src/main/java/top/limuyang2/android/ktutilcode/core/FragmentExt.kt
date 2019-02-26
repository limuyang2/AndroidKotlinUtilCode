package top.limuyang2.android.ktutilcode.core

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction

/**
 * @author: limuyang
 * @date: 2019/2/26
 * @Description:
 */

fun Fragment.hide() {
    this.fragmentManager?.hide(this)
}

fun Fragment.show() {
    this.fragmentManager?.show(this)
}

fun Fragment.remove() {
    this.fragmentManager?.remove(this)
}

fun Fragment.showHide(vararg hideFragment: Fragment,
                      transaction: Int = FragmentTransaction.TRANSIT_NONE) {
    this.fragmentManager?.showHide(this, *hideFragment, transaction = transaction)
}

