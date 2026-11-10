package com.personalfinance.cuentas.domain.reminders

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val PREFS_NAME = "finance_reminder_prefs"
        private const val KEY_CARD_REMINDERS = "card_reminders_enabled"
        private const val KEY_DAILY_SUMMARY = "daily_summary_enabled"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val _isCardRemindersEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_CARD_REMINDERS, true)
    )
    val isCardRemindersEnabled: StateFlow<Boolean> = _isCardRemindersEnabled.asStateFlow()

    private val _isDailySummaryEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_DAILY_SUMMARY, false)
    )
    val isDailySummaryEnabled: StateFlow<Boolean> = _isDailySummaryEnabled.asStateFlow()

    fun isCardRemindersEnabled(): Boolean = prefs.getBoolean(KEY_CARD_REMINDERS, true)

    fun setCardRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_CARD_REMINDERS, enabled).apply()
        _isCardRemindersEnabled.value = enabled
    }

    fun isDailySummaryEnabled(): Boolean = prefs.getBoolean(KEY_DAILY_SUMMARY, false)

    fun setDailySummaryEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DAILY_SUMMARY, enabled).apply()
        _isDailySummaryEnabled.value = enabled
    }

    fun getReminderHour(): Int = prefs.getInt(KEY_REMINDER_HOUR, 8)

    fun setReminderHour(hour: Int) {
        prefs.edit().putInt(KEY_REMINDER_HOUR, hour).apply()
    }
}
