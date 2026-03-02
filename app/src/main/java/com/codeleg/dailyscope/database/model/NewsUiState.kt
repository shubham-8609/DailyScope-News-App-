package com.codeleg.dailyscope.database.model

sealed class NewsUiState {
    object Loading : NewsUiState()
    data class Success(val news: List<Article>) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}