package com.codeleg.dailyscope.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.codeleg.dailyscope.database.repository.NewsRepository
import com.codeleg.dailyscope.utils.FilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val newsRepo: NewsRepository) : ViewModel() {
     val _filterState = MutableStateFlow(FilterState())
    val filterState = _filterState.asStateFlow()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    val news = filterState
        .flatMapLatest { state ->
            if (state.category == null && state.date == null) {
                newsRepo.getPagedNewsFromDb()
            } else {
                newsRepo.getFilteredNews(state)
            }
        }
        .cachedIn(viewModelScope)
    val isFilterApplied: Boolean
        get() = filterState.value.category != null || filterState.value.date != null
    fun applyFilters(filter: FilterState) {
        _filterState.value = filter
    }
    fun refreshNews(){
        viewModelScope.launch {
            _isRefreshing.value = true
            newsRepo.refreshNews("in", "en", false)
            _isRefreshing.value = false
           clearFilters()
        }
    }


    suspend fun getCategories() =  newsRepo.getCategoriesFromDb()
    fun clearFilters() {
        _filterState.value = FilterState()
    }
}