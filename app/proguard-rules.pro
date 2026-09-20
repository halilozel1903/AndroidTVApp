# Leanback / AndroidX
-keep class androidx.leanback.** { *; }
-dontwarn androidx.leanback.**

# Picasso / Okio
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-keep class com.squareup.picasso.** { *; }

# Serializable model passed via Intent extras
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keepnames class com.example.androidtvapp.Movie

# BuildConfig
-keep class com.example.androidtvapp.BuildConfig { *; }

# Line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
