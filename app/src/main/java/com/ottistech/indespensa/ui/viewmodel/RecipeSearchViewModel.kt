package com.ottistech.indespensa.ui.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.shared.IngredientState
import com.ottistech.indespensa.shared.RecipeLevel
import com.ottistech.indespensa.ui.model.Filter
import com.ottistech.indespensa.ui.model.FilterType
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.ui.viewmodel.state.RecipeSearchFiltersState
import com.ottistech.indespensa.webclient.dto.recipe.RecipePartialDTO
import kotlinx.coroutines.launch

class RecipeSearchViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _filters = MutableLiveData(RecipeSearchFiltersState())
    val filters: MutableLiveData<RecipeSearchFiltersState> = _filters

    private val _recipes = MutableLiveData<List<RecipePartialDTO>?>()
    val recipes: MutableLiveData<List<RecipePartialDTO>?> = _recipes

    private val _feedback = MutableLiveData<Feedback?>()
    val feedback: MutableLiveData<Feedback?> = _feedback

    private val _isLoading = MutableLiveData(false)
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private var page = 0
    private var isLastPageLoaded = false

    private var searchHandler: Handler? = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    fun fetchRecipes() {
        clearCallbacks()
        _isLoading.value = true
        searchRunnable = Runnable {
            viewModelScope.launch {
                try {
                    with(_filters.value!!) {
                        val response = repository.list(
                            queryText=queryText,
                            pageNumber=page,
                            level=level,
                            availability=availability,
                            minPreparationTime=preparationTimeRange.first,
                            maxPreparationTime=preparationTimeRange.second
                        )
                        _recipes.value = response.content
                        isLastPageLoaded = response.last
                    }
                    _isLoading.value = false
                    _feedback.value = null
                } catch(e: ResourceNotFoundException) {
                    _isLoading.value = false
                    if(page == 0) {
                        _recipes.value = null
                        _feedback.value =
                            Feedback(FeedbackId.RECIPE_SEARCH, FeedbackCode.NOT_FOUND, "Não foram encontradas receitas com esses filtros!")
                    }
                    isLastPageLoaded = true
                } catch (e: Exception) {
                    _isLoading.value = false
                    _recipes.value = null
                    _feedback.value =
                        Feedback(FeedbackId.RECIPE_SEARCH, FeedbackCode.UNHANDLED, "Não foi possível buscar as receitas!")
                }
            }
        }
        searchHandler?.postDelayed(searchRunnable!!, 1000)
    }

    fun loadNextPage() {
        if(!isLastPageLoaded) {
            page += 1
            fetchRecipes()
        }
    }

    fun applyFilters(
        vararg filter: Filter<*>
    ) {
        for(item in filter) {
            when(item.type) {
                FilterType.LEVEL ->
                    _filters.value!!.apply {
                        val newValue = if(item.isSelected) { item.value as RecipeLevel } else { null }
                        this.level =  newValue
                    }
                FilterType.AVAILABILITY ->
                    _filters.value!!.apply {
                        val newValue = if(item.isSelected) { item.value as IngredientState } else { null }
                        this.availability = newValue
                    }
                FilterType.TIME ->
                    _filters.value!!.apply {
                        val newValue = if(item.isSelected) { item.value as Pair<Int, Int> } else { Pair(0, 1440) }
                            this.preparationTimeRange = newValue
                    }
            }
        }
        resetPagination()
        fetchRecipes()
    }

    fun applyQueryText(
        queryText: String
    ) {
        _filters.value!!.queryText = queryText
        resetPagination()
        fetchRecipes()
    }

    fun clearFilters() {
        _filters.value!!.resetState()
        resetPagination()
        fetchRecipes()
    }

    private fun resetPagination() {
        page = 0
        isLastPageLoaded = false
    }

    private fun clearCallbacks() {
        searchRunnable?.let {
            searchHandler?.removeCallbacks(it)
        }
    }
}