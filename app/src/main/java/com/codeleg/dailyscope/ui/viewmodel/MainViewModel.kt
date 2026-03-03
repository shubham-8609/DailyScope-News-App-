package com.codeleg.dailyscope.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeleg.dailyscope.database.local.ArticleEntity
import com.codeleg.dailyscope.database.model.Article
import com.codeleg.dailyscope.database.model.NewsUiState
import com.codeleg.dailyscope.database.repository.NewsRepository
import com.codeleg.dailyscope.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class MainViewModel(private val newsRepo: NewsRepository) : ViewModel() {

    val news = newsRepo.getNewsFromDb()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing
    fun refreshNews(){
        viewModelScope.launch {
            _isRefreshing.value = true
            newsRepo.refreshNews("in", "en", false)
            _isRefreshing.value = false
        }
    }

}