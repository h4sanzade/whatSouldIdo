// SuggestionManager.kt içinde yeni değişkenler ve fonksiyonlar
private val categories = mutableListOf<Category>()
private val suggestionCategories = mutableMapOf<String, String>() // suggestion -> categoryId

fun getCategories(): List<Category> = categories

fun addCategory(category: Category) {
    categories.add(category)
}

fun getCategoryForSuggestion(suggestion: String): Category? {
    val categoryId = suggestionCategories[suggestion] ?: return null
    return categories.find { it.id == categoryId }
}

fun setSuggestionCategory(suggestion: String, categoryId: String) {
    suggestionCategories[suggestion] = categoryId
}

// loadSuggestions ve saveSuggestions'u güncelleyelim
fun loadSuggestions(context: Context) {
    // Mevcut kodlar...

    // Kategorileri yükle
    val categoriesJson = sharedPrefs.getString("categories", null)
    val suggestionCategoriesJson = sharedPrefs.getString("suggestionCategories", null)

    categories.clear()

    if (categoriesJson != null) {
        try {
            val jsonArray = JSONArray(categoriesJson)
            for (i in 0 until jsonArray.length()) {
                val catObj = jsonArray.getJSONObject(i)
                categories.add(Category(
                    id = catObj.getString("id"),
                    name = catObj.getString("name"),
                    color = catObj.getInt("color"),
                    emoji = catObj.getString("emoji")
                ))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    if (suggestionCategoriesJson != null) {
        try {
            val jsonObj = JSONObject(suggestionCategoriesJson)
            val keys = jsonObj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                suggestionCategories[key] = jsonObj.getString(key)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    // Eğer kategori yoksa varsayılanları ekleyelim
    if (categories.isEmpty()) {
        categories.addAll(Category.getDefaultCategories())
    }
}

fun saveSuggestions(context: Context) {
    // Mevcut kodlar...

    // Kategorileri kaydet
    val categoriesArray = JSONArray()
    for (category in categories) {
        val catObj = JSONObject()
        catObj.put("id", category.id)
        catObj.put("name", category.name)
        catObj.put("color", category.color)
        catObj.put("emoji", category.emoji)
        categoriesArray.put(catObj)
    }

    val suggestionCategoriesObj = JSONObject()
    for ((suggestion, categoryId) in suggestionCategories) {
        suggestionCategoriesObj.put(suggestion, categoryId)
    }

    editor.putString("categories", categoriesArray.toString())
    editor.putString("suggestionCategories", suggestionCategoriesObj.toString())
    editor.apply()
}

// DefaultSuggestions fonksiyonunu güncelleyelim
fun addDefaultSuggestions() {
    val defaults = mapOf(
        // Eğlence kategorisi önerileri
        "Film izle" to "Eğlence",
        "Müzik dinle" to "Eğlence",
        "Bilgisayar oyunu oyna" to "Eğlence",
        "Bulmaca çöz" to "Eğlence",

        // Sağlık kategorisi önerileri
        "Yürüyüş yap" to "Sağlık",
        "Meditasyon yap" to "Sağlık",
        "Spor yap" to "Sağlık",
        "Sağlıklı bir öğün hazırla" to "Sağlık",

        // Üretkenlik kategorisi önerileri
        "Kitap oku" to "Üretkenlik",
        "Yeni bir beceri öğren" to "Üretkenlik",
        "To-do listeni güncelle" to "Üretkenlik",
        "Çalışma alanını düzenle" to "Üretkenlik",

        // Sosyal kategorisi önerileri
        "Arkadaşını ara" to "Sosyal",
        "Aile ile vakit geçir" to "Sosyal",
        "Dışarı çık" to "Sosyal",
        "Sosyal medyada paylaşım yap" to "Sosyal"
    )

    for ((suggestion, categoryName) in defaults) {
        suggestionsList.add(suggestion)
        val category = categories.find { it.name == categoryName }
        category?.let {
            suggestionCategories[suggestion] = it.id
        }
    }
}