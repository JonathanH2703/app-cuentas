package com.personalfinance.cuentas

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.personalfinance.cuentas.core.security.SecurityLifecycleObserver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var securityLifecycleObserver: SecurityLifecycleObserver

    @Inject
    lateinit var reminderScheduler: com.personalfinance.cuentas.domain.reminders.ReminderScheduler

    override fun onCreate() {
        super.onCreate()

        // 1. Cargar librerías nativas de SQLCipher para cifrado a nivel de hardware
        System.loadLibrary("sqlcipher")

        // 2. Registrar observador del ciclo de vida del proceso para el autobloqueo de seguridad
        ProcessLifecycleOwner.get().lifecycle.addObserver(securityLifecycleObserver)

        // 3. Programar recordatorios locales de corte y pago de tarjetas con WorkManager
        reminderScheduler.scheduleDailyReminders()
    }
}
