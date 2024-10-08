package com.ottistech.indespensa.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.BadRequestException
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.ui.UiConstants
import com.ottistech.indespensa.webclient.dto.recipe.RecipeCreateDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeIngredientCreateDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeIngredientDTO
import kotlinx.coroutines.launch

class RecipeFormViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _ingredients = MutableLiveData<List<RecipeIngredientDTO>>()
    val ingredients: MutableLiveData<List<RecipeIngredientDTO>> = _ingredients

    private val _feedback = MutableLiveData<Int?>()
    val feedback: LiveData<Int?> = _feedback

    fun addIngredient(ingredient: RecipeIngredientDTO) {
        val currentList = _ingredients.value ?: mutableListOf()
        val updatedList = currentList.toMutableList()
        val existingIndex = updatedList.indexOfFirst { it.foodName == ingredient.foodName }
        if (existingIndex == -1) {
            updatedList.add(ingredient)
        } else {
            updatedList[existingIndex] = ingredient
        }
        _ingredients.value = updatedList
    }

    fun save(
        formRecipe: RecipeCreateDTO,
        imageBitmap: Bitmap?
    ) {
        viewModelScope.launch {
            formRecipe.ingredients = _ingredients.value?.map {
                RecipeIngredientCreateDTO(it.foodId, it.amount, it.unit, it.isEssential)
            }
            try {
                repository.create(formRecipe, imageBitmap)
                _feedback.value = UiConstants.CREATED
            } catch (exception: ResourceNotFoundException) {
                _feedback.value = UiConstants.ERROR_NOT_FOUND
            } catch (exception: BadRequestException) {
                _feedback.value = UiConstants.ERROR_BAD_REQUEST
            } catch (exception: Exception) {
                _feedback.value = UiConstants.FAIL
            }
        }
    }

}