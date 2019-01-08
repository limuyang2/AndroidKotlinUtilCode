package top.limuyang2.android.ktutilcode.core

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText



fun EditText.onTextChange(
        before: ((s: CharSequence?, start: Int, count: Int, after: Int) -> Unit)? = null,
        change: ((s: CharSequence?, start: Int, before: Int, count: Int) -> Unit)? = null,
        after: ((s: Editable?) -> Unit)? = null
) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            after?.invoke(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            before?.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            change?.invoke(s, start, before, count)
        }
    })
}
