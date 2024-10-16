package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.data.repository.UserRepository

class HomeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    fun isUserPremium() : Boolean {
        return this.userRepository.getUserCredentials().isPremium
    }
}