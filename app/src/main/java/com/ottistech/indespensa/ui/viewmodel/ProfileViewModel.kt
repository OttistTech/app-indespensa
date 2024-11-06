package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.DashboardRepository
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.shared.AppConstants
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.webclient.dto.dashboard.ProfileDashboardDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipePartialDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val dashboardRepository: DashboardRepository,
    private val recipeRepository: RecipeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _carouselItems = MutableLiveData<List<String>>()
    val carouselItems: LiveData<List<String>> = _carouselItems

    private val _nextCarouselItem = MutableLiveData<Int>()
    val nextCarouselItem: LiveData<Int> = _nextCarouselItem

    private var job: Job? = null

    private val _profileData = MutableLiveData<ProfileDashboardDTO>()
    val profileData: LiveData<ProfileDashboardDTO> = _profileData

    private val _recipes = MutableLiveData<List<RecipePartialDTO>?>()
    val recipes: LiveData<List<RecipePartialDTO>?> = _recipes

    private val _isRecipesLoading = MutableLiveData(false)
    val isRecipesLoading: LiveData<Boolean> = _isRecipesLoading
    private val _isContentLoading = MutableLiveData(false)
    val isContentLoading: LiveData<Boolean> = _isContentLoading


    private val _feedback = MutableLiveData<Feedback?>()
    val feedback: MutableLiveData<Feedback?> = _feedback

    private var page = 0
    private var isLastPageLoaded = false

    init {
        _carouselItems.value = AppConstants.PREMIUM_CAROUSEL_MESSAGES

        startCarousel()
    }

    private fun startCarousel() {
        job = viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                delay(3000)
                _carouselItems.value?.let {
                    val currentItem = _nextCarouselItem.value ?: 0
                    val nextItem = (currentItem + 1) % it.size
                    _nextCarouselItem.postValue(nextItem)
                }
            }
        }
    }

    fun fetchProfileData() {
        viewModelScope.launch {
            _isContentLoading.value = true
            try {
                val result = dashboardRepository.getProfileData()
                _profileData.value = result
            } catch (e: ResourceNotFoundException) {
                _feedback.value =
                    Feedback(FeedbackId.GET_PROFILE_DATA, FeedbackCode.NOT_FOUND, "Não foi possível encontrar suas informações")
            } catch(e: Exception) {
                _feedback.value =
                    Feedback(FeedbackId.GET_PROFILE_DATA, FeedbackCode.UNHANDLED, "Não foi possível carregar o perfil")
            }
            _isContentLoading.value = false
        }
    }

    fun fetchRecipes() {
        viewModelScope.launch {
            _isRecipesLoading.value = true
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
            _isRecipesLoading.value = false
        }
    }

    fun fetchSwitchPremium() {
        viewModelScope.launch {
            try {
                if (userRepository.switchPremium()) {
                    _feedback.value =
                        Feedback(FeedbackId.SWITCH_PREMIUM, FeedbackCode.SUCCESS, "Você cancelou seu plano Premium")
                }
            } catch (e: Exception) {
                _feedback.value =
                    Feedback(FeedbackId.SWITCH_PREMIUM, FeedbackCode.UNHANDLED, "Não foi possível cancelar o plano")
            }
            _isRecipesLoading.value = false
        }
    }

    fun loadNextPage() {
        if (!isLastPageLoaded) {
            page += 1
            fetchRecipes()
        }
    }

}