package com.codeleg.dailyscope.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codeleg.dailyscope.database.repository.NewsRepository
import com.codeleg.dailyscope.database.repository.SettingsRepository


class MainViewModelFactory(private val newsRepo: NewsRepository  , private val settingsRepo: SettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            // You can pass any dependencies required by MainViewModel here
            return MainViewModel(newsRepo, settingsRepo) as T
        }
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(newsRepo , settingsRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

