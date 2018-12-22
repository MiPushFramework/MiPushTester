-keep class moe.yuuta.mipushtester.push.PushReceiver {*;}
-dontwarn com.xiaomi.push.**
-dontwarn com.xiaomi.mipush.**

# OkHttp3 rules comes from https://github.com/square/okhttp/blob/master/okhttp/src/main/resources/META-INF/proguard/okhttp3.pro
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform