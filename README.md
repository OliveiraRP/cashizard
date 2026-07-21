# Cashizard

A personal budget manager for Android & iOS, built with Kotlin Multiplatform and
Compose Multiplatform (single shared UI).

## Modules

* **[/composeApp](./composeApp/src)** — the Kotlin Multiplatform module holding all shared
  code and UI. It is an Android library (`com.android.kotlin.multiplatform.library`) that
  also produces the iOS `ComposeApp` framework.
  - [commonMain](./composeApp/src/commonMain/kotlin) — code common to all targets (data /
    domain / presentation layers live here).
  - [androidMain](./composeApp/src/androidMain/kotlin) / [iosMain](./composeApp/src/iosMain/kotlin)
    — platform-specific `actual` implementations.
* **[/androidApp](./androidApp/src)** — thin Android application shell (`MainActivity`) that
  depends on `:composeApp`.
* **[/iosApp](./iosApp/iosApp)** — the iOS application entry point (SwiftUI hosting the
  `ComposeApp` framework).

## Configuration

Supabase credentials are read from `local.properties` (gitignored) via BuildKonfig.

```
supabase.url=https://YOUR-PROJECT.supabase.co
supabase.anonKey=YOUR-ANON-KEY
```

The build fails with a clear message if these are missing.

## Running the apps

- Android: `./gradlew :androidApp:assembleDebug` (or run from the IDE run widget).
- iOS: open [/iosApp](./iosApp) in Xcode and run it there (requires macOS).

---
