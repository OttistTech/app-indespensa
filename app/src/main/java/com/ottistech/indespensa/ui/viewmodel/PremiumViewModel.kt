package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.shared.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PremiumViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _carouselItems = MutableLiveData<List<String>>()
    val carouselItems: LiveData<List<String>> = _carouselItems

    private val _currentPrice = MutableLiveData<Double>()
    val currentPrice: LiveData<Double> = _currentPrice

    private val _isPremium = MutableLiveData<Boolean>()
    val isPremium: LiveData<Boolean> = _isPremium

    private val _nextCarouselItem = MutableLiveData<Int>()
    val nextCarouselItem: LiveData<Int> = _nextCarouselItem

    private var job: Job? = null

    init {
        _carouselItems.value = AppConstants.PREMIUM_CAROUSEL_MESSAGES
        _currentPrice.value = 19.99

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

    fun handlePaymentClick(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                if (repository.switchPremium()) {
                    _isPremium.value = true
                    onSuccess()
                }
            } catch (e: Exception) {
                onError("Algo aconteceu. Tente novamente mais tarde!")
            }
        }
    }

    public override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
