package com.personalfinance.cuentas.presentation.credit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.core.ui.theme.FinancialAmber
import com.personalfinance.cuentas.core.ui.theme.FinancialBlue
import com.personalfinance.cuentas.core.ui.theme.FinancialGreen
import com.personalfinance.cuentas.core.ui.theme.FinancialRed
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity
import com.personalfinance.cuentas.data.local.entities.CategoryEntity
import com.personalfinance.cuentas.data.local.entities.CreditCardEntity
import com.personalfinance.cuentas.data.local.entities.DebtEntity
import com.personalfinance.cuentas.data.local.relations.CreditCardWithAccount
import com.personalfinance.cuentas.domain.math.FinancialFormulas
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditScreen(
    viewModel: CreditViewModel,
    modifier: Modifier = Modifier
) {
    val cards by viewModel.cards.collectAsState()
    val debts by viewModel.debts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val recommendation by viewModel.cardRecommendation.collectAsState()
    val extraBudget by viewModel.extraMonthlyBudgetCents.collectAsState()
    val strategyComparison by viewModel.strategyComparison.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0) { 4 }

    var showCreateCardDialog by remember { mutableStateOf(false) }
    var showCreateDebtDialog by remember { mutableStateOf(false) }
    var cardForPurchase by remember { mutableStateOf<CreditCardWithAccount?>(null) }
    var cardForPurchases by remember { mutableStateOf<CreditCardWithAccount?>(null) }
    var cardForPayment by remember { mutableStateOf<CreditCardWithAccount?>(null) }
    var cardToEdit by remember { mutableStateOf<CreditCardWithAccount?>(null) }
    var debtForPayment by remember { mutableStateOf<DebtEntity?>(null) }
    var debtForAmortization by remember { mutableStateOf<DebtEntity?>(null) }
    var debtToEdit by remember { mutableStateOf<DebtEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Cabecera Principal
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Créditos y Deudas",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Desliza para moverte entre tarjetas, préstamos, simulador y estrategias",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pestañas Superiores sincronizadas con deslizamiento de pantalla
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    text = { Text("Tarjetas (${cards.size})", maxLines = 1, softWrap = false) }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    text = { Text("Préstamos (${debts.filter { !it.debt.isSettled }.size})", maxLines = 1, softWrap = false) }
                )
                Tab(
                    selected = pagerState.currentPage == 2,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } },
                    text = { Text("Simulador", maxLines = 1, softWrap = false) }
                )
                Tab(
                    selected = pagerState.currentPage == 3,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(3) } },
                    text = { Text("Estrategias", maxLines = 1, softWrap = false) }
                )
            }

            // Contenido deslizable entre pestañas con el dedo
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> CreditCardsTab(
                        cards = cards,
                        recommendation = recommendation,
                        onNewCardClick = { showCreateCardDialog = true },
                        onNewPurchaseClick = { cardForPurchase = it },
                        onPayCardClick = { cardForPayment = it },
                        onEditCardClick = { cardToEdit = it },
                        onViewPurchasesClick = { cardForPurchases = it },
                        viewModel = viewModel
                    )
                    1 -> DebtsTab(
                        debts = debts.map { it.debt },
                        onNewDebtClick = { showCreateDebtDialog = true },
                        onPayDebtClick = { debtForPayment = it },
                        onViewAmortization = { debtForAmortization = it },
                        onEditDebtClick = { debtToEdit = it }
                    )
                    2 -> ExtraPaymentSimulatorTab(
                        debts = debts.map { it.debt }.filter { !it.isSettled },
                        viewModel = viewModel
                    )
                    3 -> DebtStrategyTab(
                        comparison = strategyComparison,
                        currentBudget = extraBudget,
                        onBudgetChanged = { viewModel.setExtraMonthlyBudget(it) }
                    )
                }
            }
        }
    }

    // Diálogos de creación y acciones
    if (showCreateCardDialog) {
        CreateCreditCardDialog(
            onDismiss = { showCreateCardDialog = false },
            onConfirm = { name, limitCents, cutDay, payDay, ea, feeCents, digits, revolving, insurance, type ->
                viewModel.createCreditCard(name, limitCents, cutDay, payDay, ea, feeCents, digits, revolving, insurance, type)
                showCreateCardDialog = false
            }
        )
    }

    if (showCreateDebtDialog) {
        CreateDebtDialog(
            onDismiss = { showCreateDebtDialog = false },
            onConfirm = { name, principal, ea, dueDay, months, installment, insurance ->
                viewModel.createDebt(name, principal, ea, dueDay, months, installment, insurance)
                showCreateDebtDialog = false
            }
        )
    }

    cardForPurchase?.let { card ->
        RecordCardPurchaseDialog(
            card = card,
            categories = categories,
            onDismiss = { cardForPurchase = null },
            onConfirm = { merchant, amountCents, installments, categoryId, isInterestFree ->
                viewModel.recordCardPurchase(card.card.id, merchant, amountCents, installments, categoryId, isInterestFree)
                cardForPurchase = null
            }
        )
    }

    cardForPayment?.let { card ->
        PayCreditCardDialog(
            cardName = card.account.name,
            accounts = accounts,
            defaultAmountCents = viewModel.getCardBillingSummary(card).minimumPaymentCents,
            onDismiss = { cardForPayment = null },
            onConfirm = { accountId, amountCents ->
                viewModel.payCreditCard(card.card.id, accountId, amountCents)
                cardForPayment = null
            }
        )
    }

    debtForPayment?.let { debt ->
        RecordDebtPaymentDialog(
            debt = debt,
            onDismiss = { debtForPayment = null },
            onConfirm = { amountCents, isExtra ->
                viewModel.recordDebtPayment(debt.id, amountCents, isExtra)
                debtForPayment = null
            }
        )
    }

    debtForAmortization?.let { debt ->
        AmortizationBottomSheet(
            debt = debt,
            schedule = viewModel.getAmortizationSchedule(debt),
            onDismiss = { debtForAmortization = null }
        )
    }

    cardToEdit?.let { cardWithAccount ->
        EditCreditCardDialog(
            cardWithAccount = cardWithAccount,
            onDismiss = { cardToEdit = null },
            onConfirm = { limitCents, cutDay, payDay, ea, feeCents, digits, revolving, insurance, type ->
                viewModel.updateCreditCard(
                    cardId = cardWithAccount.card.id,
                    creditLimitCents = limitCents,
                    cutOffDay = cutDay,
                    paymentDueDay = payDay,
                    interestRateEa = ea,
                    handlingFeeCents = feeCents,
                    lastFourDigits = digits,
                    revolvingRate = revolving,
                    monthlyInsuranceCents = insurance,
                    installmentType = type
                )
                cardToEdit = null
            }
        )
    }

    cardForPurchases?.let { cardWithAccount ->
        CardMovementsBottomSheet(
            cardWithAccount = cardWithAccount,
            onDismiss = { cardForPurchases = null }
        )
    }

    debtToEdit?.let { debt ->
        EditDebtDialog(
            debt = debt,
            onDismiss = { debtToEdit = null },
            onConfirm = { name, principal, currentBal, ea, installment, dueDay, settled, insurance ->
                viewModel.updateDebt(
                    id = debt.id,
                    name = name,
                    principalAmountCents = principal,
                    currentBalanceCents = currentBal,
                    interestRateEa = ea,
                    monthlyInstallmentCents = installment,
                    dueDayOfMonth = dueDay,
                    isSettled = settled,
                    monthlyInsuranceCents = insurance
                )
                debtToEdit = null
            }
        )
    }
}

