package com.codeleg.dailyscope.database.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.codeleg.dailyscope.database.local.NewsDao
import com.codeleg.dailyscope.database.model.Article
import com.codeleg.dailyscope.database.network.NewsApiService

private const val START_OFFSET = 0

class SearchNewsPagingSource(
    private val api: NewsApiService,
    private val newsDao: NewsDao,
    private val query: String,
    private val language: String?
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val offset = params.key ?: START_OFFSET
        return try {
            val response = api.searchNews(
                query = query,
                offset = offset,
                pageSize = params.loadSize,
                language = language
            )
            val bookmarkedUrls = newsDao.getBookmarkedUrls().toSet()
            val articles = response.news.map { article ->
                article.copy(isBookmarked = bookmarkedUrls.contains(article.url))
            }

            val nextKey = if (offset + params.loadSize >= response.available) null else offset + params.loadSize
            val prevKey = if (offset == START_OFFSET) null else maxOf(START_OFFSET, offset - params.loadSize)

            LoadResult.Page(
                data = articles,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        val anchorPos = state.anchorPosition ?: return null
        val closestPage = state.closestPageToPosition(anchorPos)
        return closestPage?.prevKey?.plus(state.config.pageSize)
            ?: closestPage?.nextKey?.minus(state.config.pageSize)
    }
}
