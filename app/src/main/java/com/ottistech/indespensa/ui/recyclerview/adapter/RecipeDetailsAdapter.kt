package com.ottistech.indespensa.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.CardRecipeDetailsItemBinding
import com.ottistech.indespensa.ui.helpers.loadImage
import com.ottistech.indespensa.webclient.dto.recipe.RecipeIngredientDetailsDTO
import java.util.Locale

class RecipeDetailsAdapter(
    private val context: Context,
    ingredientsList: List<RecipeIngredientDetailsDTO> = ArrayList()
) : RecyclerView.Adapter<RecipeDetailsAdapter.RecipeDetailsViewHolder>() {

    private var ingredientsList : MutableList<RecipeIngredientDetailsDTO> = ingredientsList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeDetailsViewHolder {
        val itemView = CardRecipeDetailsItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )

        return RecipeDetailsViewHolder(itemView)
    }

    override fun getItemCount(): Int = ingredientsList.size

    override fun onBindViewHolder(holder: RecipeDetailsViewHolder, position: Int) {
        val shopItem = ingredientsList[position]
        holder.bind(shopItem)
    }

    inner class RecipeDetailsViewHolder(
        private val binding: CardRecipeDetailsItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var recipeDetails: RecipeIngredientDetailsDTO

        fun bind(recipeDetailsItem: RecipeIngredientDetailsDTO) = with(binding) {
            recipeDetails = recipeDetailsItem

            val formattedAmount = String.format(Locale.getDefault(), "%,.2f", recipeDetailsItem.amount)

            cardRecipeDetailsItemAmount.text = binding.root.context.getString(
                R.string.recipe_details_item_amount,
                formattedAmount,
                recipeDetailsItem.unit
            )

            cardRecipeDetailsItemName.text = recipeDetailsItem.foodName

            val imageRes = if (recipeDetailsItem.isInPantry) {
                R.drawable.ingredint_in_pantry
            } else {
                R.drawable.ingredient_not_in_pantry
            }

            cardRecipeDetailsImage.loadImage(imageRes)
        }


    }
}