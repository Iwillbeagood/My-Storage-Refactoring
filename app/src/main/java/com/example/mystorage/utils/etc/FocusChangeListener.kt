package com.example.mystorage.utils.etc

import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.mystorage.R

object FocusChangeListener {
    fun setEditTextFocusChangeListener(editText: EditText, container: ConstraintLayout) {
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                container.setBackgroundResource(R.drawable.brownbolder)
                editText.setHintTextColor(ContextCompat.getColor(editText.context, R.color.main))
            } else {
                container.setBackgroundResource(R.drawable.bolder)
                editText.setHintTextColor(ContextCompat.getColor(editText.context, R.color.gray))
            }
        }
    }
}