package top.limuyang2.android.ktutilcode.widget

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


inline fun EditText.onTextChange(
        textWatcher: TextWatcherDsl.() -> Unit
) {
    addTextChangedListener(top.limuyang2.android.ktutilcode.widget.TextWatcherDsl().apply(textWatcher))
}

class TextWatcherDsl : TextWatcher {
    private var _afterTextChanged: ((s: Editable?) -> Unit)? = null
    private var _beforeTextChanged: ((s: CharSequence?, start: Int, count: Int, after: Int) -> Unit)? = null
    private var _onTextChanged: ((s: CharSequence?, start: Int, before: Int, count: Int) -> Unit)? = null

    override fun afterTextChanged(s: Editable?) {
        _afterTextChanged?.invoke(s)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        _beforeTextChanged?.invoke(s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        _onTextChanged?.invoke(s, start, before, count)
    }

    fun afterTextChanged(after: (s: Editable?) -> Unit) {
        _afterTextChanged = after
    }

    fun beforeTextChanged(before: (s: CharSequence?, start: Int, count: Int, after: Int) -> Unit) {
        _beforeTextChanged = before
    }

    fun onTextChanged(onChanged: (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit) {
        _onTextChanged = onChanged
    }
}

