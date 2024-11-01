package com.ottistech.indespensa.ui.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.databinding.ItemTextCarouselBinding
import com.ottistech.indespensa.ui.CarrouselPage

class TextCarouselAdapter(
    private val items: List<String> = emptyList(),
    private val carrouselPage: List<CarrouselPage> = emptyList()
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