package com.ottistech.indespensa.ui.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.databinding.IngredientShoplistItemBinding
import com.ottistech.indespensa.ui.helpers.tryLoadImage
import com.ottistech.indespensa.webclient.dto.ShopListIngredientResponseDTO

class ShopListAdapter(
    private val ingredients: List<ShopListIngredientResponseDTO>
) : RecyclerView.Adapter<ShopListAdapter.ShopListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListViewHolder {
        val binding = IngredientShoplistItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ShopListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopListViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.bind(ingredient)
    }

    override fun getItemCount(): Int = ingredients.size


    inner class ShopListViewHolder(private val binding: IngredientShoplistItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: ShopListIngredientResponseDTO) {
            binding.ingredientImage.tryLoadImage(ingredient.imageUrl)

            binding.ingredientName.text = ingredient.productName
            binding.ingredientAmount.text = "${ingredient.amount}x"
            binding.ingredientUnit.text = String.format("%.2f", ingredient.productAmount)
            binding.ingredientUnitType.text = ingredient.productUnit
        }
    }
}