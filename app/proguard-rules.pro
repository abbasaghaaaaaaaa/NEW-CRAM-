# Add project specific ProGuard rules here.
-keep class com.example.persianflashcard.data.entity.** { *; }
-keepclassmembers class * {
    @androidx.room.* <fields>;
}
-dontwarn androidx.room.**
-dontwarn org.apache.commons.csv.**