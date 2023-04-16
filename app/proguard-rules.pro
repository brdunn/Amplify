# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
# http://developer.android.com/guide/developing/tools/proguard.html

# Gson
-keep class com.devdunnapps.amplify.data.models.* {
    !transient <fields>;
}

-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keepclassmembers class * extends com.google.protobuf.MessageLite {
    <fields>;
}

# androidx.fragment.app.FragmentContainerView Error
-keepnames class com.devdunnapps.amplify.domain.models.*
-keepnames class androidx.navigation.fragment.NavHostFragment
-keep class * extends androidx.fragment.app.Fragment{}

-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
