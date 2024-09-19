package com.ottistech.indespensa.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.CardShopItemBinding
import com.ottistech.indespensa.ui.helpers.loadImage
import com.ottistech.indespensa.ui.helpers.renderAmount
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemPartialDTO

class ShopAdapter(
    private val context: Context,
    shopItemList: List<ShopItemPartialDTO> = ArrayList(),
    val onChangeAmount: (itemId: Long, amount: Int) -> Unit,
    val onItemClick: (itemId: Long) -> Unit
) : RecyclerView.Adapter<ShopAdapter.ShopListViewHolder>() {

    private var shopItemList : MutableList<ShopItemPartialDTO> = shopItemList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListViewHolder {
        val itemView = CardShopItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )

        return ShopListViewHolder(itemView)
    }

    override fun getItemCount(): Int = shopItemList.size

    override fun onBindViewHolder(holder: ShopListViewHolder, position: Int) {
        val shopItem = shopItemList[position]
        holder.bind(shopItem)
    }

    fun updateState(shopItems: List<ShopItemPartialDTO>) {
        shopItemList.clear()
        shopItemList.addAll(shopItems)
        notifyDataSetChanged()
    }

    inner class ShopListViewHolder(
        private val binding: CardShopItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var shopItem: ShopItemPartialDTO

        init {
            binding.cardPantryAdd.setOnClickListener {
                shopItemList[adapterPosition].amount++
                notifyItemChanged(adapterPosition)
                onChangeAmount(shopItemList[adapterPosition].listItemId, shopItemList[adapterPosition].amount)
            }
            binding.cardPantryRemove.setOnClickListener {
                if(shopItemList[adapterPosition].amount-1 >= 0) {
                    shopItemList[adapterPosition].amount --
                    notifyItemChanged(adapterPosition)
                    onChangeAmount(shopItemList[adapterPosition].listItemId, shopItemList[adapterPosition].amount)
                }
            }
            itemView.setOnClickListener {
                onItemClick(shopItem.listItemId)
            }
        }

        fun bind(shopItem: ShopItemPartialDTO) {
            this.shopItem = shopItem
            binding.ingredientImage.loadImage(shopItem.imageUrl)
            binding.ingredientName.text = shopItem.productName
            binding.ingredientAmount.renderAmount(shopItem.amount, "x")
            binding.ingredientUnit.text = itemView.context.getString(R.string.ingredient_product_amount, shopItem.productAmount)
            binding.ingredientUnitType.text = shopItem.productUnit
        }
    }
}