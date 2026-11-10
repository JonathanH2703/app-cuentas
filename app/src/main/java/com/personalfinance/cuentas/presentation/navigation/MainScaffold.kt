package com.personalfinance.cuentas.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.personalfinance.cuentas.core.ui.theme.FinancialGreen
import com.personalfinance.cuentas.core.ui.theme.FinancialRed
import com.personalfinance.cuentas.presentation.accounts.AccountsScreen
import com.personalfinance.cuentas.presentation.accounts.AccountsViewModel
import com.personalfinance.cuentas.presentation.credit.CreditScreen
import com.personalfinance.cuentas.presentation.credit.CreditViewModel
import com.personalfinance.cuentas.presentation.dashboard.DashboardScreen
import com.personalfinance.cuentas.presentation.dashboard.DashboardViewModel
import com.personalfinance.cuentas.presentation.incomes.IncomesScreen
import com.personalfinance.cuentas.presentation.incomes.IncomesViewModel
import com.personalfinance.cuentas.presentation.receivables.ReceivablesScreen
import com.personalfinance.cuentas.presentation.receivables.ReceivablesViewModel
import com.personalfinance.cuentas.presentation.transactions.QuickExpenseSheet
import com.personalfinance.cuentas.presentation.transactions.QuickIncomeSheet
import com.personalfinance.cuentas.presentation.transactions.TransactionsScreen
import com.personalfinance.cuentas.presentation.transactions.TransactionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    modifier: Modifier = Modifier
) {
    var isSettingsOpen by remember { mutableStateOf(false) }
    var showFabMenu by remember { mutableStateOf(false) }
    var showQuickExpenseSheet by remember { mutableStateOf(false) }
    var showQuickIncomeSheet by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val accountsViewModel: AccountsViewModel = hiltViewModel()
    val creditViewModel: CreditViewModel = hiltViewModel()
    val transactionsViewModel: TransactionsViewModel = hiltViewModel()
    val incomesViewModel: IncomesViewModel = hiltViewModel()
    val receivablesViewModel: ReceivablesViewModel = hiltViewModel()
    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val settingsViewModel: com.personalfinance.cuentas.presentation.settings.SettingsViewModel = hiltViewModel()
    val eventsViewModel: com.personalfinance.cuentas.presentation.events.EventsViewModel = hiltViewModel()

    val accounts by accountsViewModel.accounts.collectAsState()
    val categories by transactionsViewModel.categories.collectAsState()
    val anchorOptions by transactionsViewModel.anchorOptions.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val incomeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val navItems = listOf(
        Screen.Dashboard,
        Screen.Accounts,
        Screen.Credit,
        Screen.Transactions,
        Screen.Incomes,
        Screen.Receivables,
        Screen.Events
    )

    val pagerState = rememberPagerState(initialPage = 0) { navItems.size }

    // Cierra el menú del FAB al cambiar de pestaña
    LaunchedEffect(pagerState.currentPage) {
        showFabMenu = false
    }

    // El botón Atrás del sistema cierra el menú del FAB si está abierto
    BackHandler(enabled = showFabMenu) { showFabMenu = false }

    Scaffold(
        bottomBar = {
            if (!isSettingsOpen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    navItems.forEachIndexed { index, screen ->
                        NavigationBarItem(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = {
                                Text(
                                    text = screen.title,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (!isSettingsOpen) {
                ExpandableFabMenu(
                    expanded = showFabMenu,
                    onToggle = { showFabMenu = !showFabMenu },
                    onExpenseClick = {
                        showFabMenu = false
                        showQuickExpenseSheet = true
                    },
                    onIncomeClick = {
                        showFabMenu = false
                        showQuickIncomeSheet = true
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isSettingsOpen) {
                com.personalfinance.cuentas.presentation.settings.SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { isSettingsOpen = false }
                )
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> DashboardScreen(
                            viewModel = dashboardViewModel,
                            onOpenSettings = { isSettingsOpen = true }
                        )
                        1 -> AccountsScreen(viewModel = accountsViewModel)
                        2 -> CreditScreen(viewModel = creditViewModel)
                        3 -> TransactionsScreen(viewModel = transactionsViewModel)
                        4 -> IncomesScreen(viewModel = incomesViewModel)
                        5 -> ReceivablesScreen(viewModel = receivablesViewModel)
                        6 -> com.personalfinance.cuentas.presentation.events.EventsScreen(viewModel = eventsViewModel)
                    }
                }
            }

            // Capa transparente para cerrar el menú del FAB al tocar fuera de él
            if (showFabMenu && !isSettingsOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { showFabMenu = false }
                )
            }
        }
    }

    if (showQuickExpenseSheet) {
        QuickExpenseSheet(
            sheetState = sheetState,
            accounts = accounts,
            categories = categories,
            anchorOptions = anchorOptions,
            onDismiss = { showQuickExpenseSheet = false },
            onSaveExpense = { accountId, categoryId, amountCents, note, linkedDebtId, linkedRecurringItemId ->
                transactionsViewModel.recordQuickExpense(
                    accountId = accountId,
                    categoryId = categoryId,
                    amountCents = amountCents,
                    note = note,
                    linkedDebtId = linkedDebtId,
                    linkedRecurringItemId = linkedRecurringItemId
                )
            }
        )
    }

    if (showQuickIncomeSheet) {
        QuickIncomeSheet(
            sheetState = incomeSheetState,
            accounts = accounts,
            onDismiss = { showQuickIncomeSheet = false },
            onSaveIncome = { accountId, amountCents, description ->
                incomesViewModel.registerQuickIncome(description, amountCents, accountId)
            }
        )
    }
}

/**
 * FAB principal que, al expandirse, ofrece registrar un Gasto o un Ingreso.
 */
@Composable
private fun ExpandableFabMenu(
    expanded: Boolean,
    onToggle: () -> Unit,
    onExpenseClick: () -> Unit,
    onIncomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FabMenuAction(
                    label = "Ingreso",
                    contentDescription = "Registrar Ingreso",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = FinancialGreen,
                    onClick = onIncomeClick
                )
                FabMenuAction(
                    label = "Gasto",
                    contentDescription = "Registrar Gasto",
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    color = FinancialRed,
                    onClick = onExpenseClick
                )
            }
        }

        val rotation by animateFloatAsState(
            targetValue = if (expanded) 45f else 0f,
            label = "fab_rotation"
        )
        FloatingActionButton(
            onClick = onToggle,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = if (expanded) "Cerrar menú de registro" else "Registrar movimiento",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
private fun FabMenuAction(
    label: String,
    contentDescription: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 2.dp
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = color,
            contentColor = Color.White
        ) {
            Icon(imageVector = icon, contentDescription = contentDescription)
        }
    }
}
