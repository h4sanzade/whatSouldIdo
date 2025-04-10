package com.materialdesign.whatsouldido

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView

class SuggestionsAdapter(
    context: Context,
    private val suggestions: MutableList<String>,
    private val onDelete: (Int) -> Unit
) : ArrayAdapter<String>(context, R.layout.item_suggestion, suggestions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_suggestion, parent, false)

        val suggestion = suggestions[position]
        val textView = view.findViewById<TextView>(R.id.suggestionItemText)
        val deleteButton = view.findViewById<ImageButton>(R.id.deleteButton)

        textView.text = suggestion

        deleteButton.setOnClickListener {
            onDelete(position)
            notifyDataSetChanged()
        }

        return view
    }
}