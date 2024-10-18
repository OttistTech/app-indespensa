package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.DashboardRepository
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.webclient.dto.dashboard.ProfileDashboardDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipePartialDTO
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val dashboardRepository: DashboardRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _profileData = MutableLiveData<ProfileDashboardDTO>()
    val profileData: LiveData<ProfileDashboardDTO> = _profileData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _recipes = MutableLiveData<List<RecipePartialDTO>?>()
    val recipes: MutableLiveData<List<RecipePartialDTO>?> = _recipes

    private val _isLoading = MutableLiveData(false)
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _feedback = MutableLiveData<String?>()
    val feedback: MutableLiveData<String?> = _feedback

    private var page = 0
    private var isLastPageLoaded = false

    fun fetchProfileData() {
        viewModelScope.launch {
            try {
                val result = dashboardRepository.getProfileData()
                _profileData.value = result
            } catch (e: ResourceNotFoundException) {
                _error.value = "Failed to get dash profile info"
            }
        }
    }

    fun fetchRecipes() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = recipeRepository.list(
                    pageNumber=page
                )

                _recipes.value = response.content
                isLastPageLoaded = response.last

                _isLoading.value = false
                _feedback.value = null
            } catch(e: ResourceNotFoundException) {
                _isLoading.value = false

                if(page == 0) {
                    _recipes.value = null
                    _feedback.value = "Parece que você não criou nenhuma receita"
                }
                isLastPageLoaded = true
            } catch (e: Exception) {
                _isLoading.value = false
                _recipes.value = null
                _feedback.value = "Não foi possível buscar as receitas criadas por você!"
            }
        }
    }

    fun loadNextPage() {
        if(!isLastPageLoaded) {
            page += 1
            fetchRecipes()
        }
    }

}