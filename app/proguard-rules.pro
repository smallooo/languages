# okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# serializer

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.alashow.**$$serializer { *; }
-keepclassmembers class com.alashow.** {
    *** Companion;
}
-keepclasseswithmembers class com.alashow.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep @kotlinx.serialization.Serializer public class *

-keep class kotlin.Metadata { *; }

# end serializer

# web
-keepattributes JavascriptInterface

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# crashlytics recommendations
-keepattributes *Annotation*
-keepattributes LineNumberTable
-keep public class * extends java.lang.Exception

# fetch
-keep class com.tonyodev.fetch2.** {*;}
-keep class com.tonyodev.fetch2core.** {*;}
-keep interface com.tonyodev.fetch2.** {*;}
-keep interface com.tonyodev.fetch2core.** {*;}

# project
-keep class com.alashow.datmusic.domain.entities.** { *; }
-keep class com.alashow.datmusic.domain.models.** { *; }