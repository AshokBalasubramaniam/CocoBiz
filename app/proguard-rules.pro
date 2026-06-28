-keep class com.cocobiz.app.data.local.entity.** { *; }
-keep class com.cocobiz.app.domain.model.** { *; }
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keep class androidx.room.** { *; }
-dontwarn javax.annotation.**
