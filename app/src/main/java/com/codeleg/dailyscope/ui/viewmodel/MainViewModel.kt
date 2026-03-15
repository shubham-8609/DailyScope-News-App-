package com.codeleg.dailyscope.ui.viewmodel


import android.content.Context
import com.codeleg.dailyscope.database.model.Article
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bumptech.glide.Glide
import com.codeleg.dailyscope.database.repository.NewsRepository
import com.codeleg.dailyscope.utils.FilterState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val newsRepo: NewsRepository , private val settingsRepo: SettingsRepository) : ViewModel() {
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
    val bookmarkedArticles = newsRepo.getBookmarkedArticles()
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


    fun setBookmark(article: Article) {
        viewModelScope.launch {
            newsRepo.setBookmarkState(article)
        }
    }

    suspend fun getCategories() =  newsRepo.getCategoriesFromDb()
    fun clearFilters() {
        _filterState.value = FilterState()
    }

    suspend fun clearNewsDB() = newsRepo.clearDB()

    suspend fun clearCachedImages(context: Context){
        Glide.get(context).clearMemory()
        withContext(Dispatchers.IO){
            Glide.get(context).clearDiskCache()
        }
    }

    fun clearBookmarks() {
        viewModelScope.launch {
            newsRepo.clearBookmarks()
        }
    }

    suspend fun getTotalNewsCount(): Int = newsRepo.getTotalNewsCount()

}