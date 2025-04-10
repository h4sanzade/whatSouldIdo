import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.Toast

// MainActivity.kt içinde yeni fonksiyonlar ve değişkenler

private lateinit var categoryChipGroup: ChipGroup
private lateinit var favoriteButton: ImageButton
private lateinit var settingsButton: ImageButton

private val favorites = mutableSetOf<String>()

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    initViews()
    loadData()
    setupClickListeners()
    setupCategoryChips()
    loadFavorites()

    // Tema ayarlarını yükle
    themeManager.loadThemeSettings(this)
    updateTheme()
}

private fun initViews() {
    // Mevcut kodlar...
    categoryChipGroup = findViewById(R.id.categoryChipGroup)
    favoriteButton = findViewById(R.id.favoriteButton)
    settingsButton = findViewById(R.id.settingsButton)
}

private fun setupCategoryChips() {
    categoryChipGroup.removeAllViews()

    // Tüm kategoriler için chip
    val allChip = Chip(this)
    allChip.text = "Tümü"
    allChip.isCheckable = true
    allChip.isChecked = true
    allChip.setChipBackgroundColorResource(R.color.colorPrimary)
    categoryChipGroup.addView(allChip)

    // Diğer kategoriler için chip'ler
    for (category in suggestionManager.getCategories()) {
        val chip = Chip(this)
        chip.text = "${category.emoji} ${category.name}"
        chip.isCheckable = true
        chip.chipBackgroundColor = ColorStateList.valueOf(category.color)
        categoryChipGroup.addView(chip)
    }

    // Chip seçildiğinde filtreleme yapılsın
    categoryChipGroup.setOnCheckedChangeListener { group, checkedId ->
        // Kategori filtrelemesi yapacağız
    }
}

private fun setupClickListeners() {
    // Mevcut kodlar...

    favoriteButton.setOnClickListener {
        toggleFavorite(suggestionTextView.text.toString())
    }

    settingsButton.setOnClickListener {
        showSettingsDialog()
    }
}

private fun generateRandomSuggestion() {
    // Mevcut kodlar...

    // Favori durumunu güncelle
    updateFavoriteButton(suggestion)
}

private fun toggleFavorite(suggestion: String) {
    if (suggestion.isEmpty() || suggestion == "Öneri için butona tıkla!") return

    if (favorites.contains(suggestion)) {
        favorites.remove(suggestion)
        favoriteButton.setImageResource(android.R.drawable.btn_star_big_off)
    } else {
        favorites.add(suggestion)
        favoriteButton.setImageResource(android.R.drawable.btn_star_big_on)

        // Favoriye eklendiğinde animasyon
        val scaleX = ObjectAnimator.ofFloat(favoriteButton, "scaleX", 1f, 1.3f, 1f)
        val scaleY = ObjectAnimator.ofFloat(favoriteButton, "scaleY", 1f, 1.3f, 1f)
        scaleX.duration = 300
        scaleY.duration = 300
        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            start()
        }
    }

    saveFavorites()
}

private fun updateFavoriteButton(suggestion: String) {
    if (favorites.contains(suggestion)) {
        favoriteButton.setImageResource(android.R.drawable.btn_star_big_on)
    } else {
        favoriteButton.setImageResource(android.R.drawable.btn_star_big_off)
    }
}

private fun loadFavorites() {
    val sharedPrefs = getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
    val favoritesSet = sharedPrefs.getStringSet("favorites", null)
    favorites.clear()
    if (favoritesSet != null) {
        favorites.addAll(favoritesSet)
    }
}

private fun saveFavorites() {
    val sharedPrefs = getSharedPreferences("NeYapsamPrefs", Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()
    editor.putStringSet("favorites", favorites)
    editor.apply()
}

private fun showSettingsDialog() {
    val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null)

    val themeRadioGroup = dialogView.findViewById<RadioGroup>(R.id.themeRadioGroup)
    val statsButton = dialogView.findViewById<Button>(R.id.statisticsButton)
    val favoritesButton = dialogView.findViewById<Button>(R.id.favoritesButton)

    // Mevcut tema seçeneğini işaretle
    when (themeManager.currentTheme) {
        "system" -> themeRadioGroup.check(R.id.radioSystem)
        "light" -> themeRadioGroup.check(R.id.radioLight)
        "dark" -> themeRadioGroup.check(R.id.radioDark)
        "custom" -> themeRadioGroup.check(R.id.radioCustom)
    }

    val dialog = AlertDialog.Builder(this)
        .setTitle("Ayarlar")
        .setView(dialogView)
        .setPositiveButton("Tamam") { _, _ ->
            // Tema seçimini kaydet
            val selectedId = themeRadioGroup.checkedRadioButtonId
            val selectedTheme = when (selectedId) {
                R.id.radioSystem -> "system"
                R.id.radioLight -> "light"
                R.id.radioDark -> "dark"
                R.id.radioCustom -> "custom"
                else -> "system"
            }

            if (selectedTheme != themeManager.currentTheme) {
                themeManager.setTheme(selectedTheme, this)
                updateTheme()
            }
        }
        .setNegativeButton("İptal", null)
        .create()

    statsButton.setOnClickListener {
        dialog.dismiss()
        showStatisticsActivity()
    }

    favoritesButton.setOnClickListener {
        dialog.dismiss()
        showFavoritesDialog()
    }

    dialog.show()
}

private fun showStatisticsActivity() {
    // Buraya istatistik aktivitesine geçiş kodu gelecek
    val intent = Intent(this, StatisticsActivity::class.java)
    startActivity(intent)
}

private fun showFavoritesDialog() {
    if (favorites.isEmpty()) {
        Toast.makeText(this, "Henüz favori önerin yok!", Toast.LENGTH_SHORT).show()
        return
    }

    val favoritesList = favorites.toList()
    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, favoritesList)

    AlertDialog.Builder(this)
        .setTitle("Favori Önerilerim")
        .setAdapter(adapter) { _, position ->
            val selectedSuggestion = favoritesList[position]
            animationManager.animateSuggestion(suggestionTextView, selectedSuggestion)
            updateEmoji(selectedSuggestion)
            updateCount(selectedSuggestion)
            updateFavoriteButton(selectedSuggestion)
        }
        .setPositiveButton("Tamam", null)
        .show()
}