// -----------------------------------------------------------------------------------------
// PESTAÑA 1: TARJETAS DE CRÉDITO
// -----------------------------------------------------------------------------------------
@Composable
fun CreditCardsTab(
    cards: List<CreditCardWithAccount>,
    recommendation: com.personalfinance.cuentas.domain.math.CardRecommendation,
    onNewCardClick: () -> Unit,
    onNewPurchaseClick: (CreditCardWithAccount) -> Unit,
    onPayCardClick: (CreditCardWithAccount) -> Unit,
    onEditCardClick: (CreditCardWithAccount) -> Unit,
    onViewPurchasesClick: (CreditCardWithAccount) -> Unit,
    viewModel: CreditViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tarjetas de Crédito",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    maxLines = 1,
                    softWrap = false
                )
                Button(
                    onClick = onNewCardClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nueva", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), softWrap = false)
                }
            }
        }

        if (cards.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "No tienes tarjetas de crédito registradas. Pulsa '+ Nueva' para configurar cupo, corte y tasas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        } else {
            items(cards, key = { it.card.id }) { cardWithAccount ->
                val billing = viewModel.getCardBillingSummary(cardWithAccount)
                CreditCardItemCard(
                    cardWithAccount = cardWithAccount,
                    billing = billing,
                    onRecordPurchase = { onNewPurchaseClick(cardWithAccount) },
                    onPayCard = { onPayCardClick(cardWithAccount) },
                    onEdit = { onEditCardClick(cardWithAccount) },
                    onViewPurchases = { onViewPurchasesClick(cardWithAccount) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

@Composable
fun CreditCardItemCard(
    cardWithAccount: CreditCardWithAccount,
    billing: com.personalfinance.cuentas.domain.math.CreditCardBillingSummary,
    onRecordPurchase: () -> Unit,
    onPayCard: () -> Unit,
    onEdit: () -> Unit,
    onViewPurchases: () -> Unit
) {
    val card = cardWithAccount.card
    val account = cardWithAccount.account

    val progress = if (card.creditLimitCents > 0)
        (billing.totalUsedCents.toFloat() / card.creditLimitCents.toFloat()).coerceIn(0f, 1f)
    else 0f
    val usagePct = (progress * 100).toInt()
    val usageColor = when {
        progress > 0.85f -> FinancialRed
        progress > 0.60f -> FinancialAmber
        else             -> FinancialBlue
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            // ── Cabecera ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(usageColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = usageColor)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = account.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        if (!card.lastFourDigits.isNullOrBlank()) {
                            Text(
                                text = "•••• ${card.lastFourDigits}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${String.format(java.util.Locale.US, "%.1f", card.interestRateEa)}% EA",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1, softWrap = false
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Número grande: pago del corte ─────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Pagar antes de ${if (billing.paymentDueFormatted.isNotBlank()) billing.paymentDueFormatted else "día ${card.paymentDueDay}"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = CurrencyFormatter.formatCentsToCop(billing.totalPaymentCents),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = if (billing.daysUntilPaymentDue <= 3 && billing.totalPaymentCents > 0) FinancialRed
                                else MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Mínimo",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = CurrencyFormatter.formatCentsToCop(billing.minimumPaymentCents),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (billing.interestIfMinimumPaidCents > 0L) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = FinancialRed.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Percent, contentDescription = null, tint = FinancialRed, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Si pagas solo el mínimo: +${CurrencyFormatter.formatCentsToCop(billing.interestIfMinimumPaidCents)} en intereses",
                            style = MaterialTheme.typography.labelSmall,
                            color = FinancialRed
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Barra de cupo ──────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Usado $usagePct%  ${CurrencyFormatter.formatCentsToCop(billing.totalUsedCents)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = usageColor
                )
                Text(
                    text = "Libre: ${CurrencyFormatter.formatCentsToCop(billing.availableLimitCents)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = FinancialGreen
                )
            }
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                color = usageColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(Modifier.height(14.dp))

            // ── Chips de fechas ────────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Próximo corte",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = if (billing.nextCutOffFormatted.isNotBlank()) billing.nextCutOffFormatted else "Día ${card.cutOffDay}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (billing.daysUntilCutOff == 0) "¡Hoy!" else "En ${billing.daysUntilCutOff}d",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (billing.daysUntilCutOff <= 2) FinancialAmber else MaterialTheme.colorScheme.outline
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (billing.isAfterCutOffWaitingPayment) FinancialAmber.copy(alpha = 0.12f)
                             else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Límite de pago",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = if (billing.paymentDueFormatted.isNotBlank()) billing.paymentDueFormatted else "Día ${card.paymentDueDay}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = if (billing.daysUntilPaymentDue <= 3) FinancialRed else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (billing.daysUntilPaymentDue == 0) "¡Hoy!" else "En ${billing.daysUntilPaymentDue}d",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (billing.daysUntilPaymentDue <= 3) FinancialRed else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            if (billing.cycleStatusMessage.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (billing.isAfterCutOffWaitingPayment) FinancialAmber.copy(alpha = 0.10f)
                             else MaterialTheme.colorScheme.primary.copy(alpha = 0.07f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = billing.cycleStatusMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (billing.isAfterCutOffWaitingPayment) FinancialAmber else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Botones de acción ──────────────────────────────────────────────
            Button(
                onClick = onPayCard,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Payments, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Pagar tarjeta")
            }
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onRecordPurchase,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Payment, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("+ Compra", maxLines = 1, style = MaterialTheme.typography.labelMedium)
                }
                OutlinedButton(
                    onClick = onViewPurchases,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.TableChart, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Movimientos (${cardWithAccount.purchases.size})", maxLines = 1, style = MaterialTheme.typography.labelMedium, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BOTTOM SHEET: MOVIMIENTOS DE TARJETA
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardMovementsBottomSheet(
    cardWithAccount: CreditCardWithAccount,
    onDismiss: () -> Unit
) {
    val card     = cardWithAccount.card
    val account  = cardWithAccount.account
    val sheetState   = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale("es", "CO")) }
    val billing = remember(cardWithAccount) {
        FinancialFormulas.calculateCardBilling(card, cardWithAccount.purchases, java.time.LocalDate.now())
    }

    // ── Filtros ───────────────────────────────────────────────────────────────
    // 0 = Todas, 1 = Pendientes, 2 = Saldadas
    var filterIdx by remember { mutableIntStateOf(0) }
    // Rango de fechas: null = sin filtro
    var fromMillis by remember { mutableStateOf<Long?>(null) }
    var toMillis   by remember { mutableStateOf<Long?>(null) }

    val allPurchases = remember(cardWithAccount) {
        cardWithAccount.purchases.sortedByDescending { it.purchaseDateMillis }
    }

    val filtered = remember(allPurchases, filterIdx, fromMillis, toMillis) {
        allPurchases.filter { p ->
            val statusOk = when (filterIdx) {
                1 -> !p.isFullyPaid
                2 -> p.isFullyPaid
                else -> true
            }
            val dateOk = (fromMillis == null || p.purchaseDateMillis >= fromMillis!!) &&
                         (toMillis   == null || p.purchaseDateMillis <= toMillis!! + 86_400_000L)
            statusOk && dateOk
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Encabezado
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(FinancialBlue.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CreditCard, null, tint = FinancialBlue)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = account.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (!card.lastFourDigits.isNullOrBlank()) "•••• ${card.lastFourDigits}" else "Movimientos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // Resumen financiero del corte
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        _SummaryRow("Cupo usado (capital)", CurrencyFormatter.formatCentsToCop(billing.totalUsedCents), FinancialRed)
                        _SummaryRow("Disponible", CurrencyFormatter.formatCentsToCop(billing.availableLimitCents), FinancialGreen)
                        _SummaryRow("Pago del corte (sin intereses)", CurrencyFormatter.formatCentsToCop(billing.totalPaymentCents), MaterialTheme.colorScheme.onSurface)
                        _SummaryRow("Pago mínimo", CurrencyFormatter.formatCentsToCop(billing.minimumPaymentCents), MaterialTheme.colorScheme.onSurfaceVariant)
                        if (billing.interestIfMinimumPaidCents > 0L) {
                            _SummaryRow(
                                "Interés si pagas solo el mínimo",
                                "+${CurrencyFormatter.formatCentsToCop(billing.interestIfMinimumPaidCents)}",
                                FinancialRed
                            )
                        }
                    }
                }
            }

            // Cómo se llega al valor del corte
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "¿Por qué es ese valor?",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        val today = java.time.LocalDate.now()
                        val activePurchases = cardWithAccount.purchases.filter { !it.isFullyPaid }
                        val singleCuota = activePurchases.filter { it.installmentsTotal <= 1 }
                        val deferred    = activePurchases.filter { it.installmentsTotal > 1 }
                        if (singleCuota.isNotEmpty()) {
                            val singleTotal = singleCuota.sumOf { it.totalAmountCents }
                            _ExplainRow("Compras a 1 cuota (${singleCuota.size})", CurrencyFormatter.formatCentsToCop(singleTotal))
                        }
                        if (deferred.isNotEmpty()) {
                            val deferredMonthTotal = deferred.sumOf { p ->
                                val rate = (p.interestRateNm / 100.0).coerceAtLeast(0.0)
                                if (card.installmentType == 1) {
                                    val cap = p.totalAmountCents / p.installmentsTotal
                                    val rem = p.totalAmountCents - cap * p.installmentsPaid
                                    cap + (rem * rate).toLong()
                                } else {
                                    FinancialFormulas.calculateInstallmentAmountCents(p.totalAmountCents, rate, p.installmentsTotal)
                                }
                            }
                            _ExplainRow("Cuotas de diferidos (${deferred.size})", CurrencyFormatter.formatCentsToCop(deferredMonthTotal))
                        }
                        if (card.handlingFeeCents > 0L) {
                            _ExplainRow("Cuota de manejo", CurrencyFormatter.formatCentsToCop(card.handlingFeeCents))
                        }
                        if (card.monthlyInsuranceCents > 0L) {
                            _ExplainRow("Seguro mensual", CurrencyFormatter.formatCentsToCop(card.monthlyInsuranceCents))
                        }
                    }
                }
            }

            // Filtros de estado
            item {
                androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Movimientos (${allPurchases.size} total · ${filtered.size} mostrados)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.outline
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Todas", "Pendientes", "Saldadas").forEachIndexed { idx, label ->
                            FilterChip(
                                selected = filterIdx == idx,
                                onClick = { filterIdx = idx },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                    // Filtro de fechas
                    _DateRangeRow(
                        fromMillis = fromMillis,
                        toMillis   = toMillis,
                        formatter  = dateFormatter,
                        onFromChange = { fromMillis = it },
                        onToChange   = { toMillis   = it }
                    )
                }
            }

            if (filtered.isEmpty()) {
                item {
                    Text(
                        text = "No hay movimientos con ese filtro.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(filtered, key = { it.id }) { purchase ->
                    _CardPurchaseExpandableRow(card = card, purchase = purchase, dateFormatter = dateFormatter)
                }
            }
        }
    }
}

// ─── Helpers privados ────────────────────────────────────────────────────────

@Composable
private fun _SummaryRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f).padding(end = 6.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = valueColor, textAlign = TextAlign.End)
    }
}

