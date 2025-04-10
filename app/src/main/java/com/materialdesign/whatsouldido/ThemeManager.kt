// ThemeManager.kt
package com.materialdesign.whatsouldido

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import java.util.*

class ThemeManager {
    var isDarkMode = false
    var currentTheme = "system" // system, light, dark, custom

    fun loadThemeSettings(context: Context) {
        val sharedPrefs = context.getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        currentTheme = sharedPrefs.getString("theme", "system") ?: "system"

        // Tema bilgisine göre karanlık modu ayarla
        when (currentTheme) {
            "system" -> checkDayNightTheme()
            "light" -> isDarkMode = false
            "dark" -> isDarkMode = true
            "custom" -> isDarkMode = sharedPrefs.getBoolean("customIsDark", false)
        }
    }

    fun saveThemeSettings(context: Context) {
        val sharedPrefs = context.getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("theme", currentTheme)
        editor.putBoolean("customIsDark", isDarkMode)
        editor.apply()
    }

    fun checkDayNightTheme() {
        // Gerçek bir uygulamada burada sistem saatine göre kontrol yapılır
        isDarkMode = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 18
    }

    fun setTheme(themeName: String, context: Context) {
        currentTheme = themeName

        when (themeName) {
            "system" -> checkDayNightTheme()
            "light" -> isDarkMode = false
            "dark" -> isDarkMode = true
            // custom tema için isDarkMode değeri dışarıdan atanacak
        }

        saveThemeSettings(context)
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