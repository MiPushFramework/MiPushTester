# MiPush
-keep class moe.yuuta.mipushtester.push.InternalPushReceiver {*;}
-keep class moe.yuuta.mipushtester.push.PushReceiver {*;}
-dontwarn com.xiaomi.push.**
-dontwarn com.xiaomi.mipush.**
-keepclassmembers class com.xiaomi.mipush.sdk.MiPushMessage {
    private <fields>;
}

# OkHttp3 rules comes from https://github.com/square/okhttp/blob/master/okhttp/src/main/resources/META-INF/proguard/okhttp3.pro
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# GSON
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# API
-keep class moe.yuuta.mipushtester.push.PushRequest {
 *;
 <fields>;
}
-keep class moe.yuuta.mipushtester.update.Update {
 *;
 <fields>;
}

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.-KotlinExtensions