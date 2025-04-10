package com.materialdesign.whatsouldido

import android.graphics.Color
import java.util.UUID

data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: Int,
    val emoji: String
) {
    companion object {
        fun getDefaultCategories(): List<Category> {
            return listOf(
                Category(name = "Eğlence", color = Color.parseColor("#FF5252"), emoji = "🎭"),
                Category(name = "Sağlık", color = Color.parseColor("#4CAF50"), emoji = "💪"),
                Category(name = "Üretkenlik", color = Color.parseColor("#2196F3"), emoji = "📝"),
                Category(name = "Sosyal", color = Color.parseColor("#FF9800"), emoji = "👥")
            )
        }
    }
}