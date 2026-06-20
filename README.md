# Alps Android SDK

Native Kotlin SDK for integrating the Alps customer support widget into Android applications.

## Installation

### Gradle (JitPack)

Add JitPack to your `settings.gradle`:

```gradle
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency to your app `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.tryalps:alps-android-sdk:1.0.0'
}
```

## Quick Start

### 1. Configure the SDK

In your `Application` class or main `Activity`:

```kotlin
import com.alps.sdk.Alps

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Alps.configure(
            context = this,
            widgetKey = "YOUR_WIDGET_KEY"
        )
    }
}
```

Or in your `MainActivity`:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Alps.configure(
        context = this,
        widgetKey = "YOUR_WIDGET_KEY"
    )
    setContentView(R.layout.activity_main)
}
```

### 2. Show the Chat Panel

```kotlin
// Open the chat panel in an Activity
Alps.show(this)
```

### 3. Identify Users (Optional)

After a user logs in:

```kotlin
Alps.identify(
    name = "John Doe",
    email = "john@example.com"
)
```

### 4. Logout

Clear user identity:

```kotlin
Alps.logout()
```

## Features

- **Native UI**: Built with Android Material Design components
- **Conversation List**: View prior conversation history
- **Real-time Messaging**: Pusher-powered live message delivery
- **Knowledge Base**: Search and browse help articles
- **Pre-chat Form**: Collect visitor name and email
- **Responsive Design**: Full-screen modal with ViewPager2 tabs

## API Reference

### Alps (Main Entry Point)

```kotlin
object Alps {
    fun configure(
        context: Context,
        widgetKey: String,
        userName: String? = null,
        userEmail: String? = null
    )

    fun show(activity: Activity)

    fun hide()

    fun identify(name: String, email: String)

    fun logout()

    fun getConfig(): AlpsConfig?

    fun getApiClient(): AlpsApiClient?

    fun getContext(): Context?
}
```

## Configuration

Pass configuration via the `configure()` method:

```kotlin
Alps.configure(
    context = this,
    widgetKey = "your_widget_key",
    userName = "John Doe",      // Optional
    userEmail = "john@alps.app" // Optional
)
```

## Requirements

- Android 7.0 (API 24) or higher
- Kotlin 1.9.20+
- AndroidX

## Dependencies

- **Retrofit 2.9** — REST API client
- **OkHttp 4.12** — HTTP client with logging
- **PusherAndroid** — Real-time messaging
- **Gson** — JSON serialization
- **AndroidX Material** — UI components

## Example App

See the `example/` directory for a complete working example with buttons to:
- Show the chat panel
- Identify a user
- Logout

## Permissions

The SDK requires the following permissions (added automatically):

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## License

Proprietary — © 2026 Alps
