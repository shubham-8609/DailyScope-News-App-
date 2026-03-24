# Filters

## UI
- `FilterFragment` is a bottom sheet launched from Home toolbar.
- Inputs: date picker (UTC-bounded to last month), category chips populated from `NewsDao.getCategories()`, and sentiment range slider.
- Buttons: Apply (validates date/category) and Reset (clears date, resets sentiment, unchecks chips).

## State
- `FilterState` (`utils/FilterState.kt`) holds `date`, `category`, `sentimentMin`, `sentimentMax`.
- `MainViewModel._filterState` is a `MutableStateFlow`; `isFilterApplied` derives from non-null date/category.

## Data pipeline
1. User applies filters → `MainViewModel.applyFilters(FilterState)`.
2. Feed Flow switches via `flatMapLatest`:
   - No filters → `NewsRepository.getPagedNewsFromDb()` (all articles sorted by publishDate).
   - With filters → `NewsRepository.getFilteredNews(filter)` which delegates to `NewsDao.getFilteredNews(...)` and maps entities to `Article`.
3. PagingData is collected in Home and submitted to `NewsListAdapter`.

## DAO query
```sql
SELECT * FROM articles
WHERE (:category IS NULL OR category = :category)
AND (:date IS NULL OR publishDate >= :date)
AND sentiment BETWEEN :sentimentMin AND :sentimentMax
ORDER BY publishDate DESC;
```

## UX notes
- When refresh is triggered while filters are active, Home shows a toast and clears filters to avoid mixing new data with stale filters.
- Empty states are handled via Paging load states (showing a fallback layout when no items).

