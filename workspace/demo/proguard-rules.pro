# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 注意事项：
#
# ① jni方法不可混淆，方法名需与native方法保持一致；
# ② 反射用到的类不混淆，否则反射可能出问题；
# ③ 四大组件、Application子类、Framework层下的类、自定义的View默认不会被混淆，无需另外配置；
# ④ WebView的JS调用接口方法不可混淆；
# ⑤ 注解相关的类不混淆；
# ⑥ GSON、Fastjson等解析的Bean数据类不可混淆；
# ⑦ 枚举enum类中的values和valuesof这两个方法不可混淆(反射调用)；
# ⑧ 继承Parceable和Serializable等可序列化的类不可混淆；
# ⑨ 第三方库或SDK，请参考第三方提供的混淆规则，没提供的话，建议第三方包全部不混淆；

# <基本指令>
# 代码混淆的压缩比例，值介于0-7，默认5
-optimizationpasses 5
# 不跳过非公共库的类
#-dontskipnonpubliclibraryclasses
# 不跳过非公共库类的成员变量
#-dontskipnonpubliclibraryclassmembers
# 指定混淆时采用的算法
#-optimizations !code/simplification/cast, !field/*, !class/merging/*
# 避免混淆注解、内部类、泛型、匿名类
#-keepattributes *Annotation*, InnerClasses, Signature, EnclosingMethod
# 保留行号
#-keepattributes SourceFile, LineNumberTable
# 忽略警告
-ignorewarnings
# 记录生成日志数据build时在本项目根目录输出Apk包内所有class的内部结构
#-dump build/print/class_files.txt
# 生成未混淆的类和成员
#-printseeds build/print/seeds.txt
# 生成从Apk中删除的代码
#-printusage build/print/unused.txt
# 生成原类名与混淆后类名的映射文件
#-printmapping build/print/mapping.txt
# </基本指令>

# <基础组件>
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.app.backup.BackupAgentHelper
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.preference.Preference
#-keep public class * extends android.support.multidex.MultiDexApplication
#-keep public class * extends android.view.View
#-keep public class com.android.vending.licensing.ILicensingService
#-keep class android.support.** {*;}
# </基础组件>

# <View相关>
# 不混淆自定义控件
#-keep public class * extends android.view.View {
#    *** get*();
#    void set*(***);
#    public <init>(android.content.Context);
#    public <init>(android.content.Context, android.util.AttributeSet);
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#    public <init>(android.content.Context, android.util.AttributeSet, int, int);
#}
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet);
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#    public <init>(android.content.Context, android.util.AttributeSet, int, int);
#}
#-keepclassmembers class * {
#    public void *(android.view.View);
#}
# 不混淆带有JavaScript的WebView自定义视图
#-keepclassmembers class com.framework.demo.widget.CustomWebview {
#    public *;
#}
# 不混淆内部类中的所有public内容
#-keep class com.framework.demo.widget.CustomView$OnClickInterface {
#    public *;
#}
# </View相关>

# <序列化相关>
# 不混淆实现了Serializable接口的类成员，此处只是演示
#-keepclassmembers class * implements java.io.Serializable {
#    static final long serialVersionUID;
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}
# 不混淆实现了Serializable接口的类成员
#-keep public class * implements java.io.Serializable {*;}
# 不混淆实现了parcelable接口的类成员
#-keep class * implements android.os.Parcelable {
#    public static final android.os.Parcelable$Creator *;
#}
# </序列化相关>

# <R文件相关>
# 不混淆资源类
#-keep class **.R$* {*;}
# </R文件相关>

# <枚举类相关>
# 不混淆枚举类中的values和valuesof这两个方法
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
# </枚举类相关>

# <Native相关>
# 不混淆native方法
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
# </Native相关>

# <Package相关>
# 不混淆类名中包含了"entity"的类，及类中内容
#-keep class **.*entity*.** {*;}

# 不混淆指定包名下的类名，不包括子包下的类名
#-keep class com.framework.demo*

# 不混淆指定包名下的类名，及类里的内容
#-keep class com.framework.demo* {*;}

# 不混淆指定包名下的类名，包括子包下的类名
#-keep class com.framework.demo**

# 不混淆某个类的子类
#-keep public class * extends com.framework.demo.Test

# 不混淆实现了某个接口的类
#-keep class * implements com.framework.demo.TestImpl
# </Package相关>

# <OkHttp3.x>
#-dontwarn com.squareup.okhttp3.**
#-keep class com.squareup.okhttp3.** {*;}
#-dontwarn okio.**
# </OkHttp3.x>

# <Retrofit2.x>
#-dontnote retrofit2.Platform
#-dontwarn retrofit2.Platform$Java8
#-keepattributes Signature
#-keepattributes Exceptions
#-dontwarn okio.**
# </Retrofit2.x>

# <Gson>
#-keep class com.google.gson.** {*;}
#-keep class com.google.** {*;}
#-keep class sun.misc.Unsafe {*;}
#-keep class com.google.gson.stream.** {*;}
#-keep class com.google.gson.examples.android.model.** {*;}
# </Gson>

# <Glide>
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#    **[] $VALUES;
#    public *;
#}
# </Glide>