# Data Layer

## Models
- `database/model/Article.kt` – UI model (Parcelable) with bookmark flag and metadata.
- API DTOs: `TopNewsResponse`, `TopNews`, `SearchNewsResponse` map remote payloads.

## Local persistence (Room)
- `ArticleEntity` stored in `articles` table; indices on `category` and `publishDate` for filtered queries.
- `NewsDao` provides paging sources, bookmark queries, sentiment-based queries, and cleanup (`deleteOlderThan`).
- `NewsDB` wires Room with the `articles` table; converters live in `Convertors.kt`.
- `Mapper.kt` converts between network/DB (`ArticleEntity`) and UI (`Article`).

## Network (Retrofit)
- `NewsApiService` endpoints:
  - `getLatestNews(source-country, language, headlines-only)` → `TopNewsResponse`.
  - `searchNews(text, offset, number, language, sort, sort-direction)` → `SearchNewsResponse`.
- `NewsRepositoryModule` installs OkHttp interceptor to append the WorldNews API key to every request and builds Retrofit with `GsonConverterFactory`.

## Paging
- Paging 3 drives both local and remote sources.
- Local feed: `Pager` with `NewsDao.getPagedArticles()` or `getFilteredNews()`.
- Remote search: `SearchNewsPagingSource` calls `searchNews`, merges bookmark state using DAO `getBookmarkedUrls`, and handles offset-based pagination.

## Repositories
- `NewsRepository` orchestrates network + cache:
  - `refreshNews` fetches top news, preserves bookmarks, and saves to Room.
  - `getPagedNewsFromDb`, `getFilteredNews` expose `Flow<PagingData<Article>>`.
  - `searchNews` exposes `Flow<PagingData<Article>>` backed by `SearchNewsPagingSource`.
  - Helpers: bookmarks, sentiment queries, notified articles, and cleanup (`deleteOlderThan`).
- `SettingsRepository` wraps `DataStore<Preferences>` for app behavior (auto-open search, auto-speak toggle, dark theme, Material You), notifications (allowance, fetched/breaking), background sync, and auto cleanup. Also includes cache size utilities and notification permission helper.

## Preferences
- `SettingsDataStore` implements `PreferenceDataStore` bridge so `PreferenceFragmentCompat` reads/writes the same DataStore keys as `SettingsRepository`.
- Keys live alongside `SettingsRepository` (e.g., `auto_open_search`, `background_fetch`, `auto_cleanup`).

## Background jobs
- WorkManager workers run against repositories:
  - `NewsSyncWorker` refreshes news and optionally fires fetched-news notifications.
  - `BreakingNewsWorker` alternates good/bad sentiment notifications, tracking notified items via Room.
  - `CleanupOldArticlesWorker` deletes articles older than 4 days (configurable via preference toggle).

