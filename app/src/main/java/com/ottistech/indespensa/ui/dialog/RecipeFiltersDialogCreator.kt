package com.ottistech.indespensa.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.databinding.DialogRecipeFiltersBinding
import com.ottistech.indespensa.shared.AppConstants
import com.ottistech.indespensa.shared.IngredientState
import com.ottistech.indespensa.shared.RecipeLevel
import com.ottistech.indespensa.ui.model.Filter
import com.ottistech.indespensa.ui.recyclerview.adapter.FilterAdapter
import com.ottistech.indespensa.ui.viewmodel.RecipeSearchViewModel

class RecipeFiltersDialogCreator (
    private val context: Context,
    private val viewModel: RecipeSearchViewModel

) {

    private lateinit var binding: DialogRecipeFiltersBinding
    private lateinit var dialog: Dialog

    private lateinit var levelFiltersAdapter: FilterAdapter<RecipeLevel>
    private lateinit var availabilityFiltersAdapter: FilterAdapter<IngredientState>
    private lateinit var preparationTimeRangeFiltersAdapter: FilterAdapter<Pair<Int, Int>>

    private var levelFilters: List<Filter<RecipeLevel>> = AppConstants.LEVEL_RECIPE_FILTERS.map { it.copy() }
    private var availabilityFilters: List<Filter<IngredientState>> = AppConstants.AVAILABILITY_RECIPE_FILTERS.map { it.copy() }
    private var preparationTimeRangeFilters: List<Filter<Pair<Int, Int>>> = AppConstants.TIME_RECIPE_FILTERS.map { it.copy() }

    private var levelFilterChange: Filter<RecipeLevel>? = null
    private var availabilityFilterChange: Filter<IngredientState>? = null
    private var preparationTimeRangeFilterChange: Filter<Pair<Int,Int>>? = null

    fun showDialog(
        onApply: (Array<out Filter<*>>) -> Unit
    ) {
        with(viewModel.filters.value!!) {
            levelFilters.find { it.value == this.level }?.apply { isSelected = true }
            availabilityFilters.find { it.value == this.availability }?.apply { isSelected = true }
            preparationTimeRangeFilters.find { it.value == this.preparationTimeRange }?.apply { isSelected = true }
        }

        binding = DialogRecipeFiltersBinding.inflate(LayoutInflater.from(context))
        dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .create()

        setupAdapter(
            levelFilters,
            availabilityFilters,
            preparationTimeRangeFilters
        )
        setupRecyclerView()

        binding.dialogRecipeFiltersApplyButton.setOnClickListener {
            val filtersChanged = listOfNotNull(levelFilterChange, availabilityFilterChange, preparationTimeRangeFilterChange).toTypedArray()
            onApply(filtersChanged)
            resetState()
            dialog.dismiss()
        }

        binding.dialogRecipeFiltersCancelButton.setOnClickListener {
            resetState()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun resetState() {
        levelFilters = AppConstants.LEVEL_RECIPE_FILTERS.map { it.copy() }
        availabilityFilters = AppConstants.AVAILABILITY_RECIPE_FILTERS.map { it.copy() }
        preparationTimeRangeFilters = AppConstants.TIME_RECIPE_FILTERS.map { it.copy() }

        levelFilterChange = null
        availabilityFilterChange = null
        preparationTimeRangeFilterChange = null
    }

    private fun setupAdapter(
        levelFilters: List<Filter<RecipeLevel>>,
        availabilityFilters: List<Filter<IngredientState>>,
        timeFilters: List<Filter<Pair<Int, Int>>>
    ) {
        levelFiltersAdapter = FilterAdapter(
            context=context,
            filters=levelFilters,
            isExclusive=true
        ) {
            levelFilterChange = it
        }
        availabilityFiltersAdapter = FilterAdapter(
            context=context,
            filters=availabilityFilters,
            isExclusive=true
        ) {
            availabilityFilterChange = it
        }
        preparationTimeRangeFiltersAdapter = FilterAdapter(
            context=context,
            filters=timeFilters,
            isExclusive=true
        ) {
            preparationTimeRangeFilterChange = it
        }
    }

    private fun setupRecyclerView() {
        with(binding.dialogRecipeFiltersLevelFilters) {
            adapter = levelFiltersAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
        with(binding.dialogRecipeFiltersAvailabilityFilters) {
            adapter = availabilityFiltersAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
        with(binding.dialogRecipeFiltersPreparationTimeFilters) {
            adapter = preparationTimeRangeFiltersAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
    }
}