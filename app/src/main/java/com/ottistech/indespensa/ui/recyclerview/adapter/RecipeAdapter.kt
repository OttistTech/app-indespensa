package com.ottistech.indespensa.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.CardRecipeItemBinding
import com.ottistech.indespensa.shared.RecipeLevel
import com.ottistech.indespensa.shared.loadImage
import com.ottistech.indespensa.webclient.dto.recipe.RecipePartialDTO

class RecipeAdapter(
    private val context: Context,
    recipeList: List<RecipePartialDTO> = ArrayList(),
    val onItemClick: (itemId: Long) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private var recipes : MutableList<RecipePartialDTO> = recipeList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeAdapter.RecipeViewHolder {
        val itemView = CardRecipeItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false)

        return RecipeViewHolder(itemView)
    }

    override fun getItemCount(): Int = recipes.size

    override fun onBindViewHolder(holder: RecipeAdapter.RecipeViewHolder, position: Int) {
        val pantryItem = recipes[position]
        holder.bind(pantryItem)
    }

    fun addItems(items: List<RecipePartialDTO>) {
        recipes.addAll(items)
        notifyItemRangeInserted(
            (recipes.size - items.size),
            recipes.size
        )
    }

    fun clearItems() {
        val totalItemsRemoved = recipes.size
        recipes.clear()
        notifyItemRangeRemoved(0, totalItemsRemoved)
    }

    inner class RecipeViewHolder(
        private val binding : CardRecipeItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var recipe: RecipePartialDTO

        init {
            itemView.setOnClickListener {
                onItemClick(recipe.recipeId)
            }
        }

        fun bind(item: RecipePartialDTO) {
            recipe = item
            with(binding) {
                cardRecipeItemImage.loadImage(recipe.imageUrl)
                cardRecipeItemTitle.text = recipe.title
                cardRecipeItemDescription.text = recipe.description
                cardRecipeItemIngredientsCount.text =
                    context.getString(R.string.ingredients_count_short, recipe.amountInPantry, recipe.amountIngredients)
                cardRecipeItemLevel.text = recipe.level.displayName
                cardRecipeItemTime.text =
                    context.getString(R.string.time_minutes, recipe.preparationTime)
                cardRecipeItemRating.rating = recipe.numStars
            }
            applyLabelStyles()
        }

        fun applyLabelStyles() {
            val colorRed = context.getColor(R.color.red)
            val colorOrange = context.getColor(R.color.secondary)
            val colorGreen = context.getColor(R.color.green)
            val defaultColor = context.getColor(R.color.gray)

            val percentageInPantry = (recipe.amountInPantry * 100) / recipe.amountIngredients
            val ingredientsColor = when (percentageInPantry) {
                in 0..80 -> colorRed
                in 81..99 -> colorOrange
                100 -> colorGreen
                else -> defaultColor
            }

            val levelStyles = when(recipe.level) {
                RecipeLevel.EASY -> Pair(context.getDrawable(R.drawable.ic_easy), colorGreen)
                RecipeLevel.INTER -> Pair(context.getDrawable(R.drawable.ic_inter), colorOrange)
                RecipeLevel.ADVANCED -> Pair(context.getDrawable(R.drawable.ic_advanced), colorRed)
            }

            with(binding) {
                cardRecipeItemIngredientsCount.setTextColor(ingredientsColor)
                cardRecipeItemLevelIcon.load(levelStyles.first)
                cardRecipeItemLevel.setTextColor(levelStyles.second)
            }
        }
    }
}