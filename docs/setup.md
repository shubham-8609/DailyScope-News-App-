# Setup

## Prerequisites
- Android Studio Hedgehog or newer
- JDK 17
- Android SDK with API 24+
- An active WorldNews API key (used via query param `api-key`)

## Configure API key
- The project currently injects the key in `DI/NewsRepositoryModule.kt` and `database/network/RetrofitInstance.kt` via an OkHttp interceptor.
- Replace the placeholder value with your key before building, or move it to a secured source (e.g., `local.properties` + BuildConfig) if shipping.

## Clone
```powershell
git clone https://github.com/shubham-8609/DailyScope-News-App-.git
cd DailyScope
```

## Build & run (debug)
```powershell
.\gradlew.bat clean assembleDebug
```
Open the project in Android Studio and run on a device/emulator.

## Permissions
- Notification permission (POST_NOTIFICATIONS) is requested when enabling notifications in Settings on Android 13+.
- Internet access is required for news fetching; WorkManager jobs require network connectivity.

## Background jobs
- Controlled via Settings → Data and Sync / Notifications / Storage:
  - `background_fetch` → schedules `NewsSyncWorker` every 8h.
  - `breaking_news_notification` → schedules `BreakingNewsWorker` every 3h.
  - `auto_cleanup` → schedules `CleanupOldArticlesWorker` every 3 days to delete old rows.

## Troubleshooting
- If paging shows empty data, trigger refresh from Home or verify API key validity.
- For Room schema changes during development, uninstall the app or bump `fallbackToDestructiveMigration()` behavior if needed.
- If WorkManager jobs do not run, ensure battery optimizations are disabled and constraints (network) are met.

