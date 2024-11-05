package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.data.repository.DashboardRepository
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.webclient.dto.dashboard.PersonalDashboardDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCloseValidityDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipePartialDTO
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val dashboardRepository: DashboardRepository,
    private val recipeRepository: RecipeRepository,
    private val pantryRepository: PantryRepository
) : ViewModel() {

    private val _isDashboardLoading = MutableLiveData(false)
    private val _isCloseValidityItemsLoading = MutableLiveData(false)

    private val _isLoading = MediatorLiveData<Boolean>().apply {
        addSource(_isDashboardLoading) { updateLoadingState() }
        addSource(_isCloseValidityItemsLoading) { updateLoadingState() }
    }
    val isLoading: LiveData<Boolean> = _isLoading

    private val _dashboardData = MutableLiveData<PersonalDashboardDTO?>()
    val dashboardData: MutableLiveData<PersonalDashboardDTO?> = _dashboardData

    private val _closeValidityItems = MutableLiveData<List<PantryItemCloseValidityDTO>?>()
    val closeValidityItems: MutableLiveData<List<PantryItemCloseValidityDTO>?> = _closeValidityItems

    private val _isRecipesLoading = MutableLiveData(false)
    val isRecipesLoading: MutableLiveData<Boolean> = _isRecipesLoading
    private val _recipes = MutableLiveData<List<RecipePartialDTO>?>()
    val recipes: MutableLiveData<List<RecipePartialDTO>?> = _recipes

    private val _feedback = MutableLiveData<Feedback?>()
    val feedback: LiveData<Feedback?> = _feedback

    private var recipesPage = 0
    private var isLastRecipePageLoaded = false

    fun isUserPremium() : Boolean {
        return this.userRepository.getUserCredentials().isPremium
    }

    fun fetchDashboardData() {
        viewModelScope.launch {
            try {
                _isDashboardLoading.value = true
                val data = dashboardRepository.getPersonalData()
                _isDashboardLoading.value = false
                _dashboardData.value = data
            } catch(e: ResourceUnauthorizedException) {
                userRepository.logoutUser()
                _feedback.value =
                    Feedback(FeedbackId.PERSONAL_DASHBOARD, FeedbackCode.UNAUTHORIZED, "Realize login novamente!")
            } catch (e: Exception) {
                _isDashboardLoading.value = false
                _feedback.value =
                    Feedback(FeedbackId.PERSONAL_DASHBOARD, FeedbackCode.UNHANDLED, "Ops! Não foi possível carregar o DashBoard.")
            }
        }
    }

    fun fetchCloseValidityItems() {
        viewModelScope.launch {
            try {
                _isCloseValidityItemsLoading.value = true
                val data = pantryRepository.listCloseValidityItems()
                _isCloseValidityItemsLoading.value = false
                _closeValidityItems.value = data
            } catch(e: ResourceNotFoundException) {
                _isCloseValidityItemsLoading.value = false
                _closeValidityItems.value = null
                _feedback.value =
                    Feedback(FeedbackId.CLOSE_VALIDITY_ITEMS, FeedbackCode.NOT_FOUND, "Nenhum item próximo da validade encontrado!")
            } catch(e: ResourceUnauthorizedException) {
                userRepository.logoutUser()
                _feedback.value =
                    Feedback(FeedbackId.CLOSE_VALIDITY_ITEMS, FeedbackCode.UNAUTHORIZED, "Realize login novamente!")
            } catch (e: Exception) {
                _isCloseValidityItemsLoading.value = false
                _feedback.value =
                    Feedback(FeedbackId.CLOSE_VALIDITY_ITEMS, FeedbackCode.UNHANDLED, "Não foi possível carregar os itens próximos da validade!")
            }
        }
    }

    fun fetchRecommendedRecipes() {
        viewModelScope.launch {
            try {
                _isRecipesLoading.value = true
                val data = recipeRepository.list(pageNumber = recipesPage)
                _isRecipesLoading.value = false
                _recipes.value = data.content
                isLastRecipePageLoaded = data.last
            } catch (e: ResourceNotFoundException) {
                _isRecipesLoading.value = false
                if (recipesPage == 0) {
                    _recipes.value = null
                    _feedback.value =
                        Feedback(
                            FeedbackId.RECIPES_LIST,
                            FeedbackCode.NOT_FOUND,
                            "Nenhuma receita recomendada encontrada!"
                        )
                }
            } catch(e: ResourceUnauthorizedException) {
                userRepository.logoutUser()
                _feedback.value =
                    Feedback(FeedbackId.RECIPES_LIST, FeedbackCode.UNAUTHORIZED, "Realize login novamente!")
            } catch (e: Exception) {
                _isRecipesLoading.value = false
                _feedback.value =
                    Feedback(FeedbackId.RECIPES_LIST, FeedbackCode.UNHANDLED, "Não foi possível carregar as receitas!")
            }
        }
    }

    fun loadNextRecipePage() {
        if(!isLastRecipePageLoaded) {
            recipesPage += 1
            fetchRecommendedRecipes()
        }
    }

    private fun updateLoadingState() {
        _isLoading.value =
            _isDashboardLoading.value == true ||
            _isCloseValidityItemsLoading.value == true
    }
}