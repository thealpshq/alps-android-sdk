# Keep Gson classes
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep interface com.google.gson.TypeAdapterFactory
-keep interface com.google.gson.JsonSerializer
-keep interface com.google.gson.JsonDeserializer

# Keep Retrofit interfaces
-keep interface * extends retrofit2.Call
-keep class * implements retrofit2.Callback

# Keep Pusher classes
-keep class com.pusher.** { *; }

# Keep our data classes
-keep class com.alps.sdk.network.** { *; }
-keep class com.alps.sdk.** { *; }
