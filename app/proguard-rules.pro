# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ~/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard/index.html

# Add any custom Keep rules here:

# Hilt rules
-keep public class * extends android.app.Service
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.view.View
-keep class  **.*_HiltModules* { *; }
-keep class com.google.dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Retrofit / OkHttp / Moshi
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep @interface retrofit2.http.** { *; }

-keep class com.squareup.moshi.** { *; }
-keep @interface com.squareup.moshi.** { *; }

# Moshi Kotlin Reflection rules
-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class * extends com.squareup.moshi.JsonAdapter
-keep class * extends com.squareup.moshi.JsonAdapter$Factory

# Keep network models to avoid obfuscation of field names used by Moshi reflection
-keep class io.github.alxiw.punkapp.data.remote.model.** { *; }
-keepclassmembers class io.github.alxiw.punkapp.data.remote.model.** {
    <fields>;
}

# Coil
-keep class coil.** { *; }
-dontwarn coil.**
