package com.ottistech.indespensa.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.databinding.CardPantryItemBinding
import com.ottistech.indespensa.shared.loadImage
import com.ottistech.indespensa.shared.renderAmount
import com.ottistech.indespensa.shared.renderValidityDate
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
            pantryItem.validityDate?.let {
                binding.cardPantryItemValidityDate.renderValidityDate(it)
            }

            if (pantryItem.wasOpened) binding.cardPantryWasOpened.visibility = View.VISIBLE
        }
    }

    fun updateState(pantryItems: List<PantryItemPartialDTO>) {
        pantryItemsList.clear()
        pantryItemsList.addAll(pantryItems)
        notifyDataSetChanged()
    }
}
