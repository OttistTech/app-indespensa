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

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _recipes = MutableLiveData<List<RecipePartialDTO>?>()
    val recipes: MutableLiveData<List<RecipePartialDTO>?> = _recipes

    private val _isLoading = MutableLiveData(false)
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _feedback = MutableLiveData<Feedback?>()
    val feedback: MutableLiveData<Feedback?> = _feedback

    private var page = 0
    private var isLastPageLoaded = false

    private val _isPremium = MutableLiveData<Boolean>()
    val isPremium: LiveData<Boolean> = _isPremium

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
                    pageNumber = page,
                    createdByYou = true
                )

                _recipes.value = response.content
                isLastPageLoaded = response.last

                _isLoading.value = false
                _feedback.value = null
            } catch (e: ResourceNotFoundException) {
                _isLoading.value = false

                if (page == 0) {
                    _recipes.value = null
                    _feedback.value = Feedback(
                        feedbackId = FeedbackId.RECIPES_LIST,
                        code = FeedbackCode.NOT_FOUND,
                        message = "Parece que você não criou nenhuma receita"
                    )
                }

                isLastPageLoaded = true
            } catch (e: Exception) {
                _isLoading.value = false
                _recipes.value = null
                _feedback.value = Feedback(
                    feedbackId = FeedbackId.RECIPES_LIST,
                    code = FeedbackCode.UNHANDLED,
                    message = "Não foi possível buscar as receitas criadas por você!"
                )
            }
        }
    }

    fun fetchSwitchPremium(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                if (userRepository.updateUserBecomePremium()) {
                    _isPremium.value = true
                    _feedback.value = Feedback(
                        feedbackId = FeedbackId.SWITCH_PREMIUM,
                        code = FeedbackCode.SUCCESS,
                        message = "Você cancelou seu plano Premium"
                    )
                    onSuccess("Você cancelou seu plano premium")
                }
            } catch (e: Exception) {
                _feedback.value = Feedback(
                    feedbackId = FeedbackId.SWITCH_PREMIUM,
                    code = FeedbackCode.UNHANDLED,
                    message = "Não foi possível cancelar o plano"
                )
                onError("Algo aconteceu. Tente novamente mais tarde!")
            }
        }
    }

    fun loadNextPage() {
        if (!isLastPageLoaded) {
            page += 1
            fetchRecipes()
        }
    }

}