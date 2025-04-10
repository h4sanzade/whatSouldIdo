package com.materialdesign.whatsouldido

// MainActivity.kt

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainLayout: ConstraintLayout
    private lateinit var suggestionTextView: TextView
    private lateinit var generateButton: Button
    private lateinit var addButton: Button
    private lateinit var manageButton: Button
    private lateinit var emojiTextView: TextView
    private lateinit var countTextView: TextView

    private val suggestionsList = mutableListOf<String>()
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

    private val suggestionCounts = mutableMapOf<String, Int>()
    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI elemanlarını tanımla
        mainLayout = findViewById(R.id.mainLayout)
        suggestionTextView = findViewById(R.id.suggestionTextView)
        generateButton = findViewById(R.id.generateButton)
        addButton = findViewById(R.id.addButton)
        manageButton = findViewById(R.id.manageButton)
        emojiTextView = findViewById(R.id.emojiTextView)
        countTextView = findViewById(R.id.countTextView)

        // Kaydedilmiş önerileri yükle
        loadSuggestions()

        // Varsayılan öneriler ekle (eğer liste boşsa)
        if (suggestionsList.isEmpty()) {
            addDefaultSuggestions()
        }

        // Gece/gündüz modu kontrolü
        checkDayNightTheme()
        updateBackground()

        // Buton işlevlerini ayarla
        generateButton.setOnClickListener {
            generateRandomSuggestion()
        }

        addButton.setOnClickListener {
            showAddSuggestionDialog()
        }

        manageButton.setOnClickListener {
            showManageSuggestionsDialog()
        }
    }

    private fun loadSuggestions() {
        val sharedPrefs = getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val suggestionsJson = sharedPrefs.getString("suggestions", null)
        val countsJson = sharedPrefs.getString("counts", null)

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

    private fun saveSuggestions() {
        val sharedPrefs = getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        val jsonArray = JSONArray()
        for (suggestion in suggestionsList) {
            jsonArray.put(suggestion)
        }

        val countsArray = JSONArray()
        for ((suggestion, count) in suggestionCounts) {
            val item = JSONArray()
            item.put(suggestion)
            item.put(count)
            countsArray.put(item)
        }

        editor.putString("suggestions", jsonArray.toString())
        editor.putString("counts", countsArray.toString())
        editor.apply()
    }

    private fun addDefaultSuggestions() {
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
        saveSuggestions()
    }

    private fun generateRandomSuggestion() {
        if (suggestionsList.isEmpty()) {
            Toast.makeText(this, "Hiç öneri yok! Eklemen gerekiyor.", Toast.LENGTH_SHORT).show()
            return
        }

        val randomIndex = Random().nextInt(suggestionsList.size)
        val suggestion = suggestionsList[randomIndex]

        // Sayaçları güncelle
        val currentCount = suggestionCounts[suggestion] ?: 0
        suggestionCounts[suggestion] = currentCount + 1
        saveSuggestions()

        // Animasyonla öneriyi göster
        animateSuggestion(suggestion)

        // Emoji göster
        updateEmoji(suggestion)

        // Öneri sayısını güncelle
        updateCount(suggestion)
    }

    private fun animateSuggestion(suggestion: String) {
        // Önce kaybolma animasyonu
        val fadeOut = ObjectAnimator.ofFloat(suggestionTextView, "alpha", 1f, 0f)
        fadeOut.duration = 200

        // Sonra belirme animasyonu
        val fadeIn = ObjectAnimator.ofFloat(suggestionTextView, "alpha", 0f, 1f)
        fadeIn.duration = 400

        // Boyut animasyonu
        val scaleX = ObjectAnimator.ofFloat(suggestionTextView, "scaleX", 0.8f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(suggestionTextView, "scaleY", 0.8f, 1.2f, 1f)

        scaleX.duration = 500
        scaleY.duration = 500

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(fadeOut, fadeIn)

        val scaleSet = AnimatorSet()
        scaleSet.playTogether(scaleX, scaleY)

        fadeOut.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                suggestionTextView.text = suggestion
            }
        })

        animatorSet.start()
        scaleSet.start()
    }

    private fun updateEmoji(suggestion: String) {
        val emoji = getEmojiForSuggestion(suggestion)
        emojiTextView.text = emoji

        // Emoji için bounce animasyonu
        val scaleX = ObjectAnimator.ofFloat(emojiTextView, "scaleX", 1f, 1.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(emojiTextView, "scaleY", 1f, 1.5f, 1f)

        scaleX.duration = 500
        scaleY.duration = 500
        scaleX.interpolator = AccelerateDecelerateInterpolator()
        scaleY.interpolator = AccelerateDecelerateInterpolator()

        val bounceSet = AnimatorSet()
        bounceSet.playTogether(scaleX, scaleY)
        bounceSet.start()
    }

    private fun updateCount(suggestion: String) {
        val count = suggestionCounts[suggestion] ?: 0
        countTextView.text = "Bu öneri $count kez çıktı"

        // Sayaç için fade animasyonu
        val fadeIn = ObjectAnimator.ofFloat(countTextView, "alpha", 0f, 1f)
        fadeIn.duration = 500
        fadeIn.start()
    }

    private fun getEmojiForSuggestion(suggestion: String): String {
        return emojiMap[suggestion] ?: "❓"
    }

    private fun showAddSuggestionDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_suggestion, null)
        val editText = dialogView.findViewById<EditText>(R.id.suggestionEditText)

        AlertDialog.Builder(this)
            .setTitle("Öneri Ekle")
            .setView(dialogView)
            .setPositiveButton("Ekle") { _, _ ->
                val newSuggestion = editText.text.toString().trim()
                if (newSuggestion.isNotEmpty()) {
                    suggestionsList.add(newSuggestion)
                    saveSuggestions()
                    Toast.makeText(this, "Öneri eklendi!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun showManageSuggestionsDialog() {
        if (suggestionsList.isEmpty()) {
            Toast.makeText(this, "Yönetilecek öneri yok!", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_manage_suggestions, null)
        val listView = dialogView.findViewById<ListView>(R.id.suggestionsListView)

        val adapter = SuggestionsAdapter(this, suggestionsList)
        listView.adapter = adapter

        AlertDialog.Builder(this)
            .setTitle("Önerileri Yönet")
            .setView(dialogView)
            .setPositiveButton("Tamam", null)
            .show()
    }

    private fun checkDayNightTheme() {
        // Gerçek bir uygulamada burada sistem saatine göre kontrol yapılır
        // Bu örnekte basitçe rasgele değiştirelim (demo amaçlı)
        isDarkMode = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 18
    }

    private fun updateBackground() {
        val colors = if (isDarkMode) {
            intArrayOf(
                ContextCompat.getColor(this, R.color.darkStart),
                ContextCompat.getColor(this, R.color.darkEnd)
            )
        } else {
            intArrayOf(
                ContextCompat.getColor(this, R.color.lightStart),
                ContextCompat.getColor(this, R.color.lightEnd)
            )
        }

        val gradient = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
        mainLayout.background = gradient

        // Text renklerini de güncelle
        val textColor = if (isDarkMode) Color.WHITE else Color.BLACK
        suggestionTextView.setTextColor(textColor)
        countTextView.setTextColor(textColor)
    }

    inner class SuggestionsAdapter(
        context: Context,
        private val suggestions: MutableList<String>
    ) : ArrayAdapter<String>(context, R.layout.item_suggestion, suggestions) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_suggestion, parent, false)

            val suggestion = suggestions[position]
            val textView = view.findViewById<TextView>(R.id.suggestionItemText)
            val deleteButton = view.findViewById<ImageButton>(R.id.deleteButton)

            textView.text = suggestion

            deleteButton.setOnClickListener {
                suggestions.removeAt(position)
                notifyDataSetChanged()
                saveSuggestions()
            }

            return view
        }
    }
}