@Composable
private fun _ExplainRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(end = 6.dp)) {
            Text("• ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), textAlign = TextAlign.End)
    }
}

@Composable
private fun _DateRangeRow(
    fromMillis: Long?,
    toMillis:   Long?,
    formatter:  SimpleDateFormat,
    onFromChange: (Long?) -> Unit,
    onToChange:   (Long?) -> Unit
) {
    val cal = java.util.Calendar.getInstance()
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        // Desde
        Surface(
            onClick = {
                // Toggle: quita el filtro si ya estaba puesto, o pone inicio del mes actual
                if (fromMillis != null) onFromChange(null)
                else {
                    cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
                    cal.set(java.util.Calendar.HOUR_OF_DAY, 0); cal.set(java.util.Calendar.MINUTE, 0)
                    onFromChange(cal.timeInMillis)
                }
            },
            shape = RoundedCornerShape(8.dp),
            color = if (fromMillis != null) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Text(
                text = if (fromMillis != null) "Desde: ${formatter.format(java.util.Date(fromMillis))}" else "Desde: cualquier fecha",
                style = MaterialTheme.typography.labelSmall,
                color = if (fromMillis != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                maxLines = 1
            )
        }
        // Hasta
        if (fromMillis != null) {
            Surface(
                onClick = {
                    if (toMillis != null) onToChange(null)
                    else onToChange(System.currentTimeMillis())
                },
                shape = RoundedCornerShape(8.dp),
                color = if (toMillis != null) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Text(
                    text = if (toMillis != null) "Hasta: ${formatter.format(java.util.Date(toMillis!!))}" else "Hasta: hoy",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (toMillis != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun _CardPurchaseExpandableRow(
    card: CreditCardEntity,
    purchase: CardPurchaseEntity,
    dateFormatter: SimpleDateFormat
) {
    val remaining = _purchaseRemainingCents(card, purchase)
    val isSettled  = purchase.isFullyPaid || remaining <= 0L
    val isDeferred = purchase.installmentsTotal > 1

    val statusColor = when {
        isSettled  -> FinancialGreen
        isDeferred -> FinancialBlue
        else       -> FinancialAmber
    }
    val statusLabel = when {
        isSettled  -> "Saldada"
        isDeferred -> "${purchase.installmentsPaid}/${purchase.installmentsTotal} cuotas"
        else       -> "Pendiente"
    }

    var expanded by remember { mutableStateOf(false) }

    Surface(
        onClick = { if (isDeferred || !isSettled) expanded = !expanded },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Fila principal
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = purchase.merchant,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = dateFormatter.format(java.util.Date(purchase.purchaseDateMillis)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = statusColor.copy(alpha = 0.12f),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyFormatter.formatCentsToCop(purchase.totalAmountCents),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    if (!isSettled && remaining > 0L) {
                        Text(
                            text = "Saldo: ${CurrencyFormatter.formatCentsToCop(remaining)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = FinancialRed
                        )
                    }
                    if (isDeferred && !isSettled) {
                        val rate = (purchase.interestRateNm / 100.0).coerceAtLeast(0.0)
                        val cuotaAmt = if (card.installmentType == 1) {
                            val cap = purchase.totalAmountCents / purchase.installmentsTotal
                            val rem = purchase.totalAmountCents - cap * purchase.installmentsPaid
                            cap + (rem * rate).toLong()
                        } else {
                            FinancialFormulas.calculateInstallmentAmountCents(purchase.totalAmountCents, rate, purchase.installmentsTotal)
                        }
                        Text(
                            text = "Cuota: ${CurrencyFormatter.formatCentsToCop(cuotaAmt)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = FinancialBlue
                        )
                    }
                }
            }

            // Detalle expandido
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(Modifier.height(4.dp))

                    if (isDeferred) {
                        val rate = (purchase.interestRateNm / 100.0).coerceAtLeast(0.0)
                        val pendingInstallments = purchase.installmentsTotal - purchase.installmentsPaid
                        val cuotaAmt = if (card.installmentType == 1) {
                            val cap = purchase.totalAmountCents / purchase.installmentsTotal
                            val rem = purchase.totalAmountCents - cap * purchase.installmentsPaid
                            cap + (rem * rate).toLong()
                        } else {
                            FinancialFormulas.calculateInstallmentAmountCents(purchase.totalAmountCents, rate, purchase.installmentsTotal)
                        }

                        _DetailRow("Valor total de la compra", CurrencyFormatter.formatCentsToCop(purchase.totalAmountCents))
                        _DetailRow("Cuotas totales", "${purchase.installmentsTotal}")
                        _DetailRow("Cuotas pagadas", "${purchase.installmentsPaid}")
                        _DetailRow("Cuotas restantes", "$pendingInstallments")
                        _DetailRow("Valor por cuota", CurrencyFormatter.formatCentsToCop(cuotaAmt), FinancialBlue)
                        if (rate > 0.0) {
                            _DetailRow("Tasa mensual", "${String.format(java.util.Locale.US, "%.2f", purchase.interestRateNm)}%")
                        } else {
                            _DetailRow("Tasa", "Sin intereses")
                        }
                        _DetailRow("Capital pendiente", CurrencyFormatter.formatCentsToCop(remaining), FinancialRed)
                        if (pendingInstallments > 0) {
                            Spacer(Modifier.height(2.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FinancialBlue.copy(alpha = 0.07f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Quedan $pendingInstallments cuotas de ${CurrencyFormatter.formatCentsToCop(cuotaAmt)} = ${CurrencyFormatter.formatCentsToCop(cuotaAmt * pendingInstallments)} restantes (sin intereses futuros)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FinancialBlue,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    } else {
                        // Compra a 1 cuota
                        _DetailRow("Valor de la compra", CurrencyFormatter.formatCentsToCop(purchase.totalAmountCents))
                        _DetailRow("Estado", if (isSettled) "Pagada completamente" else "Pendiente de pago")
                        if (!isSettled) {
                            _DetailRow("Monto pendiente", CurrencyFormatter.formatCentsToCop(purchase.totalAmountCents), FinancialRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun _DetailRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.weight(1f).padding(end = 8.dp),
            maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = valueColor,
            textAlign = TextAlign.End
        )
    }
}

/** Capital pendiente de una compra. */
private fun _purchaseRemainingCents(card: CreditCardEntity, purchase: CardPurchaseEntity): Long {
    if (purchase.isFullyPaid || purchase.installmentsPaid >= purchase.installmentsTotal) return 0L
    if (purchase.installmentsTotal <= 1) return purchase.totalAmountCents
    val rate = purchase.interestRateNm / 100.0
    return if (card.installmentType == 1) {
        val cap = purchase.totalAmountCents / purchase.installmentsTotal
        (purchase.totalAmountCents - cap * purchase.installmentsPaid).coerceAtLeast(0L)
    } else {
        FinancialFormulas.calculateRemainingPrincipalCents(
            principalCents      = purchase.totalAmountCents,
            monthlyRateDecimal  = rate,
            totalInstallments   = purchase.installmentsTotal,
            paidInstallments    = purchase.installmentsPaid
        )
    }
}

// -----------------------------------------------------------------------------------------
// PESTANA 2: DEUDAS Y PRESTAMOS
// -----------------------------------------------------------------------------------------

@Composable
fun DebtsTab(
    debts: List<DebtEntity>,
    onNewDebtClick: () -> Unit,
    onPayDebtClick: (DebtEntity) -> Unit,
    onViewAmortization: (DebtEntity) -> Unit,
    onEditDebtClick: (DebtEntity) -> Unit
) {
    val totalDebtCents = debts.filter { !it.isSettled }.sumOf { it.currentBalanceCents }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Préstamos y Créditos",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    maxLines = 1,
                    softWrap = false
                )
                Button(
                    onClick = onNewDebtClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FinancialRed.copy(alpha = 0.15f),
                        contentColor = FinancialRed
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nuevo", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), softWrap = false)
                }
            }
        }

        // Card Total Deuda Activa
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FinancialRed.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "TOTAL SALDO DE CAPITAL PENDIENTE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp),
                        color = FinancialRed
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = CurrencyFormatter.formatCentsToCop(totalDebtCents),
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = FinancialRed
                    )
                }
            }
        }

        if (debts.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "No tienes préstamos ni deudas registradas. ¡Felicitaciones!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        } else {
            items(debts, key = { it.id }) { debt ->
                DebtItemCard(
                    debt = debt,
                    onPay = { onPayDebtClick(debt) },
                    onAmortization = { onViewAmortization(debt) },
                    onEdit = { onEditDebtClick(debt) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

@Composable
fun DebtItemCard(
    debt: DebtEntity,
    onPay: () -> Unit,
    onAmortization: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = debt.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Cuota pactada: ${CurrencyFormatter.formatCentsToCop(debt.monthlyInstallmentCents)} • Día ${debt.dueDayOfMonth}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Tasa: ${String.format(Locale.US, "%.1f", debt.interestRateEa)}% EA",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = FinancialAmber
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = CurrencyFormatter.formatCentsToCop(debt.currentBalanceCents),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (debt.isSettled) FinancialGreen else FinancialRed
                        )
                        if (debt.isSettled) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = FinancialGreen.copy(alpha = 0.2f)
                            ) {
                                Text("PAGADA", style = MaterialTheme.typography.labelSmall, color = FinancialGreen, modifier = Modifier.padding(4.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Préstamo", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            if (!debt.isSettled) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onPay,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Pagar / Abonar")
                    }

                    OutlinedButton(
                        onClick = onAmortization,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Amortización")
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// PESTAÑA 3: SIMULADOR DE ABONOS EXTRA A CAPITAL
// -----------------------------------------------------------------------------------------
@Composable
fun ExtraPaymentSimulatorTab(
    debts: List<DebtEntity>,
    viewModel: CreditViewModel
) {
    var selectedDebtId by remember { mutableStateOf(debts.firstOrNull()?.id ?: 0L) }
    var extraAmountText by remember { mutableStateOf("500000") } // $500.000 COP por defecto

    val selectedDebt = debts.firstOrNull { it.id == selectedDebtId } ?: debts.firstOrNull()
    val extraCents = CurrencyFormatter.parseCopToCents(extraAmountText)

    val simulation = if (selectedDebt != null && extraCents > 0L) {
        viewModel.simulateExtraPayment(selectedDebt, extraCents)
    } else null

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Simulador de Abonos Extraordinarios",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Calcula exactamente cuánto tiempo e intereses te ahorras al abonar a capital",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        if (debts.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "Primero agrega un crédito o préstamo en la pestaña 'Préstamos' para simular.",
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Selecciona el préstamo a simular:", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            debts.forEach { debt ->
                                FilterChip(
                                    selected = selectedDebtId == debt.id,
                                    onClick = { selectedDebtId = debt.id },
                                    label = { Text(debt.name) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = extraAmountText,
                            onValueChange = { extraAmountText = it },
                            label = { Text("Monto del Abono Extra a Capital (acepta centavos)") },
                            prefix = { Text("$ ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (simulation != null && selectedDebt != null) {
                // Resultados de la Simulación
                item {
                    Text(
                        text = "Resultados del Abono de ${CurrencyFormatter.formatCentsToCop(extraCents)}:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Opción A: Reducir Plazo
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = FinancialGreen.copy(alpha = 0.12f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Savings, contentDescription = null, tint = FinancialGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "OPCIÓN A: REDUCIR PLAZO (Recomendada)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = FinancialGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Te ahorras ${simulation.reduceTermMonthsSaved} meses de pago",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Ahorro total en intereses: ${CurrencyFormatter.formatCentsToCop(simulation.reduceTermInterestSavedCents)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = FinancialGreen
                            )
                            Text(
                                text = "Terminas tu crédito en ${simulation.reduceTermNewMonths} meses en vez de ${simulation.originalMonthsRemaining}.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                // Opción B: Reducir Cuota
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = FinancialBlue.copy(alpha = 0.12f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalAtm, contentDescription = null, tint = FinancialBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "OPCIÓN B: REDUCIR CUOTA MENSUAL",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = FinancialBlue
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tu cuota mensual baja a ${CurrencyFormatter.formatCentsToCop(simulation.reduceInstallmentNewMonthlyCents)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Ahorro total en intereses: ${CurrencyFormatter.formatCentsToCop(simulation.reduceInstallmentInterestSavedCents)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = FinancialBlue
                            )
                            Text(
                                text = "Mantienes el plazo de ${simulation.originalMonthsRemaining} meses pero liberas flujo de caja mensual.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

// -----------------------------------------------------------------------------------------
// PESTAÑA 4: ESTRATEGIAS (BOLA DE NIEVE VS AVALANCHA)
// -----------------------------------------------------------------------------------------
@Composable
fun DebtStrategyTab(
    comparison: com.personalfinance.cuentas.domain.math.DebtStrategyComparison,
    currentBudget: Long,
    onBudgetChanged: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Bola de Nieve vs Avalancha",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Elige la mejor estrategia matemática para pagar todas tus deudas rápido",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        // Selector de Presupuesto Extra Mensual
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Presupuesto Extra Mensual para Deudas:",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            10000000L to "$100K",
                            20000000L to "$200K",
                            50000000L to "$500K",
                            100000000L to "$1M"
                        ).forEach { (cents, label) ->
                            FilterChip(
                                selected = currentBudget == cents,
                                onClick = { onBudgetChanged(cents) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        }

        // Cuadro Avalancha (Mayor Tasa Primero)
        item {
            StrategySummaryCard(
                title = "Estrategia Avalancha (Mayor Tasa Primero)",
                subtitle = "Paga primero la deuda más cara matemáticamente.",
                months = comparison.avalanche.monthsToFree,
                totalInterest = comparison.avalanche.totalInterestPaidCents,
                totalPaid = comparison.avalanche.totalPaidCents,
                order = comparison.avalanche.payoffOrder,
                badgeColor = FinancialGreen
            )
        }

        // Cuadro Bola de Nieve (Menor Saldo Primero)
        item {
            StrategySummaryCard(
                title = "Estrategia Bola de Nieve (Menor Saldo Primero)",
                subtitle = "Elimina deudas pequeñas primero para victorias rápidas.",
                months = comparison.snowball.monthsToFree,
                totalInterest = comparison.snowball.totalInterestPaidCents,
                totalPaid = comparison.snowball.totalPaidCents,
                order = comparison.snowball.payoffOrder,
                badgeColor = FinancialAmber
            )
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

@Composable
fun StrategySummaryCard(
    title: String,
    subtitle: String,
    months: Int,
    totalInterest: Long,
    totalPaid: Long,
    order: List<String>,
    badgeColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Tiempo total:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Text("$months meses", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Intereses totales pagados:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Text(CurrencyFormatter.formatCentsToCop(totalInterest), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = badgeColor)
                }
            }

            if (order.isNotEmpty()) {
                Text(
                    text = "Orden de liquidación: ${order.joinToString(" ➔ ")}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// DIÁLOGOS DE CREACIÓN Y BOTTOM SHEET DE AMORTIZACIÓN
// -----------------------------------------------------------------------------------------
@Composable
fun CreateCreditCardDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, limitCents: Long, cutDay: Int, payDay: Int, ea: Double, feeCents: Long, digits: String?, revolvingRate: Double, insuranceCents: Long, installmentType: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var limitText by remember { mutableStateOf("") }
    var cutDayText by remember { mutableStateOf("15") }
    var payDayText by remember { mutableStateOf("5") }
    var eaText by remember { mutableStateOf("28.5") }
    var feeText by remember { mutableStateOf("0") }
    var insuranceText by remember { mutableStateOf("0") }
    var revolvingRateText by remember { mutableStateOf("100") }
    var digits by remember { mutableStateOf("") }
    var installmentType by remember { mutableStateOf(0) } // 0: Tradicional, 1: Fintech

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Tarjeta de Crédito") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre / Banco (ej. Nu)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Tipo de Cuota / Sistema", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = installmentType == 0,
                        onClick = { installmentType = 0; revolvingRateText = "5" },
                        label = { Text("Tradicional") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = installmentType == 1,
                        onClick = { installmentType = 1; revolvingRateText = "100" },
                        label = { Text("Fintech (Nu/Rappi)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it },
                    label = { Text("Cupo Total en COP") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cutDayText,
                        onValueChange = { cutDayText = it },
                        label = { Text("Día Corte") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = payDayText,
                        onValueChange = { payDayText = it },
                        label = { Text("Día Límite") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = eaText,
                    onValueChange = { eaText = it },
                    label = { Text("Tasa EA %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("%") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = revolvingRateText,
                    onValueChange = { revolvingRateText = it },
                    label = { Text("Pago Mínimo Revolvente (%)") },
                    placeholder = { Text("100 para Nu/Rappi, 5 para Tradicionales") },
                    suffix = { Text("%") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text(if (installmentType == 1) "En Nu/Rappi suele ser el 100% de compras a 1 cuota." else "En bancos suele ser el 5%.")
                    }
                )
                OutlinedTextField(
                    value = insuranceText,
                    onValueChange = { insuranceText = it },
                    label = { Text("Seguro de Vida Deudor (Mensual)") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = digits,
                    onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) digits = it },
                    label = { Text("Últimos 4 dígitos (opcional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limit = CurrencyFormatter.parseCopToCents(limitText)
                    val cut = cutDayText.toIntOrNull() ?: 15
                    val pay = payDayText.toIntOrNull() ?: 5
                    val ea = eaText.replace(",", ".").toDoubleOrNull() ?: 28.5
                    val fee = CurrencyFormatter.parseCopToCents(feeText)
                    val insurance = CurrencyFormatter.parseCopToCents(insuranceText)
                    val revolving = (revolvingRateText.replace(",", ".").toDoubleOrNull() ?: 100.0) / 100.0
                    onConfirm(name, limit, cut, pay, ea, fee, digits.ifBlank { null }, revolving, insurance, installmentType)
                },
                enabled = name.isNotBlank() && limitText.isNotBlank()
            ) {
                Text("Guardar Tarjeta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun CreateDebtDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, principal: Long, ea: Double, dueDay: Int, termMonths: Int?, customInstallment: Long?, insuranceCents: Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var principalText by remember { mutableStateOf("") }
    var eaText by remember { mutableStateOf("18.0") }
    var dueDayText by remember { mutableStateOf("10") }
    var termText by remember { mutableStateOf("36") }
    var customInstallmentText by remember { mutableStateOf("") }
    var insuranceText by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Préstamo o Crédito") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Descripción (ej. Crédito Vehículo)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = principalText,
                    onValueChange = { principalText = it },
                    label = { Text("Monto Inicial en COP (acepta centavos)") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = eaText,
                        onValueChange = { eaText = it },
                        label = { Text("Tasa EA %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dueDayText,
                        onValueChange = { dueDayText = it },
                        label = { Text("Día de Pago") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = termText,
                    onValueChange = { termText = it },
                    label = { Text("Plazo en Meses (ej. 36)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = insuranceText,
                    onValueChange = { insuranceText = it },
                    label = { Text("Seguro Mensual (ej. Vida Deudor)") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = customInstallmentText,
                    onValueChange = { customInstallmentText = it },
                    label = { Text("Cuota Mensual Real (Opcional)") },
                    placeholder = { Text("Ajusta si el banco cobra seguros") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text("💡 Si dejas vacío, se calculará automáticamente con la tasa EA.")
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val principal = CurrencyFormatter.parseCopToCents(principalText)
                    val ea = eaText.replace(",", ".").toDoubleOrNull() ?: 18.0
                    val dueDay = dueDayText.toIntOrNull() ?: 10
                    val term = termText.toIntOrNull() ?: 36
                    val customInstallment = if (customInstallmentText.isBlank()) null else CurrencyFormatter.parseCopToCents(customInstallmentText)
                    val insurance = CurrencyFormatter.parseCopToCents(insuranceText)
                    onConfirm(name, principal, ea, dueDay, term, customInstallment, insurance)
                },
                enabled = name.isNotBlank() && principalText.isNotBlank()
            ) {
                Text("Guardar Crédito")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun RecordCardPurchaseDialog(
    card: CreditCardWithAccount,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onConfirm: (merchant: String, amountCents: Long, installments: Int, categoryId: Long, isInterestFree: Boolean) -> Unit
) {
    var merchant by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var installmentsText by remember { mutableStateOf("1") }
    var isInterestFree by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: 0L) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Compra en ${card.account.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = merchant,
                    onValueChange = { merchant = it },
                    label = { Text("Comercio / Detalle (ej. Tiquetes Avianca)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto en COP (acepta centavos)") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = installmentsText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) installmentsText = it },
                    label = { Text("Número de Cuotas (1 a 48)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if ((installmentsText.toIntOrNull() ?: 1) > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Compra sin intereses", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = isInterestFree,
                            onCheckedChange = { isInterestFree = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rawCents = CurrencyFormatter.parseCopToCents(amountText)
                    val installments = maxOf(1, installmentsText.toIntOrNull() ?: 1)
                    onConfirm(merchant, rawCents, installments, selectedCategoryId, isInterestFree)
                },
                enabled = merchant.isNotBlank() && amountText.isNotBlank()
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun RecordDebtPaymentDialog(
    debt: DebtEntity,
    onDismiss: () -> Unit,
    onConfirm: (amountCents: Long, isExtra: Boolean) -> Unit
) {
    var amountText by remember { mutableStateOf(CurrencyFormatter.formatCentsForInput(debt.monthlyInstallmentCents)) }
    var isExtra by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Pago: ${debt.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !isExtra,
                        onClick = {
                            isExtra = false
                            amountText = CurrencyFormatter.formatCentsForInput(debt.monthlyInstallmentCents)
                        },
                        label = { Text("Cuota Normal") }
                    )
                    FilterChip(
                        selected = isExtra,
                        onClick = { isExtra = true },
                        label = { Text("Abono a Capital") }
                    )
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto a Pagar en COP (acepta centavos)") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isExtra) {
                    Text(
                        text = "💡 Este abono va 100% a reducir el capital del crédito, disminuyendo intereses futuros.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FinancialGreen
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val paymentCents = CurrencyFormatter.parseCopToCents(amountText)
                    onConfirm(paymentCents, isExtra)
                },
                enabled = amountText.isNotBlank()
            ) {
                Text("Confirmar Pago")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PayCreditCardDialog(
    cardName: String,
    accounts: List<AccountEntity>,
    defaultAmountCents: Long,
    onDismiss: () -> Unit,
    onConfirm: (accountId: Long, amountCents: Long) -> Unit
) {
    var amountText by remember { mutableStateOf(CurrencyFormatter.formatCentsForInput(defaultAmountCents)) }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pagar Tarjeta: $cardName") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Selecciona la cuenta desde la que haces el pago. El cupo usado de la tarjeta se reducirá automáticamente.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                if (accounts.isEmpty()) {
                    Text(
                        text = "No tienes cuentas activas para pagar. Registra una cuenta primero.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FinancialRed
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        accounts.forEach { acc ->
                            FilterChip(
                                selected = selectedAccountId == acc.id,
                                onClick = { selectedAccountId = acc.id },
                                label = { Text(acc.name, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto a Pagar en COP (acepta centavos)") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "💡 Registrar este pago avanza tus cuotas diferidas y baja el cupo usado, sin afectar tus sobres de presupuesto.",
                    style = MaterialTheme.typography.bodySmall,
                    color = FinancialGreen
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val accountId = selectedAccountId ?: return@Button
                    val amountCents = CurrencyFormatter.parseCopToCents(amountText)
                    onConfirm(accountId, amountCents)
                },
                enabled = amountText.isNotBlank() && selectedAccountId != null
            ) {
                Text("Confirmar Pago")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmortizationBottomSheet(
    debt: DebtEntity,
    schedule: List<com.personalfinance.cuentas.domain.math.AmortizationRow>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Tabla de Amortización: ${debt.name}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Cuota fija mensual • Desglose mes a mes de Capital e Intereses",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Cabecera de la Tabla
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mes", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(0.7f))
                Text("Cuota", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.3f))
                Text("Capital", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.3f))
                Text("Interés", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.3f))
                Text("Saldo", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.4f))
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(420.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(schedule) { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${row.month}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.7f))
                        Text(CurrencyFormatter.formatCentsToCop(row.installmentCents), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1.3f))
                        Text(CurrencyFormatter.formatCentsToCop(row.principalPaidCents), style = MaterialTheme.typography.bodySmall.copy(color = FinancialGreen), modifier = Modifier.weight(1.3f))
                        Text(CurrencyFormatter.formatCentsToCop(row.interestCents), style = MaterialTheme.typography.bodySmall.copy(color = FinancialRed), modifier = Modifier.weight(1.3f))
                        Text(CurrencyFormatter.formatCentsToCop(row.remainingBalanceCents), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.4f))
                    }
                }
            }
        }
    }
}

@Composable
fun EditCreditCardDialog(
    cardWithAccount: CreditCardWithAccount,
    onDismiss: () -> Unit,
    onConfirm: (limitCents: Long, cutDay: Int, payDay: Int, ea: Double, feeCents: Long, digits: String?, revolvingRate: Double, insuranceCents: Long, installmentType: Int) -> Unit
) {
    val card = cardWithAccount.card
    var limitText by remember { mutableStateOf(CurrencyFormatter.formatCentsForInput(card.creditLimitCents)) }
    var cutDayText by remember { mutableStateOf(card.cutOffDay.toString()) }
    var payDayText by remember { mutableStateOf(card.paymentDueDay.toString()) }
    var eaText by remember { mutableStateOf(card.interestRateEa.toString()) }
    var feeText by remember { mutableStateOf(CurrencyFormatter.formatCentsForInput(card.handlingFeeCents)) }
    var insuranceText by remember { mutableStateOf(CurrencyFormatter.formatCentsForInput(card.monthlyInsuranceCents)) }
    var revolvingRateText by remember { mutableStateOf((card.revolvingRate * 100).toString()) }
    var digits by remember { mutableStateOf(card.lastFourDigits ?: "") }
    var installmentType by remember { mutableStateOf(card.installmentType) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Tarjeta: ${cardWithAccount.account.name}") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Tipo de Cuota / Sistema", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = installmentType == 0,
                        onClick = { installmentType = 0 },
                        label = { Text("Tradicional") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = installmentType == 1,
                        onClick = { installmentType = 1 },
                        label = { Text("Fintech (Nu/Rappi)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it },
                    label = { Text("Cupo Total en COP") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cutDayText,
                        onValueChange = { cutDayText = it },
                        label = { Text("Día Corte") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = payDayText,
                        onValueChange = { payDayText = it },
                        label = { Text("Día Límite") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = eaText,
                    onValueChange = { eaText = it },
                    label = { Text("Tasa EA %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("%") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = revolvingRateText,
                    onValueChange = { revolvingRateText = it },
                    label = { Text("Pago Mínimo Revolvente (%)") },
                    placeholder = { Text("100 para Nu/Rappi, 5 para Tradicionales") },
                    suffix = { Text("%") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text(if (installmentType == 1) "En Nu/Rappi suele ser el 100% de compras a 1 cuota." else "En bancos suele ser el 5%.")
                    }
                )
                OutlinedTextField(
                    value = feeText,
                    onValueChange = { feeText = it },
                    label = { Text("Cuota de Manejo Mensual") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = insuranceText,
                    onValueChange = { insuranceText = it },
                    label = { Text("Seguro de Vida Deudor Mensual") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = digits,
                    onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) digits = it },
                    label = { Text("Últimos 4 dígitos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limit = CurrencyFormatter.parseCopToCents(limitText)
                    val cut = cutDayText.toIntOrNull()?.coerceIn(1, 31) ?: card.cutOffDay
                    val pay = payDayText.toIntOrNull()?.coerceIn(1, 31) ?: card.paymentDueDay
                    val ea = eaText.replace(",", ".").toDoubleOrNull() ?: card.interestRateEa
                    val fee = CurrencyFormatter.parseCopToCents(feeText)
                    val insurance = CurrencyFormatter.parseCopToCents(insuranceText)
                    val revolving = (revolvingRateText.replace(",", ".").toDoubleOrNull() ?: (card.revolvingRate * 100)) / 100.0
                    onConfirm(limit, cut, pay, ea, fee, digits.ifBlank { null }, revolving, insurance, installmentType)
                },
                enabled = limitText.isNotBlank()
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun EditDebtDialog(
    debt: DebtEntity,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        principalAmountCents: Long,
        currentBalanceCents: Long,
        interestRateEa: Double,
        monthlyInstallmentCents: Long,
        dueDayOfMonth: Int,
        isSettled: Boolean,
        monthlyInsuranceCents: Long
    ) -> Unit
) {
    var name by remember { mutableStateOf(debt.name) }
    var principalText by remember { mutableStateOf(CurrencyFormatter.formatCentsForInput(debt.principalAmountCents)) }
    var currentBalanceText by remember { mutableStateOf(CurrencyFormatter.formatCentsForInput(debt.currentBalanceCents)) }
    var installmentText by remember { mutableStateOf(CurrencyFormatter.formatCentsForInput(debt.monthlyInstallmentCents)) }
    var eaText by remember { mutableStateOf(debt.interestRateEa.toString()) }
    var dueDayText by remember { mutableStateOf(debt.dueDayOfMonth.toString()) }
    var insuranceText by remember { mutableStateOf(CurrencyFormatter.formatCentsForInput(debt.monthlyInsuranceCents)) }
    var isSettled by remember { mutableStateOf(debt.isSettled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Préstamo: ${debt.name}") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre / Entidad del Préstamo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = currentBalanceText,
                    onValueChange = { currentBalanceText = it },
                    label = { Text("Saldo Actual de Capital (acepta centavos)") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = installmentText,
                    onValueChange = { installmentText = it },
                    label = { Text("Cuota Mensual Real") },
                    placeholder = { Text("Ajusta si el banco cobra seguros") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text("💡 El banco suele sumar seguros. Digita el valor exacto de tu extracto.")
                    }
                )
                OutlinedTextField(
                    value = insuranceText,
                    onValueChange = { insuranceText = it },
                    label = { Text("Seguro Mensual (ej. Vida Deudor)") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = principalText,
                    onValueChange = { principalText = it },
                    label = { Text("Monto Original Prestado") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = eaText,
                        onValueChange = { eaText = it },
                        label = { Text("Tasa EA %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dueDayText,
                        onValueChange = { dueDayText = it },
                        label = { Text("Día de Pago") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Marcar como saldado / pagado", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isSettled, onCheckedChange = { isSettled = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val principal = CurrencyFormatter.parseCopToCents(principalText)
                    val currentBal = CurrencyFormatter.parseCopToCents(currentBalanceText)
                    val installment = CurrencyFormatter.parseCopToCents(installmentText)
                    val ea = eaText.replace(",", ".").toDoubleOrNull() ?: debt.interestRateEa
                    val dueDay = dueDayText.toIntOrNull()?.coerceIn(1, 31) ?: debt.dueDayOfMonth
                    val insurance = CurrencyFormatter.parseCopToCents(insuranceText)
                    onConfirm(name, principal, currentBal, ea, installment, dueDay, isSettled, insurance)
                },
                enabled = name.isNotBlank() && currentBalanceText.isNotBlank()
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
