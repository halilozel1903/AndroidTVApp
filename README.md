# Android TV App

Sample Android TV application built with the [AndroidX Leanback](https://developer.android.com/training/tv/start/start) library. It demonstrates a TV-optimized browse experience: category rows, poster cards, a details screen with related items, and remote-friendly navigation.

The app ships with hard-coded sample movies and series metadata and loads poster images from the network at runtime (Picasso). It is intended as a learning reference, not a production streaming client.

## Features

- **Browse home** — Leanback `BrowseFragment` with a *Movies* text grid row and a *Series* card row.
- **Details** — Full-width overview for a selected series, plus a related-videos row (sample data).
- **Dynamic backgrounds** — Background art updates when rows and cards are focused (`PicassoBackgroundManager`).
- **Error flow** — Selecting *ErrorFragment* in the Movies row opens a sample error screen.
- **TV-only** — Declares `leanback` as required and uses the `LEANBACK_LAUNCHER` intent category.

## Requirements

| Tool | Version |
|------|---------|
| JDK | 17 |
| Android Gradle Plugin | 8.7.x (see root `build.gradle`) |
| Gradle | 8.9 (wrapper) |
| Android SDK | API 34 (`compileSdk` / `targetSdk`) |
| Minimum device API | 24 |

Install [Android Studio](https://developer.android.com/studio) (recommended) or the [command-line SDK tools](https://developer.android.com/studio#command-tools). Set `sdk.dir` in `local.properties` (Android Studio creates this automatically).

## Setup

1. Clone the repository.
2. Open the project in Android Studio, or ensure `local.properties` points at your SDK:

   ```properties
   sdk.dir=/path/to/Android/sdk
   ```

3. Sync Gradle and download dependencies.

## Build and run

### Command line

```bash
./gradlew assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/`.

Release build (no signing configured in-repo):

```bash
./gradlew assembleRelease
```

### Android TV emulator

1. In Android Studio: **Tools → Device Manager → Create device**.
2. Choose a **TV** hardware profile (for example Android TV (1080p)).
3. Select a system image with API 24 or higher (API 34 matches this project).
4. Run the **app** configuration on the TV emulator.

### Physical Android TV

Enable [developer options and USB debugging](https://developer.android.com/studio/debug/dev-options) on the device, connect via ADB, and install the debug APK:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

The app appears in the Android TV launcher under **AndroidTVTutorial** (see `app_name` in `strings.xml`).

## Architecture overview

```
MainActivity
  └── MainFragment (BrowseFragment)
        ├── Movies row — grid text items (includes ErrorFragment demo)
        └── Series row — Movie cards → DetailsActivity

DetailsActivity
  └── VideoDetailsFragment (DetailsFragment)
        ├── Details overview (poster, title, studio, description)
        └── Related videos row (sample Movie list)

ErrorActivity
  └── ErrorFragment (sample error UI)
```

| Component | Role |
|-----------|------|
| `MainFragment` | Builds browse rows and handles focus / click navigation. |
| `CardPresenter` | Leanback presenter for series poster cards. |
| `VideoDetailsFragment` | Loads poster bitmaps asynchronously and builds the details UI. |
| `PicassoBackgroundManager` | Cross-fades background images on the browse and details screens. |
| `Movie` | Serializable model passed to the details screen via intent extras. |

Package: `com.example.androidtvapp`. Application ID: `com.example.androidtvapp` (configurable in `app/build.gradle`).

## Configuration

- **App label** — `app/src/main/res/values/strings.xml` (`app_name`).
- **Sample content** — Row titles, descriptions, and image URLs are defined in `MainFragment` and `VideoDetailsFragment` (replace with your own data source for real apps).
- **Theming** — Leanback theme in `res/values/styles.xml` and colors in `colors.xml`.
- **ProGuard** — Rules in `app/proguard-rules.pro` (minification is off for release in the default config).

## Screenshots

Browse and details flows on Android TV:

| Movies browse | Series row |
|---------------|------------|
| ![Movies browse](docs/screenshots/pic1.png) | ![Series row](docs/screenshots/pic2.png) |

| Series cards | Details |
|--------------|---------|
| ![Series cards](docs/screenshots/pic3.png) | ![Details](docs/screenshots/pic8.png) |

Additional captures: `docs/screenshots/`.

## Contributing

Issues and pull requests are welcome. For larger changes, open an issue first to discuss scope. Keep changes focused and match existing Java / Leanback patterns.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).

## Acknowledgments

Sample poster URLs point to third-party sites for demonstration only. Replace them with licensed assets in derivative work.
