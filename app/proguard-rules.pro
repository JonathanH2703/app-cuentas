# Reglas para SQLCipher
-keep class net.zetetic.** { *; }
-dontwarn net.zetetic.**

# Reglas para Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# Reglas para BiometricPrompt
-keep class androidx.biometric.** { *; }

# Reglas para Hilt
-keep class * extends dagger.hilt.internal.GeneratedComponentManager { *; }
-keep class com.personalfinance.cuentas.** { *; }
