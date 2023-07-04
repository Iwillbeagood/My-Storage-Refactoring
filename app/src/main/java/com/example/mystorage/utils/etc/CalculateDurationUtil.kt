package com.example.mystorage.utils.etc

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.sql.Timestamp
import java.util.*

object CalculateDurationUtil {
    fun calculateDuration(dateTime: Timestamp): String {
        val currentDate = Date()

        val durationInMillis = currentDate.time - dateTime.time
        val minutesInMillis = 60 * 1000
        val hoursInMillis = 60 * minutesInMillis
        val daysInMillis = 24 * hoursInMillis

        val days = durationInMillis / daysInMillis
        val hours = (durationInMillis - days * daysInMillis) / hoursInMillis

        return "물건 사용을 완료한지\n${days}일 ${hours}시간이 지났습니다"
    }
}

@BindingAdapter("formattedDuration")
fun TextView.setFormattedDuration(dateTime: Timestamp) {
    val duration = CalculateDurationUtil.calculateDuration(dateTime)
    text = duration
}
