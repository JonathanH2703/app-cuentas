package com.personalfinance.cuentas.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Inicio", Icons.Default.Dashboard)
    object Accounts : Screen("accounts", "Cuentas", Icons.Default.AccountBalance)
    object Credit : Screen("credit", "Créditos", Icons.Default.CreditCard)
    object Transactions : Screen("transactions", "Historial", Icons.AutoMirrored.Filled.ReceiptLong)
    object Incomes : Screen("incomes", "Ingresos", Icons.AutoMirrored.Filled.TrendingUp)
    object Receivables : Screen("receivables", "Cobros", Icons.Default.Handshake)
    object Events : Screen("events", "Fechas", Icons.Default.Celebration)
    object Settings : Screen("settings", "Ajustes", Icons.Default.Settings)
}
