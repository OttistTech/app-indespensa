package com.ottistech.indespensa.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.BadRequestException
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.ui.helpers.validMaxValue
import com.ottistech.indespensa.ui.helpers.validMinLength
import com.ottistech.indespensa.ui.helpers.validNotNull
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.ui.viewmodel.state.RecipeFormErrorState
import com.ottistech.indespensa.ui.viewmodel.state.RecipeFormFieldsState
import com.ottistech.indespensa.webclient.dto.recipe.RecipeIngredientDTO
import kotlinx.coroutines.launch

class RecipeFormViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    val formState = MutableLiveData(RecipeFormFieldsState())
    val formErrorState = MutableLiveData(RecipeFormErrorState())

    val isFormValid = MutableLiveData(true)
    val isLoading = MutableLiveData(false)

    private val _feedback = MutableLiveData<Feedback?>()
    val feedback: LiveData<Feedback?> = _feedback

    init {
        formErrorState.observeForever {
            validForm()
        }
    }

    fun addIngredient(ingredient: RecipeIngredientDTO) {
        val list =
            formState.value!!.ingredients?.toMutableList()
            ?: mutableListOf()
        val existingIndex = list.indexOfFirst { it.foodName == ingredient.foodName }
        if (existingIndex == -1) {
            list.add(ingredient)
        } else {
            list[existingIndex] = ingredient
        }
        formState.value = formState.value!!.copy(ingredients=list)
        formErrorState.value = formErrorState.value!!.copy(ingredients=null)
    }

    fun removeIngredient(position: Int) {
        val list =
            formState.value!!.ingredients?.toMutableList()
            ?: mutableListOf()
        list.removeAt(position)
        formState.value = formState.value!!.copy(ingredients=list)
    }

    fun setNewProductImageBitmap(bitmap: Bitmap) {
        formState.value!!.newImageBitmap = bitmap
    }

    fun submit() {
        validAllFields()
        if(isFormValid.value!!) {
            viewModelScope.launch {
                isLoading.value = true
                try {
                    repository.create(
                        formState.value!!.toRecipeCreateDTO(),
                        formState.value!!.newImageBitmap
                    )
                    _feedback.value =
                        Feedback(FeedbackId.CREATE_RECIPE, FeedbackCode.SUCCESS, "Receita criada com sucesso!")
                } catch (exception: ResourceNotFoundException) {
                    _feedback.value =
                        Feedback(FeedbackId.CREATE_RECIPE, FeedbackCode.NOT_FOUND, "Algum dos ingredientes não foi encontrado!")
                } catch (exception: BadRequestException) {
                    _feedback.value =
                        Feedback(FeedbackId.CREATE_RECIPE, FeedbackCode.BAD_REQUEST, "Não foi possível criar. Revise as informações!")
                } catch (exception: Exception) {
                    _feedback.value =
                        Feedback(FeedbackId.CREATE_RECIPE, FeedbackCode.UNHANDLED, "Não foi possível criar a receita!")
                }
                isLoading.value = false
            }
        }
    }

    private fun validForm() {
        val errorState = formErrorState.value
        isFormValid.value = errorState?.let {
            it.name == null &&
            it.description == null &&
            it.time == null &&
            it.level == null &&
            it.preparationMethod == null &&
            it.ingredients == null
        } ?: true
    }

    fun validName() {
        val value = formState.value!!.name
        val error =
            if( !validNotNull(value) ) { "Nome é obrigatório!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(name=error)
    }

    fun validDescription() {
        val value = formState.value!!.description
        val error =
            if( !validNotNull(value) ) { "Descrição é obrigatório!" }
            else if( !validMinLength(value, 12) ) { "Descrição deve ter no mínimo 12 caracteres!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(description=error)
    }

    fun validTime() {
        val value = formState.value!!.time
        val error =
            if( !validNotNull(value) ) { "Tempo é obrigatório!" }
            else if( !validMaxValue(value, 1440) ) { "Tempo máximo excedido" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(time=error)
    }

    fun validLevel() {
        val value = formState.value!!.level
        val error =
            if( !validNotNull(value) ) { "Nível é obrigatório!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(level=error)
    }

    private fun validIngredients() {
        val value = formState.value!!.ingredients
        val error =
            if(value?.size == 0) { "Selecione pelo menos um ingrediente" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(ingredients=error)
    }

    fun validPreparationMethod() {
        val value = formState.value!!.preparationMethod
        val error =
            if( !validNotNull(value) ) { "Modo de preparo é obrigatório!" }
            else if( !validMinLength(value, 12) ) { "Modo de preparo deve ter no mínimo 50 caracteres!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(preparationMethod=error)
    }

    private fun validAllFields() {
        validName()
        validDescription()
        validTime()
        validLevel()
        validIngredients()
        validPreparationMethod()
        validForm()
    }
}