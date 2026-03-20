package com.houseofrafa.feature.home.presentation

import androidx.lifecycle.ViewModel
import com.houseofrafa.core.domain.model.User
import com.houseofrafa.feature.auth.domain.repository.AuthRepository

class HomeViewModel(authRepository: AuthRepository) : ViewModel() {
    val user: User? = authRepository.getSavedUser()
}