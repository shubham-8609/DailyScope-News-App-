# Search Feature

## UX flow
- Search icon in Home toolbar navigates to `SearchFragment`.
- `SearchFragment` hosts its own Toolbar with a collapsible SearchView (menu `search_page_menu`).
- Initial state focuses the SearchView and shows suggestion chips; keyboard opens automatically when action view expands.
- Empty state shown when no query or no results.

## Data flow
- `SearchViewModel` keeps `_query: MutableStateFlow<String>`.
- `query.flatMapLatest` → `newsRepository.searchNews(query)` → PagingData<Article>; empty query returns `PagingData.empty()`.
- `searchResults` is cached in `viewModelScope` for rotation safety.
- `collectLatest` in `SearchFragment` submits PagingData to `NewsListAdapter`.

## Paging source
- `SearchNewsPagingSource` calls `NewsApiService.searchNews(text, offset, number, language)`.
- Bookmark state merged client-side by comparing URLs with `NewsDao.getBookmarkedUrls()`.
- Offset-based pagination computes `prevKey/nextKey`; `getRefreshKey` ensures smooth refresh.

## UI interactions
- `SearchView.OnQueryTextListener` submits trimmed text; `setOnCloseListener` clears results.
- Bookmark toggles call `searchViewModel.setBookmark(article)`; UI updates icon immediately.
- Load-state listener switches between suggestion container, results list, and empty message.

## Preferences integration
- `auto_open_search` (DataStore) is collected in `collectAutoOpenPreference`; when enabled, the search action view expands automatically on enter.
- Future hooks: `auto_speak` can be read in `ArticleFragment` to auto-play TTS when navigating from search results.

