package com.ottistech.indespensa.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.databinding.CardPantryItemBinding
import com.ottistech.indespensa.ui.helpers.loadImage
import com.ottistech.indespensa.ui.helpers.renderAmount
import com.ottistech.indespensa.ui.helpers.renderValidityDate
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemPartialDTO

class PantryAdapter(
    private val context: Context,
    pantryItemList: List<PantryItemPartialDTO> = ArrayList(),
    val onChangeAmount: (itemId: Long, amount: Int) -> Unit,
    val onItemClick: (itemId: Long) -> Unit
) : RecyclerView.Adapter<PantryAdapter.PantryItemViewHolder>(){

    private var pantryItemsList : MutableList<PantryItemPartialDTO> = pantryItemList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PantryItemViewHolder {
        val itemView = CardPantryItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false)

        return PantryItemViewHolder(itemView)
    }

    override fun getItemCount(): Int = pantryItemsList.size

    override fun onBindViewHolder(holder: PantryItemViewHolder, position: Int) {
        val pantryItem = pantryItemsList[position]
        holder.bind(pantryItem)
    }

    inner class PantryItemViewHolder(
        private val binding: CardPantryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var pantryItem: PantryItemPartialDTO

        init {
            binding.cardPantryAdd.setOnClickListener {
                pantryItemsList[adapterPosition].pantryAmount ++
                notifyItemChanged(adapterPosition)
                onChangeAmount(pantryItemsList[adapterPosition].pantryItemId, pantryItemsList[adapterPosition].pantryAmount)
            }
            binding.cardPantryRemove.setOnClickListener {
                if(pantryItemsList[adapterPosition].pantryAmount-1 >= 0) {
                    pantryItemsList[adapterPosition].pantryAmount --
                    notifyItemChanged(adapterPosition)
                    onChangeAmount(pantryItemsList[adapterPosition].pantryItemId, pantryItemsList[adapterPosition].pantryAmount)
                }
            }
            itemView.setOnClickListener {
                onItemClick(pantryItem.pantryItemId)
            }
        }

        fun bind(pantryItem: PantryItemPartialDTO) {
            this.pantryItem = pantryItem
            binding.cardPantryItemImage.loadImage(pantryItem.imageUrl)
            binding.cardPantryItemName.text = pantryItem.name
            binding.cardPantryItemAmount.renderAmount(pantryItem.pantryAmount, "x")
            binding.cardPantryItemValidityDate.renderValidityDate(pantryItem.validityDate)
        }
    }

    fun updateState(pantryItems: List<PantryItemPartialDTO>) {
        pantryItemsList.clear()
        pantryItemsList.addAll(pantryItems)
        notifyDataSetChanged()
    }
}
