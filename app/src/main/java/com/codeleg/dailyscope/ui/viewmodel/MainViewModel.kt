package com.codeleg.dailyscope.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeleg.dailyscope.database.model.Article
import com.codeleg.dailyscope.database.model.NewsUiState
import com.codeleg.dailyscope.database.repository.NewsRepository
import com.codeleg.dailyscope.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class MainViewModel(private val newsRepo: NewsRepository) : ViewModel() {
    private val _topNews = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val topNews: StateFlow<NewsUiState> = _topNews


    fun fetchNews() {
        viewModelScope.launch {
            _topNews.value = NewsUiState.Loading
            try {
                when(val result = newsRepo.getTopNews("in" , "en", false)) {
                    is Resource.Success -> {
                        val articles: List<Article> =
                            result.data?.top_news
                                ?.flatMap { it.news }
                                ?: emptyList()
                        _topNews.value = NewsUiState.Success(articles)
                    }
                    is Resource.Error -> {
                        _topNews.value = NewsUiState.Error(result.message ?: "Unknown error  at viewmodel")
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _topNews.value =
                    NewsUiState.Error("Failed to fetch news: ${e.message ?: "Unknown error"}")
            }
        }
    }


}