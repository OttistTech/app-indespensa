package com.ottistech.indespensa.ui.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.IngredientShoplistItemBinding
import com.ottistech.indespensa.ui.helpers.loadImage
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
            binding.ingredientImage.loadImage(ingredient.imageUrl)

            binding.ingredientName.text = ingredient.productName
            binding.ingredientAmount.text = itemView.context.getString(R.string.ingredient_amount, ingredient.amount)
            binding.ingredientUnit.text = itemView.context.getString(R.string.ingredient_product_amount, ingredient.productAmount)
            binding.ingredientUnitType.text = ingredient.productUnit
        }
    }
}