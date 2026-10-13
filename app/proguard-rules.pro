# PingTest — ProGuard kuralları
# Şu an minify kapalı; ileride açılırsa org.json ve reflection kullanan
# alanların korunması için temel kurallar buraya eklenir.
-keep class org.json.** { *; }
-dontwarn org.json.**
