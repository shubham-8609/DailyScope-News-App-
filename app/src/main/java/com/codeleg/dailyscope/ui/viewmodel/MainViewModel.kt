package com.codeleg.dailyscope.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.codeleg.dailyscope.database.repository.NewsRepository
import com.codeleg.dailyscope.utils.FilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class MainViewModel(private val newsRepo: NewsRepository) : ViewModel() {
    val news = newsRepo.getPagedNewsFromDb().cachedIn(viewModelScope)
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing
    private val _filterState = MutableStateFlow(FilterState())
    val filterState = _filterState.asStateFlow()
    val filteredNews = filterState.flatMapLatest { state ->
        newsRepo.getFilteredNews(state)
    }.cachedIn(viewModelScope)
    fun applyFilters(filter: FilterState) {
        _filterState.value = filter
    }
    fun refreshNews(){
        viewModelScope.launch {
            _isRefreshing.value = true
            newsRepo.refreshNews("in", "en", false)
            _isRefreshing.value = false
        }
    }


    suspend fun getCategories() =  newsRepo.getCategoriesFromDb()
}