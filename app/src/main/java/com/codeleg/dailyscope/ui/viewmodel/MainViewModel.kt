package com.codeleg.dailyscope.ui.viewmodel

import android.util.Log
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
            val startTime = System.currentTimeMillis()
            _topNews.value = NewsUiState.Loading

            Log.d("NewsVM", "fetchNews() started")

            try {
                when (val result = newsRepo.getTopNews("in", "en", false)) {

                    is Resource.Success -> {
                        val articles = result.data?.top_news
                            ?.flatMap { it.news }
                            ?: emptyList()

                        Log.d("NewsVM", "Success: ${articles.size} articles received")

                        _topNews.value = NewsUiState.Success(articles)
                    }

                    is Resource.Error -> {
                        Log.e("NewsVM", "Error from repo: ${result.message}")
                        _topNews.value =
                            NewsUiState.Error(result.message ?: "Unknown error")
                    }
                    else -> {}
                }

            } catch (e: Exception) {
                Log.e("NewsVM", "Exception: ${e.localizedMessage}", e)

                _topNews.value =
                    NewsUiState.Error("Failed to fetch news: ${e.message}")
            } finally {
                val duration = System.currentTimeMillis() - startTime
                Log.d("NewsVM", "fetchNews() finished in ${duration}ms")
            }
        }
    }

}