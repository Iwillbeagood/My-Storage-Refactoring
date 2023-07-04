package com.example.mystorage.utils.etc

import android.text.Editable
import android.text.TextWatcher

object TextWatcherUtil {
    fun createTextWatcher(setter: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                setter(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }
}