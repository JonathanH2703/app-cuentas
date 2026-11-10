package com.personalfinance.cuentas.presentation.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.personalfinance.cuentas.presentation.dashboard.DashboardScreen
import com.personalfinance.cuentas.presentation.dashboard.DashboardViewModel
import com.personalfinance.cuentas.presentation.lock.LockScreen

@Composable
fun AppNavigation(
    isAppLocked: Boolean,
    lockErrorMessage: String?,
    onUnlockRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    Crossfade(
        targetState = isAppLocked,
        label = "AppLockCrossfade"
    ) { locked ->
        if (locked) {
            LockScreen(
                errorMessage = lockErrorMessage,
                onUnlockClick = onUnlockRequested,
                modifier = modifier
            )
        } else {
            MainScaffold(modifier = modifier)
        }
    }
}
