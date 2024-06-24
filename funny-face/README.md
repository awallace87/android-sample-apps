# Android (Internal) Boilerplate Project

This project is a complete boilerplate for personal Android projects. This includes integrated
common libraries, common UI components, and shared Android /framework components (built for
Dagger/Hilt).

## Features
- Compatible with Android 8.0 (API level 26) and above.

## Libraries and tools included:

- [AndroidX](https://developer.android.com/jetpack/androidx)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material3 Theming and Components](https://m3.material.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
- [Dagger 2](https://dagger.dev/)
- [Hilt](https://dagger.dev/hilt/)
- [Room](https://developer.android.com/training/data-storage/room)
- [Proto DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [OkHttp](https://square.github.io/okhttp/)
- [Retrofit](https://square.github.io/retrofit/)
- [Timber Logging](https://github.com/JakeWharton/timber)
- [Picasso](https://square.github.io/picasso/)
- [Coil](https://coil-kt.github.io/coil/)
- [Accompanist - Permissions](https://google.github.io/accompanist/permissions/)
- [Google (Lazy Download) Fonts](https://developer.android.com/develop/ui/compose/text/fonts#downloadable-fonts)
- [Material Icons - Extended](https://developer.android.com/reference/kotlin/androidx/compose/material/icons/package-summary)
- [Material Adaptive UI](https://developer.android.com/develop/ui/compose/layouts/adaptive)
- [Kotlin Serialization](https://kotlinlang.org/docs/serialization.html)
- [Parcelize](https://developer.android.com/kotlin/parcelize)
- [Truth Assertion Library](https://truth.dev/)

## How to use this project

* Download this repository as a zip
* Change the package name to match your project
    * Rename packages in main, androidTest, and test directories
    * In 'app/build.gradle' file, 'packageName' and 'testInstrumentationRunner' fields
    * In 'src/main/AndroidManifest.xml' file, the 'android:name' field
    * In 'src/main/proto/settings/app_settings.proto' file, the 'java_package' field
    * Optional: In 'settings.gradle' file, the 'rootProject.name' field
* Replace the example code with your app code following the same architecture.
* Update README with information relevant to the new project.
* Update LICENSE to match the requirements of the new project.
