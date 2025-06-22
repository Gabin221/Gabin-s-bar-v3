package com.example.gabinsbar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CafesAdapter(private val elements: List<Element>, private val onItemClick: (Element) -> Unit) :
    RecyclerView.Adapter<CafesAdapter.CafesViewHolder>() {

    class CafesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.elementImage)
        val name: TextView = view.findViewById(R.id.elementName)
        val info: TextView = view.findViewById(R.id.elementCalories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_element, parent, false)
        return CafesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CafesViewHolder, position: Int) {
        val element = elements[position]
        holder.name.text = element.name
        holder.info.text = element.extraInfo
        Glide.with(holder.itemView).load(element.imageUrl).into(holder.image)
        holder.itemView.setOnClickListener {
            onItemClick(element)
        }
    }

    override fun getItemCount() = elements.size
}
