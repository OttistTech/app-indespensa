package com.ottistech.indespensa.ui.recyclerview.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.TagFilterBinding
import com.ottistech.indespensa.ui.model.Filter

class FilterAdapter<T> (
    private val context: Context,
    filters: List<Filter<T>> = ArrayList(),
    private val isExclusive: Boolean,
    val onFilterSwitch: (item: Filter<T>) -> Unit
) : RecyclerView.Adapter<FilterAdapter<T>.FilterViewHolder>() {

    private var filtersList : MutableList<Filter<T>> = filters.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterAdapter<T>.FilterViewHolder {
        val itemView = TagFilterBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false)

        return FilterViewHolder(itemView)
    }

    override fun getItemCount(): Int = filtersList.size

    override fun onBindViewHolder(holder: FilterAdapter<T>.FilterViewHolder, position: Int) {
        val filterItem = filtersList[position]
        holder.bind(filterItem)
    }

    fun clearFilters() {
        filtersList.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }

    fun updateState(items: List<Filter<T>>) {
        filtersList.clear()
        filtersList.addAll(items)
        notifyDataSetChanged()
    }

    inner class FilterViewHolder(
        private val binding: TagFilterBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var filter: Filter<T>

        init {
            itemView.setOnClickListener {
                switchFilter()
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item: Filter<T>) {
            filter = item
            binding.tagFilter.apply {
                text = filter.text
                var backgroundDrawable = context.getDrawable(R.drawable.tag_filter_default)
                var color = context.getColor(R.color.pastelOrange)
                if(filter.isSelected) {
                    backgroundDrawable = context.getDrawable(R.drawable.tag_filter_selected)
                    color = context.getColor(R.color.white)
                }
                background = backgroundDrawable
                setTextColor(color)
            }
        }

        private fun switchFilter() {
            val position = filtersList.indexOf(filter)
            val newState = !filter.isSelected
            if(newState && isExclusive) {
                clearFilters()
            }
            filter.isSelected = newState
            filtersList[position].isSelected = newState
            notifyItemChanged(position, filter)
            onFilterSwitch(filter)
        }
    }
}
