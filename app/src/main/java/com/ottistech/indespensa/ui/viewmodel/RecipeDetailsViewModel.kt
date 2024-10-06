package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.BadRequestException
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.webclient.dto.recipe.RateRecipeRequestDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeDetailsDTO
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _recipeDetails = MutableLiveData<RecipeDetailsDTO?>()
    val recipeDetails: LiveData<RecipeDetailsDTO?> = _recipeDetails

    private val _error = MutableLiveData<Exception?>()
    val error: LiveData<Exception?> = _error

    fun fetchRecipeDetails(recipeId: Long) {
        viewModelScope.launch {
            try {
                val details = recipeRepository.getRecipeDetails(recipeId)
                _recipeDetails.value = details
                _error.value = null
            } catch (e: Exception) {
                _error.value = e
            }
        }
    }

    fun handleRatingClick(rateRecipeRequestDTO: RateRecipeRequestDTO, recipeId: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                if (recipeRepository.rateRecipe(recipeId, rateRecipeRequestDTO)) {
                    onSuccess()
                }
            } catch (e: ResourceNotFoundException) {
                onError("No recipes found with ID: $recipeId")
            } catch (e: BadRequestException) {
                onError("Bad request")
            }
        }
    }
}