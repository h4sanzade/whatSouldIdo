package com.materialdesign.whatsouldido

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SuggestionManager {
    private val suggestionsList = mutableListOf<String>()
    private val suggestionCounts = mutableMapOf<String, Int>()

    private val emojiMap = mapOf(
        "Film izle" to "\uD83C\uDFAC",
        "Kitap oku" to "\uD83D\uDCDA",
        "Yürüyüş yap" to "\uD83D\uDEB6",
        "Müzik dinle" to "\uD83C\uDFB6",
        "Uyu" to "\uD83D\uDE34",
        "Yeni bir yemek tarifi dene" to "\uD83C\uDF72",
        "Arkadaşını ara" to "\uD83D\uDCDE",
        "Meditasyon yap" to "\uD83E\uDD2C",
        "Dışarı çık" to "\uD83D\uDEAB",
        "Alışveriş yap" to "\uD83D\uDED2"
    )

    fun getSuggestions(): MutableList<String> = suggestionsList

    fun getSuggestionCount(suggestion: String): Int = suggestionCounts[suggestion] ?: 0

    fun getRandomSuggestion(): String {
        val randomIndex = Random().nextInt(suggestionsList.size)
        return suggestionsList[randomIndex]
    }

    fun getEmojiForSuggestion(suggestion: String): String {
        return emojiMap[suggestion] ?: "❓"
    }

    fun addSuggestion(suggestion: String) {
        suggestionsList.add(suggestion)
    }

    fun removeSuggestion(position: Int) {
        if (position >= 0 && position < suggestionsList.size) {
            suggestionsList.removeAt(position)
        }
    }

    fun incrementSuggestionCount(suggestion: String) {
        val currentCount = suggestionCounts[suggestion] ?: 0
        suggestionCounts[suggestion] = currentCount + 1
    }

    fun loadSuggestions(context: Context) {
        val sharedPrefs = context.getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val suggestionsJson = sharedPrefs.getString("suggestions", null)
        val countsJson = sharedPrefs.getString("counts", null)

        suggestionsList.clear()
        suggestionCounts.clear()

        if (suggestionsJson != null) {
            try {
                val jsonArray = JSONArray(suggestionsJson)
                for (i in 0 until jsonArray.length()) {
                    suggestionsList.add(jsonArray.getString(i))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        if (countsJson != null) {
            try {
                val jsonArray = JSONArray(countsJson)
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    suggestionCounts[item.getString("suggestion")] = item.getInt("count")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun saveSuggestions(context: Context) {
        val sharedPrefs = context.getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        val jsonArray = JSONArray()
        for (suggestion in suggestionsList) {
            jsonArray.put(suggestion)
        }

        val countsArray = JSONArray()
        for ((suggestion, count) in suggestionCounts) {
            val countObj = JSONObject()
            countObj.put("suggestion", suggestion)
            countObj.put("count", count)
            countsArray.put(countObj)
        }

        editor.putString("suggestions", jsonArray.toString())
        editor.putString("counts", countsArray.toString())
        editor.apply()
    }

    fun addDefaultSuggestions() {
        val defaults = listOf(
            "Film izle",
            "Kitap oku",
            "Yürüyüş yap",
            "Müzik dinle",
            "Uyu",
            "Yeni bir yemek tarifi dene",
            "Arkadaşını ara",
            "Meditasyon yap",
            "Dışarı çık",
            "Alışveriş yap"
        )

        suggestionsList.addAll(defaults)
    }
}