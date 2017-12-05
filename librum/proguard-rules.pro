-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

-dontwarn android.support.v4.app.**
-dontwarn android.support.v7.widget.**

-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

-keepattributes EnclosingMethod

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# ok http
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.*

# okio
-dontwarn okio.
-dontwarn okio.**

# retrofit and okhttp
-dontwarn okhttp3.**
-dontwarn okhttp3.internal.cache.**
-dontwarn retrofit2.**
-dontwarn retrofit2.adapter.rxjava2.**
-dontwarn retrofit2.converter.**
-dontwarn retrofit2.converter.gson.package-info

-dontwarn com.fasterxml.jackson.databind.ext.**
-dontwarn com.fasterxml.jackson.databind.**
-keep class com.fasterxml.jackson.annotation.** { *; }
-keep class com.fasterxml.jackson.annotation.** { *** PUBLIC_ONLY; }

# reactive x
-dontwarn io.reactivex.rxkotlin.**
-dontwarn io.reactivex.subjects.**
-dontwarn io.reactivex.internal.**
-dontwarn io.reactivex.**

# base module
-dontwarn com.brck.moja.base.**
-dontnote com.brck.moja.base.**

# jetbrains
-dontnote org.jetbrains.anko.**
-dontwarn org.jetbrains.anko.**

# jakewharton
-dontwarn com.jakewharton.rxbinding2.**
-dontnote com.jakewharton.rxbinding2.**

# room
-dontwarn android.arch.persistence.room.**
-dontnote android.arch.persistence.room.**