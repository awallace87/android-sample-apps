# Android Boilerplate Project

This project is a boilerplate for Android projects. It includes a lot of libraries and tools that
are used in most of the Android projects. The main goal of this project is to reduce the time that
is required to start a new project with the most common (and latest stable) libraries and tools.

## Libraries and tools included:
- [AndroidX](https://developer.android.com/jetpack/androidx)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin](https://kotlinlang.org/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
- [Dagger 2](https://dagger.dev/)
- [Hilt](https://dagger.dev/hilt/)
- [Room](https://developer.android.com/training/data-storage/room)
- [Proto DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [OkHttp](https://square.github.io/okhttp/)
- [Timber Logging](https://github.com/JakeWharton/timber)
- [Moshi](https://github.com/square/moshi)
- [Coil](https://coil-kt.github.io/coil/)
- [Accompanist](https://google.github.io/accompanist/)
- [Google Fonts](https://fonts.google.com/)
- [Material3 Theming](https://material.io/design/material3)
- [Truth Assertion Library](https://truth.dev/)

## How to use this project
* Download this repository as a zip 
* Change the package name
  * Rename packages in main, androidTest, and test directories
  * In 'app/build.gradle' file, 'packageName' and 'testInstrumentationRunner' fields
  * In 'src/main/AndroidManifest.xml' file, the 'android:name' field
* Replace the example code with your app code following the same architecture.
* Update README with information relevant to the new project.
* Update LICENSE to match the requirements of the new project.
