package com.ottistech.indespensa.ui.recyclerview.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.CardIngredientBinding
import com.ottistech.indespensa.ui.UiMode
import com.ottistech.indespensa.webclient.dto.recipe.RecipeIngredientDTO

class IngredientAdapter (
    private val context: Context,
    ingredients: List<RecipeIngredientDTO> = ArrayList(),
    private val mode: UiMode
) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    private var ingredientList : MutableList<RecipeIngredientDTO> = ingredients.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val itemView = CardIngredientBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )

        return IngredientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredientList[position]
        holder.bind(ingredient)
    }

    override fun getItemCount(): Int = ingredientList.size

    fun updateState(ingredients: List<RecipeIngredientDTO>) {
        ingredientList.clear()
        ingredientList.addAll(ingredients)
        notifyDataSetChanged()
    }

    inner class IngredientViewHolder(
        private val binding: CardIngredientBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var ingredient: RecipeIngredientDTO

        init {
            // listeners
        }

        fun bind(ingredient: RecipeIngredientDTO) {
            this.ingredient = ingredient
            with(binding) {
                val amountText = ingredient.amount.toString() + ingredient.unit
                ingredientAmount.text = amountText
                ingredientName.text = ingredient.foodName
                ingredientRequiredIcon.visibility = if(ingredient.isEssential) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                if(mode == UiMode.EDIT) {
                    ingredientIcon.visibility = View.GONE
                } else {
                    val icon: Drawable = if(ingredient.isInPantry) {
                        context.getDrawable(R.drawable.ic_check)!!
                    } else {
                        context.getDrawable(R.drawable.ic_alert)!!
                    }
                    ingredientIcon.setImageDrawable(icon)
                }
            }
        }
    }
}