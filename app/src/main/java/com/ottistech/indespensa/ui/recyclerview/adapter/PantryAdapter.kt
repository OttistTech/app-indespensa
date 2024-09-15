package com.ottistech.indespensa.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.CardPantryItemBinding
import com.ottistech.indespensa.ui.helpers.formatAsString
import com.ottistech.indespensa.ui.helpers.loadImage
import com.ottistech.indespensa.webclient.dto.PantryItemPartialDTO
import java.util.Date
import java.util.concurrent.TimeUnit

class PantryAdapter(
    private val context: Context,
    pantryItemList: List<PantryItemPartialDTO> = ArrayList(),
    val onChangeAmount: (itemId: Long, amount: Int) -> Unit
) : RecyclerView.Adapter<PantryAdapter.PantryItemViewHolder>(){

    private var pantryItemsList : MutableList<PantryItemPartialDTO> = pantryItemList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PantryItemViewHolder {
        val itemView = CardPantryItemBinding.inflate(LayoutInflater.from(context), parent, false)
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
        }

        fun bind(pantryItem: PantryItemPartialDTO) {
            this.pantryItem = pantryItem
            binding.cardPantryItemImage.loadImage(pantryItem.imageUrl)
            binding.cardPantryItemName.text = pantryItem.name
            showAmount(pantryItem.pantryAmount)
            showValidityDate(pantryItem.validityDate)
        }

        private fun showValidityDate(validityDate: Date) {
            val dateView = binding.cardPantryItemValidityDate
            val currentDate = Date()
            val daysLeft = TimeUnit.MILLISECONDS.toDays(validityDate.time - currentDate.time)
            if(daysLeft > 10) {
                dateView.setTextColor(ContextCompat.getColor(context, R.color.green))
            } else {
                dateView.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
            dateView.text = validityDate.formatAsString()
        }

        private fun showAmount(amount: Int) {
            binding.cardPantryItemAmount.text = context.getString(R.string.amount_template, amount)
        }
    }

    fun updateState(pantryItems: List<PantryItemPartialDTO>) {
        pantryItemsList.clear()
        pantryItemsList.addAll(pantryItems)
        notifyDataSetChanged()
    }
}
