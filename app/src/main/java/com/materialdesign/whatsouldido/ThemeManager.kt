package com.materialdesign.whatsouldido

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import java.util.*

class ThemeManager {
    var isDarkMode = false

    fun checkDayNightTheme() {
        // Gerçek bir uygulamada burada sistem saatine göre kontrol yapılır
        // Bu örnekte basitçe saat kontrolü yapalım
        isDarkMode = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 18
    }

    fun updateBackground(
        context: Context,
        layout: ConstraintLayout,
        suggestionTextView: TextView,
        countTextView: TextView
    ) {
        val colors = if (isDarkMode) {
            intArrayOf(
                ContextCompat.getColor(context, R.color.darkStart),
                ContextCompat.getColor(context, R.color.darkEnd)
            )
        } else {
            intArrayOf(
                ContextCompat.getColor(context, R.color.lightStart),
                ContextCompat.getColor(context, R.color.lightEnd)
            )
        }

        val gradient = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
        layout.background = gradient


        val textColor = if (isDarkMode) Color.WHITE else Color.BLACK
        suggestionTextView.setTextColor(textColor)
        countTextView.setTextColor(textColor)
    }
}