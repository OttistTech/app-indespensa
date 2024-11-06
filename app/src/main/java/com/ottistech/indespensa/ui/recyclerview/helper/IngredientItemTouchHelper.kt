package com.ottistech.indespensa.ui.recyclerview.helper

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.ui.recyclerview.adapter.IngredientAdapter

val ingredientItemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val recyclerView = viewHolder.itemView.parent as RecyclerView
        val adapter = recyclerView.adapter as IngredientAdapter
        val position = viewHolder.adapterPosition
        adapter.removeItem(position)
    }
}