package com.ottistech.indespensa.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.databinding.CardCloseValidityBinding
import com.ottistech.indespensa.shared.formatAsString
import com.ottistech.indespensa.shared.renderAmount
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCloseValidityDTO

class CloseValidityAdapter(
    private val context: Context,
    closeValidityItemsList: List<PantryItemCloseValidityDTO> = ArrayList(),
    val onItemClick: (itemId: Long) -> Unit
) : RecyclerView.Adapter<CloseValidityAdapter.CloseValidityViewHolder>(){

    private var pantryItemsList : MutableList<PantryItemCloseValidityDTO> = closeValidityItemsList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CloseValidityViewHolder {
        val itemView = CardCloseValidityBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false)

        return CloseValidityViewHolder(itemView)
    }

    override fun getItemCount(): Int = pantryItemsList.size

    override fun onBindViewHolder(holder: CloseValidityViewHolder, position: Int) {
        val pantryItem = pantryItemsList[position]
        holder.bind(pantryItem)
    }

    inner class CloseValidityViewHolder(
        private val binding: CardCloseValidityBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var pantryItem: PantryItemCloseValidityDTO

        init {
            itemView.setOnClickListener {
                onItemClick(pantryItem.pantryItemId)
            }
        }

        fun bind(pantryItem: PantryItemCloseValidityDTO) {
            this.pantryItem = pantryItem
            with(binding) {
                cardCloseValidityProduct.text = pantryItem.productName
                cardCloseValidityAmount.renderAmount(pantryItem.amount, pantryItem.unit)
                cardCloseValidityDate.text = pantryItem.validityDate.formatAsString()
            }
        }
    }

    fun updateState(pantryItems: List<PantryItemCloseValidityDTO>) {
        pantryItemsList.clear()
        pantryItemsList.addAll(pantryItems)
        notifyDataSetChanged()
    }
}
