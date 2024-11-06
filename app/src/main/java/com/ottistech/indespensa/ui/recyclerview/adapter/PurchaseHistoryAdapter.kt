package com.ottistech.indespensa.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.CardPurchaseItemBinding
import com.ottistech.indespensa.databinding.HeaderPurchaseBinding
import com.ottistech.indespensa.shared.formatAsString
import com.ottistech.indespensa.shared.loadImage
import com.ottistech.indespensa.webclient.dto.shoplist.PurchaseItemDTO
import java.util.Date

class PurchaseHistoryAdapter(
    private val context: Context,
    purchaseItemsList: List<Any> = ArrayList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var purchaseItemsList : MutableList<Any> = purchaseItemsList.toMutableList()

    data class PurchaseHeader(
        val purchaseDate: Date,
        val dailyAmount: Int
    )
    private val TYPE_HEADER = 0
    private val TYPE_PURCHASE_ITEM = 1

    override fun getItemViewType(position: Int): Int {
        return when(purchaseItemsList[position]) {
            is PurchaseHeader -> TYPE_HEADER
            is PurchaseItemDTO -> TYPE_PURCHASE_ITEM
            else -> throw IllegalArgumentException("Invalid type of data at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_HEADER -> {
                val itemView = HeaderPurchaseBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                PurchaseHeaderViewHolder(itemView)
            }
            TYPE_PURCHASE_ITEM -> {
                val itemView = CardPurchaseItemBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                PurchaseItemViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PurchaseHeaderViewHolder -> {
                val dayHeader = purchaseItemsList[position] as PurchaseHeader
                holder.bind(dayHeader)
            }
            is PurchaseItemViewHolder -> {
                val purchase = purchaseItemsList[position] as PurchaseItemDTO
                holder.bind(purchase)
            }
        }
    }

    override fun getItemCount(): Int = purchaseItemsList.size

    fun updateState(purchaseItems: List<Any>) {
        purchaseItemsList.clear()
        purchaseItemsList.addAll(purchaseItems)
        notifyDataSetChanged()
    }

    inner class PurchaseHeaderViewHolder(
        private val binding: HeaderPurchaseBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PurchaseHeader) {
            with(binding) {
                headerPurchaseDate.text = data.purchaseDate.formatAsString()
                headerPurchaseAmount.text = context.getString(R.string.ingredients_number, data.dailyAmount)
            }
        }
    }

    inner class PurchaseItemViewHolder(
        private val binding: CardPurchaseItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PurchaseItemDTO) {
            with(binding) {
                cardPurchaseItemImage.loadImage(data.imageUrl)
                cardPurchaseItemName.text = data.productName
                cardPurchaseItemAmount.text = context.getString(R.string.ingredient_amount, data.amount)
            }
        }
    }

}