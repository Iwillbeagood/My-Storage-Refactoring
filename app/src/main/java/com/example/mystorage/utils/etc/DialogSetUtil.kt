package com.example.mystorage.utils.etc

import android.app.Dialog
import android.view.ViewGroup
import com.example.mystorage.R

object DialogSetUtil {
    fun setDialogWindow(dialog: Dialog) {
        dialog.window?.setWindowAnimations(R.style.DialogAnimation)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
    }
}