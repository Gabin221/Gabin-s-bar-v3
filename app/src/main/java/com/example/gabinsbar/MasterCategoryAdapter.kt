package com.example.gabinsbar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MasterCategoryAdapter(private var categories: List<Category>, private val onItemClick: (Element) -> Unit) :
    RecyclerView.Adapter<MasterCategoryAdapter.MasterCategoryViewHolder>() {

    class MasterCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.categoryTitle)
        val horizontalRecyclerView: RecyclerView = view.findViewById(R.id.horizontalRecyclerView)
        val refreshButton: ImageButton = view.findViewById(R.id.refreshSuggestionsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasterCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return MasterCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MasterCategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.title.text = if (category.title != "Suggestions") {
            "${category.title} (${category.elements.size})"
        } else {
            category.title
        }
        holder.horizontalRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context, RecyclerView.HORIZONTAL, false)

        val adapter = when (category.title) {
            "Sirops" -> SiropsAdapter(category.elements, onItemClick)
            "Classiques" -> ClassiquesAdapter(category.elements, onItemClick)
            "Extravagants" -> ExtravagantsAdapter(category.elements, onItemClick)
            "Bières" -> BieresAdapter(category.elements, onItemClick)
            "Cafés" -> CafesAdapter(category.elements, onItemClick)
            "Thés" -> ThesAdapter(category.elements, onItemClick)
            "Softs" -> SoftsAdapter(category.elements, onItemClick)
            else -> ElementAdapter(category.elements, onItemClick)
        }

        holder.horizontalRecyclerView.adapter = adapter

        if (category.title.trim() == "Suggestions") {
            holder.refreshButton.visibility = View.VISIBLE
            holder.refreshButton.setOnClickListener {
                (holder.itemView.context as? MainActivity)?.refreshSuggestions()
            }
        } else {
            holder.refreshButton.visibility = View.GONE
            holder.refreshButton.setOnClickListener(null)
        }
    }

    fun updateCategories(newCategories: List<Category>) {
        this.categories = newCategories
        notifyDataSetChanged()
    }

    override fun getItemCount() = categories.size
}
