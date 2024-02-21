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
# ⑦ 枚举enum类中的values和valuesOf这两个方法不可混淆(反射调用)；
# ⑧ 继承Parceable和Serializable等可序列化的类不可混淆；
# ⑨ 第三方库或SDK，请参考第三方提供的混淆规则，没提供的话，建议第三方包全部不混淆；

# ==================================== <基本指令> ====================================
# 混淆记录日志
-verbose
# 混淆忽略警告
-ignorewarnings
# 混淆压缩比例，值介于0-7，默认5
-optimizationpasses 5
# 不使用大小写混淆
-dontusemixedcaseclassnames
# 不跳过非公共库的类
-dontskipnonpubliclibraryclasses
# 不跳过非公共库的类的成员
-dontskipnonpubliclibraryclassmembers
# 指定混淆时采用的算法
-optimizations !code/simplification/cast, !field/*, !class/merging/*
# ==================================== <基本指令> ====================================

# ==================================== <Android> ====================================
# <注解>
# 禁止混淆注解
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation {*;}

# <反射>
# 禁止混淆反射
-keepattributes EnclosingMethod

# <泛型>
# 禁止混淆泛型
-keepattributes Signature

# <异常>
# 禁止混淆异常
-keepattributes Exceptions
-keep class * extends java.lang.Exception {*;}

# <内部类>
# 禁止混淆内部类
-keepattributes Exceptions, InnerClasses

# <代码行号>
# 禁止混淆代码行号
-keepattributes SourceFile, LineNumberTable

# <基础组件>
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Dialog
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

# <Support组件>
-keep class android.support.** {*;}

# <视图组件>
# 不混淆自定义视图
-keep public class * extends android.view.View {
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
}
# 不混淆有参构造方法
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
}

# <R文件资源>
# 不混淆资源类
-keep class **.R$* {*;}

# <序列化>
# 不混淆实现了Serializable接口的类成员
-keep class * implements java.io.Serializable {*;}
# 不混淆实现了parcelable接口的类成员
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
    *;
}

# <Native>
# 不混淆native方法
-keepclasseswithmembernames class * {
    native <methods>;
}

# <枚举类>
# 不混淆枚举类中的values和valueOf这两个方法
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# ==================================== <Android> ====================================