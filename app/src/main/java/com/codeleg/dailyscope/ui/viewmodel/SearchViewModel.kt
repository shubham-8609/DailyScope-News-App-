package com.codeleg.dailyscope.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.codeleg.dailyscope.database.model.Article
import com.codeleg.dailyscope.database.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class SearchViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val searchResults: Flow<PagingData<Article>> = _query
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { term ->
            val trimmed = term.trim()
            if (trimmed.isEmpty()) {
                flowOf(PagingData.empty())
            } else {
                newsRepository.searchNews(trimmed)
            }
        }
        .cachedIn(viewModelScope)

    fun submitQuery(term: String) {
        _query.value = term
    }

    fun clearQuery() {
        _query.value = ""
    }

    fun setBookmark(article: Article) {
        viewModelScope.launch {
            newsRepository.setBookmarkState(article)
        }
    }
}
