# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizationpasses 5          # 指定代码的压缩级别
-dontusemixedcaseclassnames   # 是否使用大小写混合
-dontpreverify           # 混淆时是否做预校验
-verbose                # 混淆时是否记录日志
-ignorewarnings                # 抑制警告

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法

-keep public class * extends android.app.Activity      # 保持哪些类不被混淆
-keep public class * extends android.app.Application   # 保持哪些类不被混淆
-keep public class * extends android.app.Service       # 保持哪些类不被混淆
-keep public class * extends android.content.BroadcastReceiver  # 保持哪些类不被混淆
-keep public class * extends android.content.ContentProvider    # 保持哪些类不被混淆
-keep public class * extends android.app.backup.BackupAgentHelper # 保持哪些类不被混淆
-keep public class * extends android.preference.Preference        # 保持哪些类不被混淆
-keep public class com.android.vending.licensing.ILicensingService    # 保持哪些类不被混淆

-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
    native <methods>;
}
-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class *{
    public void call(android.webkit.WebView, java.lang.String);
}

-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes Deprecated
-keepattributes SourceFile
-keepattributes LineNumberTable
-keepattributes EnclosingMethod  
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

-keep public class com.tencent.bugly.**{*;}

# Retain generated class which implement ViewBinder.
-keep public class * implements butterknife.internal.ViewBinder { public <init>(); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinder.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

-keepclassmembers class ** {
    @com.hynet.heebit.components.permission.annotation.PermissionGrant <methods>;
}
-keepclassmembers class ** {
    @com.hynet.heebit.components.permission.annotation.PermissioDenied <methods>;
}

-keep class android.support.multidex.** {
    *;
}

-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

#个推
-dontwarn com.igexin.**
-keep class com.igexin.** { *; }
-keep class org.json.** { *; }

#腾讯移动分析
-keep class com.tencent.stat.**  {* ;}
-keep class com.tencent.mid.**  {* ;}

#语音播放
-keep class com.iflytek.**{*;}

#eventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#友盟
-keep public class javax.**
#-dontwarn android.support.v4.**
-dontwarn androidx.**
-dontwarn com.google.android.material.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.tencent.**
-dontwarn twitter4j.**
-dontwarn com.facebook.**
-keep enum com.facebook.**
-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**
-keep class com.umeng.socialize.** { *; }
-keep class com.facebook.**
-keep class com.facebook.** { *; }
-keep class com.umeng.** { *; }
-keep public class com.tencent.** {*;}
-keep class com.tencent.** { *; }
-keep class UMMoreHandler{*;}
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
-keep public class android.webkit.**
-keep class twitter4j.** { *; }
-keep class com.kakao.** { *; }
-keep public class com.umeng.com.umeng.soexample.R$*{
    public static final int *;
}
-keep public class com.linkedin.android.mobilesdk.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
-keep class com.umeng.socialize.impl.ImageImpl {*;}
-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
   *;
}
-keep class com.android.dingtalk.share.ddsharemodule.** { *; }

-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}
-keep class com.hynet.heebit.components.screenshot.** {*;}