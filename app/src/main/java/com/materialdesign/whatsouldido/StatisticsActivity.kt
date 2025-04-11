package com.materialdesign.whatsouldido

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import org.json.JSONObject
import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.cardview.widget.CardView
import org.json.JSONException
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    private lateinit var mainLayout: ConstraintLayout
    private lateinit var statsContainer: LinearLayout
    private lateinit var themeManager: ThemeManager
    private val suggestionCounts = mutableMapOf<String, Int>()
    private val categoryUsage = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        themeManager = ThemeManager()
        themeManager.loadThemeSettings(this)

        initViews()
        loadData()
        updateTheme()
        displayStatistics()
    }

    private fun initViews() {
        mainLayout = findViewById(R.id.statsMainLayout)
        statsContainer = findViewById(R.id.statsContainer)
    }

    private fun loadData() {
        loadCounts()
        calculateCategoryUsage()
    }

    private fun loadCounts() {
        val sharedPrefs = getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val countsJson = sharedPrefs.getString("counts", null)
        suggestionCounts.clear()

        if (countsJson != null) {
            try {
                val jsonObj = JSONObject(countsJson)
                val keys = jsonObj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    suggestionCounts[key] = jsonObj.getInt(key)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun calculateCategoryUsage() {
        categoryUsage.clear()

        val suggestionManager = SuggestionManager()
        suggestionManager.loadSuggestions(this)

        // Her bir öneri için kategori kullanımını hesapla
        for ((suggestion, count) in suggestionCounts) {
            val category = suggestionManager.getCategoryForSuggestion(suggestion)
            if (category != null) {
                val categoryName = category.name
                categoryUsage[categoryName] = categoryUsage.getOrDefault(categoryName, 0) + count
            }
        }
    }

    private fun updateTheme() {
        // Arka plan rengini ayarla
        val backgroundColor = if (themeManager.isDarkMode) {
            ContextCompat.getColor(this, R.color.darkStart)
        } else {
            ContextCompat.getColor(this, R.color.lightStart)
        }
        mainLayout.setBackgroundColor(backgroundColor)
    }

    private fun displayStatistics() {
        // Mevcut istatistik görünümlerini temizle
        statsContainer.removeAllViews()

        // Kategori kullanım istatistiklerini göster
        addCategoryStatistics()

        // En çok kullanılan önerileri göster
        addTopSuggestionsStatistics()

        // Genel istatistikleri göster
        addGeneralStatistics()
    }

    private fun addCategoryStatistics() {
        if (categoryUsage.isEmpty()) return

        val categoryTitle = TextView(this)
        categoryTitle.text = "Kategori Kullanımı"
        categoryTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        categoryTitle.setTypeface(null, Typeface.BOLD)
        categoryTitle.setTextColor(if (themeManager.isDarkMode) Color.WHITE else Color.BLACK)
        categoryTitle.setPadding(0, 32, 0, 16)
        statsContainer.addView(categoryTitle)


        val tableLayout = TableLayout(this)
        tableLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tableLayout.setPadding(16, 16, 16, 16)
        tableLayout.stretchAllColumns = true

        // Tablo başlık satırı
        val headerRow = TableRow(this)
        val headerCategory = createTableHeaderCell("Kategori")
        val headerCount = createTableHeaderCell("Kullanım Sayısı")
        val headerPercentage = createTableHeaderCell("Yüzde")
        headerRow.addView(headerCategory)
        headerRow.addView(headerCount)
        headerRow.addView(headerPercentage)
        tableLayout.addView(headerRow)

        // Toplam kullanımı hesapla
        val totalUsage = categoryUsage.values.sum()

        // Her kategori için satır ekle
        for ((category, count) in categoryUsage.toList().sortedByDescending { it.second }) {
            val percentage = if (totalUsage > 0) (count.toFloat() / totalUsage) * 100 else 0f

            val row = TableRow(this)
            val categoryCell = createTableCell(category)
            val countCell = createTableCell(count.toString())
            val percentageCell = createTableCell("%.1f%%".format(percentage))

            row.addView(categoryCell)
            row.addView(countCell)
            row.addView(percentageCell)
            tableLayout.addView(row)
        }

        // Tablo için card view
        val cardView = CardView(this)
        cardView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        cardView.radius = 16f
        cardView.cardElevation = 8f
        cardView.setContentPadding(8, 8, 8, 8)
        cardView.addView(tableLayout)

        statsContainer.addView(cardView)
    }

    private fun addTopSuggestionsStatistics() {
        if (suggestionCounts.isEmpty()) return

        // En çok kullanılan 5 öneriyi al
        val topSuggestions = suggestionCounts.toList()
            .sortedByDescending { it.second }
            .take(5)

        val topTitle = TextView(this)
        topTitle.text = "En Çok Kullanılan Öneriler"
        topTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        topTitle.setTypeface(null, Typeface.BOLD)
        topTitle.setTextColor(if (themeManager.isDarkMode) Color.WHITE else Color.BLACK)
        topTitle.setPadding(0, 32, 0, 16)
        statsContainer.addView(topTitle)

        // Her öneri için bir kart oluştur
        for ((suggestion, count) in topSuggestions) {
            val suggestionCard = CardView(this)
            suggestionCard.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            suggestionCard.radius = 16f
            suggestionCard.cardElevation = 4f
            (suggestionCard.layoutParams as LinearLayout.LayoutParams).setMargins(0, 8, 0, 8)

            val suggestionLayout = LinearLayout(this)
            suggestionLayout.orientation = LinearLayout.HORIZONTAL
            suggestionLayout.setPadding(16, 16, 16, 16)
            suggestionLayout.gravity = Gravity.CENTER_VERTICAL

            val suggestionName = TextView(this)
            suggestionName.text = suggestion
            suggestionName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            suggestionName.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val suggestionCount = TextView(this)
            suggestionCount.text = "$count kez"
            suggestionCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            suggestionCount.gravity = Gravity.END

            suggestionLayout.addView(suggestionName)
            suggestionLayout.addView(suggestionCount)
            suggestionCard.addView(suggestionLayout)

            statsContainer.addView(suggestionCard)
        }
    }

    private fun addGeneralStatistics() {
        val generalTitle = TextView(this)
        generalTitle.text = "Genel İstatistikler"
        generalTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        generalTitle.setTypeface(null, Typeface.BOLD)
        generalTitle.setTextColor(if (themeManager.isDarkMode) Color.WHITE else Color.BLACK)
        generalTitle.setPadding(0, 32, 0, 16)
        statsContainer.addView(generalTitle)

        val generalCard = CardView(this)
        generalCard.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        generalCard.radius = 16f
        generalCard.cardElevation = 8f

        val generalLayout = LinearLayout(this)
        generalLayout.orientation = LinearLayout.VERTICAL
        generalLayout.setPadding(16, 16, 16, 16)

        // Toplam kullanım sayısı
        val totalUsageCount = suggestionCounts.values.sum()
        val totalUsageItem = createStatItem("Toplam Öneri Kullanımı", totalUsageCount.toString())
        generalLayout.addView(totalUsageItem)

        // Toplam öneri sayısı
        val sharedPrefs = getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
        val suggestionsJson = sharedPrefs.getString("suggestions", null)
        val suggestionsCount = if (suggestionsJson != null) {
            try {
                JSONObject(suggestionsJson).getJSONArray("suggestions").length()
            } catch (e: JSONException) {
                0
            }
        } else {
            0
        }
        val totalSuggestionsItem = createStatItem("Toplam Öneri Sayısı", suggestionsCount.toString())
        generalLayout.addView(totalSuggestionsItem)

        // Son kullanım tarihi
        val lastUsage = sharedPrefs.getLong("lastUsage", 0)
        val lastUsageText = if (lastUsage > 0) {
            val date = Date(lastUsage)
            val dateFormat = android.text.format.DateFormat.getDateFormat(this)
            dateFormat.format(date)
        } else {
            "Hiç kullanılmadı"
        }
        val lastUsageItem = createStatItem("Son Kullanım", lastUsageText)
        generalLayout.addView(lastUsageItem)

        generalCard.addView(generalLayout)
        statsContainer.addView(generalCard)
    }

    private fun createStatItem(title: String, value: String): LinearLayout {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.HORIZONTAL
        layout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layout.setPadding(0, 8, 0, 8)

        val titleText = TextView(this)
        titleText.text = title
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        titleText.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        val valueText = TextView(this)
        valueText.text = value
        valueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        valueText.setTypeface(null, Typeface.BOLD)
        valueText.gravity = Gravity.END

        layout.addView(titleText)
        layout.addView(valueText)

        return layout
    }

    private fun createTableHeaderCell(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.setTypeface(null, Typeface.BOLD)
        textView.gravity = Gravity.CENTER
        textView.setPadding(8, 8, 8, 8)
        textView.setTextColor(if (themeManager.isDarkMode) Color.WHITE else Color.BLACK)
        return textView
    }

    private fun createTableCell(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.gravity = Gravity.CENTER
        textView.setPadding(8, 8, 8, 8)
        textView.setTextColor(if (themeManager.isDarkMode) Color.WHITE else Color.BLACK)
        return textView
    }
}