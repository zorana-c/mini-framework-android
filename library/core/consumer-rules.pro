# ==================================== <Library> ====================================
# 不混淆指定包的类，包括类的内容
-dontwarn com.navigation.**
-keep class com.navigation.** {*;}
# 不混淆指定包的类，包括类的内容
-dontwarn com.framework.core.**
-keep class com.framework.core.** {*;}

# 不混淆实现UIRoute的子类，包括类内容
-keep class * implements com.framework.core.route.UIRoute {*;}
# 不混淆实现UIModelInterface的子类，包括类内容
-keep class * implements com.framework.core.bean.UIModelInterface {*;}
# 不混淆实现UIPageOptions的子类，包括类内容
-keep class * implements com.framework.core.content.UIPageOptions {*;}
# 不混淆实现HttpServiceRepository的子类，包括类内容
-keep class * implements com.framework.core.http.HttpServiceRepository {*;}
# ==================================== <Library> ====================================

# ==================================== <第三方SDK> ====================================
# <OkHttp3.x>
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** {*;}
-dontwarn okio.**

# <Retrofit2.x>
# 不混淆OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** {*;}

# 不混淆Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** {*;}

# 不混淆RxJava and RxAndroid
-dontwarn io.reactivex.rxjava3.**
-keep class io.reactivex.rxjava3.** {*;}

# <Gson>
-dontwarn com.google.gson.**
-keep class com.google.gson.** {*;}

# <Glide>
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
# ==================================== <第三方SDK> ====================================