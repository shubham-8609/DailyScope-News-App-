package com.codeleg.dailyscope.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codeleg.dailyscope.database.repository.NewsRepository

class MainViewModelFactory(private val newsRepo: NewsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            // You can pass any dependencies required by MainViewModel here
            return MainViewModel(newsRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

