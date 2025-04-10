package com.materialdesign.whatsouldido

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainLayout: ConstraintLayout
    private lateinit var suggestionTextView: TextView
    private lateinit var generateButton: Button
    private lateinit var addButton: Button
    private lateinit var manageButton: Button
    private lateinit var emojiTextView: TextView
    private lateinit var countTextView: TextView

    private val suggestionManager = SuggestionManager()
    private val animationManager = AnimationManager()
    private val themeManager = ThemeManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        loadData()
        setupClickListeners()
        updateTheme()
    }

    private fun initViews() {
        mainLayout = findViewById(R.id.mainLayout)
        suggestionTextView = findViewById(R.id.suggestionTextView)
        generateButton = findViewById(R.id.generateButton)
        addButton = findViewById(R.id.addButton)
        manageButton = findViewById(R.id.manageButton)
        emojiTextView = findViewById(R.id.emojiTextView)
        countTextView = findViewById(R.id.countTextView)
    }

    private fun loadData() {
        suggestionManager.loadSuggestions(this)
        if (suggestionManager.getSuggestions().isEmpty()) {
            suggestionManager.addDefaultSuggestions()
            suggestionManager.saveSuggestions(this)
        }
    }

    private fun setupClickListeners() {
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

    private fun updateTheme() {
        themeManager.checkDayNightTheme()
        themeManager.updateBackground(this, mainLayout, suggestionTextView, countTextView)
    }

    private fun generateRandomSuggestion() {
        if (suggestionManager.getSuggestions().isEmpty()) {
            Toast.makeText(this, "Hiç öneri yok! Eklemen gerekiyor.", Toast.LENGTH_SHORT).show()
            return
        }

        val suggestion = suggestionManager.getRandomSuggestion()
        suggestionManager.incrementSuggestionCount(suggestion)
        suggestionManager.saveSuggestions(this)

        animationManager.animateSuggestion(suggestionTextView, suggestion)
        updateEmoji(suggestion)
        updateCount(suggestion)
    }

    private fun updateEmoji(suggestion: String) {
        val emoji = suggestionManager.getEmojiForSuggestion(suggestion)
        emojiTextView.text = emoji
        animationManager.animateEmoji(emojiTextView)
    }

    private fun updateCount(suggestion: String) {
        val count = suggestionManager.getSuggestionCount(suggestion)
        countTextView.text = "Bu öneri $count kez çıktı"

        val fadeIn = ObjectAnimator.ofFloat(countTextView, "alpha", 0f, 1f)
        fadeIn.duration = 500
        fadeIn.start()
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
                    suggestionManager.addSuggestion(newSuggestion)
                    suggestionManager.saveSuggestions(this)
                    Toast.makeText(this, "Öneri eklendi!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun showManageSuggestionsDialog() {
        if (suggestionManager.getSuggestions().isEmpty()) {
            Toast.makeText(this, "Yönetilecek öneri yok!", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_manage_suggestions, null)
        val listView = dialogView.findViewById<ListView>(R.id.suggestionsListView)

        val adapter = SuggestionsAdapter(
            this,
            suggestionManager.getSuggestions(),
            onDelete = { position ->
                suggestionManager.removeSuggestion(position)
                suggestionManager.saveSuggestions(this)
            }
        )
        listView.adapter = adapter

        AlertDialog.Builder(this)
            .setTitle("Önerileri Yönet")
            .setView(dialogView)
            .setPositiveButton("Tamam", null)
            .show()
    }
}