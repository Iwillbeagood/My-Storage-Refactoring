package com.example.mystorage.utils.etc

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.mystorage.R

object DialogUtils {
    fun showDialog(context: Context, message: String, btnA: String, btnB: String, onPositiveClick: () -> Unit, onNegativeClick: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
            .setPositiveButton(btnA) { dialog, which ->
                onPositiveClick()
            }
            .setNegativeButton(btnB) { dialog, which ->
                onNegativeClick()
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()

        // 버튼 색상 설정
        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)

        positiveButton.setTextColor(ContextCompat.getColor(context, R.color.main))
        negativeButton.setTextColor(ContextCompat.getColor(context, R.color.main))
    }

    fun showNoMessageDialog(context: Context, title: String, btnA: String, btnB: String, onPositiveClick: () -> Unit, onNegativeClick: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setPositiveButton(btnA) { dialog, which ->
                onPositiveClick()
            }
            .setNegativeButton(btnB) { dialog, which ->
                onNegativeClick()
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()

        // 버튼 색상 설정
        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)

        positiveButton.setTextColor(ContextCompat.getColor(context, R.color.main))
        negativeButton.setTextColor(ContextCompat.getColor(context, R.color.main))

        // 버튼 위치 및 크기 조정
        val layoutParams = positiveButton.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10F
        layoutParams.width = 200
        layoutParams.height = 200
        positiveButton.layoutParams = layoutParams
        negativeButton.layoutParams = layoutParams
    }
}
