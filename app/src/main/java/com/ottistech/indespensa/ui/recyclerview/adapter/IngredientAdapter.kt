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
    private val mode: UiMode,
    val onItemRemoved: (position: Int) -> Unit = {},
    val onItemAdded: (item: RecipeIngredientDTO) -> Unit = {}
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

    fun removeItem(position: Int) {
        ingredientList.removeAt(position)
        onItemRemoved(position)
        notifyItemRemoved(position)
    }

    fun addItem(item: RecipeIngredientDTO) {
        val existingIndex = ingredientList.indexOfFirst { it.foodName == item.foodName }
        if (existingIndex == -1) {
            ingredientList.add(item)
            notifyItemInserted(ingredientList.size - 1)
        } else {
            ingredientList[existingIndex] = item
            notifyItemChanged(existingIndex)
        }
        onItemAdded(item)
    }

    inner class IngredientViewHolder(
        private val binding: CardIngredientBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var ingredient: RecipeIngredientDTO

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
                        context.getDrawable(R.drawable.ic_x)!!
                    }
                    ingredientIcon.setImageDrawable(icon)
                }
            }
        }
    }
}