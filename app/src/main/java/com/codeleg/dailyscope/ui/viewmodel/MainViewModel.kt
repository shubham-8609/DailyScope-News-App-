package com.codeleg.dailyscope.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeleg.dailyscope.database.model.NewsResponse
import com.codeleg.dailyscope.database.model.NewsUiState
import com.codeleg.dailyscope.database.repository.NewsRepository
import com.codeleg.dailyscope.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val newsRepo: NewsRepository) : ViewModel() {
    private val _latestNews = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val latestNews: StateFlow<NewsUiState> = _latestNews


    fun fetchNews() {
        viewModelScope.launch {
            _latestNews.value = NewsUiState.Loading
            try {

                when(val resul = newsRepo.getLatestNews()) {
                    is Resource.Success -> {
                        val news = resul.data?.news ?: emptyList()
                        _latestNews.value = NewsUiState.Success(news)
                    }
                    is Resource.Error -> {
                        _latestNews.value = NewsUiState.Error(resul.message ?: "Unknown error altering at viewmodel")
                    }

                    else -> {}
                }


            } catch (e: Exception) {
                _latestNews.value =
                    NewsUiState.Error("Failed to fetch news: ${e.message ?: "Unknown error"}")
            }
        }
    }


}