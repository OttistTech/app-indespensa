package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.webclient.dto.recipe.RecipePartialDTO
import kotlinx.coroutines.launch

class RecipeListViewModel(
    private val recipeRepository: RecipeRepository,
) : ViewModel() {

    private val _recipes = MutableLiveData<List<RecipePartialDTO>?>()
    val recipes: LiveData<List<RecipePartialDTO>?> = _recipes

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _feedback = MutableLiveData<Feedback?>()
    val feedback: MutableLiveData<Feedback?> = _feedback

    private var page = 0
    private var isLastPageLoaded = false

    fun fetchRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = recipeRepository.list(
                    pageNumber = page,
                    createdByYou = true
                )

                _recipes.value = response.content
                isLastPageLoaded = response.last

                _feedback.value = null
            } catch (e: ResourceNotFoundException) {
                if (page == 0) {
                    _recipes.value = null
                    _feedback.value =
                        Feedback(FeedbackId.RECIPES_LIST, FeedbackCode.NOT_FOUND, "Parece que você não criou nenhuma receita")
                }
                isLastPageLoaded = true
            } catch (e: Exception) {
                _recipes.value = null
                _feedback.value =
                    Feedback(FeedbackId.RECIPES_LIST, FeedbackCode.UNHANDLED, "Não foi possível buscar as receitas criadas por você!")
            }
            _isLoading.value = false
        }
    }

    fun loadNextPage() {
        if (!isLastPageLoaded) {
            page += 1
            fetchRecipes()
        }
    }
}