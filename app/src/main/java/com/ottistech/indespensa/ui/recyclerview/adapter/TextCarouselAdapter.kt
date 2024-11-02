package com.ottistech.indespensa.ui.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.databinding.ItemTextCarouselBinding

class TextCarouselAdapter(
    private val items: List<String>
) : RecyclerView.Adapter<TextCarouselAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTextCarouselBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.itemTextCarouselText.text = items[position]
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(
        val binding: ItemTextCarouselBinding
    ) : RecyclerView.ViewHolder(binding.root)
}