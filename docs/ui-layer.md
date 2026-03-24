# UI Layer

## Activity
- `ui/activity/MainActivity.kt` hosts a single `NavHostFragment`, sets up Toolbar + BottomNavigation, and reacts to destination changes to adjust titles/subtitles and bottom-nav visibility.
- Applies dynamic theme and dark mode via `SettingsRepository` flows; requests notification permission when toggled in Settings.

## Fragments
- **HomeFragment**: shows paged feed from Room; pull-to-refresh triggers `NewsRepository.refreshNews`; integrates filter and search menu items.
- **SearchFragment**: Material-style search with Toolbar action view, suggestion chips, paging results, and bookmark support.
- **BookmarkFragment**: lists bookmarked articles from Room.
- **ArticleFragment**: displays article detail (navigated via Safe Args), with optional auto-speak behavior flag from preferences.
- **SettingsFragment**: `PreferenceFragmentCompat` backed by `SettingsDataStore`; toggles app behavior, notifications, background jobs, and storage actions.
- **FilterFragment**: bottom sheet for date/category/sentiment filters; writes `FilterState` into `MainViewModel`.

## ViewModels
- `MainViewModel`: holds feed PagingData, filter state, bookmarks, notification permission events, and theme/material flows. Delegates data to `NewsRepository` and `SettingsRepository`.
- `SearchViewModel`: owns query StateFlow, exposes search PagingData via `flatMapLatest`, and forwards bookmark updates to the repository.

## Adapters
- `NewsListAdapter`: PagingDataAdapter for feed and search; binds title/summary, metadata, image (Glide), and bookmark toggle.
- `BookmarkedAdapter`: recycler adapter for saved articles (non-paged list).

## Navigation
- Navigation Component graph connects destinations (Home, Search, Bookmark, Article, Settings). Bottom nav routes main tabs; Article hides bottom nav.

## UI state management
- StateFlows collected with `repeatOnLifecycle` to avoid leaks.
- Paging load states drive empty-state handling in Home and Search.
- ViewBinding used across fragments/activities for type-safe view access.

