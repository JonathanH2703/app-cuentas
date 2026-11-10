# Design Document: Dashboard UI Fix & Quick Income FAB

## Overview

This document covers the technical design for two related bugfixes in the personal finance app:

1. **Bug 1 — Dashboard visual overflow**: Four layout defects in `DashboardScreen.kt` and `DashboardComponents.kt` that cause text truncation, excessive spacing, a duplicated Actual/Proyectado toggle, and redundant bottom padding on 6.7" FHD+ screens.
2. **Bug 2 — FAB only registers expenses**: The main floating action button in `MainScaffold.kt` only opens `QuickExpenseSheet`. This fix introduces an expandable FAB that surfaces both expense and income registration, and adds the new `QuickIncomeSheet` composable.

Both fixes are surgical: no new ViewModels, no new navigation destinations, no new data layer changes.

---

## Bug Details

### Bug 1 — Dashboard Visual Overflow

Four sub-issues manifest on 6.7" FHD+ screens (e.g. Honor 400 Smart):

**1.1 — Text truncation in `CompactFinancialHeroCard`**
The three-metric `Row` inside `CompactFinancialHeroCard` renders label `Text` composables (`"Libre hoy"`, `"Por entrar"`, `"Por día (Xd)"`) with `softWrap = false` and no `overflow` parameter. Combined with `Modifier.weight(1f)` columns on a narrow available width (~340–380 dp after margins), the text is hard-clipped without ellipsis.

**1.2 — Excessive horizontal padding and vertical spacing**
`DashboardScreen` `LazyColumn` applies `.padding(horizontal = 20.dp)` and `verticalArrangement = Arrangement.spacedBy(14.dp)`. On wider/longer 6.7" devices this accumulates into overly tight side margins and excess dead space between cards.

**1.3 — Duplicated Actual/Proyectado toggle**
`CashFlowChartCard` is called from `DashboardScreen` with `onViewModeChange = { isProjectedView = it }`. Because this lambda is non-null, `CashFlowChartCard` renders its own Actual/Proyectado pill toggle — a duplicate of the one already rendered inside `CompactFinancialHeroCard` at the top of the screen.

**1.4 — Redundant bottom spacer**
The `LazyColumn` has `contentPadding = PaddingValues(bottom = 96.dp)` and a final list item `item { Spacer(modifier = Modifier.height(72.dp)) }`, accumulating ~168 dp of dead scroll space below the last card.

### Bug 2 — FAB Only Registers Expenses

**2.1 — No income path from FAB**
In `MainScaffold`, the `FloatingActionButton`'s `onClick` sets `showQuickExpenseSheet = true` directly. There is no branch or menu to open an income registration flow. `IncomesViewModel.createIncome` exists and works, but is only reachable via the Incomes tab (page 4).

---

## Expected Behavior

**1.1** — Label texts in the three-metric `Row` of `CompactFinancialHeroCard` must never be hard-clipped. They may be ellipsized if truly too long, but must not overflow or disappear.

**1.2** — The `LazyColumn` should use `horizontal = 12.dp` padding and `spacedBy(10.dp)` to give cards more horizontal room and less vertical dead space without breaking the visual hierarchy.

**1.3** — The Actual/Proyectado toggle must appear exactly once on the dashboard — inside `CompactFinancialHeroCard`. `CashFlowChartCard` must react to the toggle state as a read-only consumer.

**1.4** — Only `contentPadding = PaddingValues(bottom = 96.dp)` manages bottom clearance. The redundant `Spacer(72.dp)` item must be removed.

**2.1** — Tapping "+" expands a two-option FAB menu above the main FAB button. The user can tap "Gasto" to open `QuickExpenseSheet` (unchanged) or "Ingreso" to open the new `QuickIncomeSheet`. Tapping outside the menu or pressing Back collapses it without opening any sheet.

**2.2** — `QuickIncomeSheet` mirrors `QuickExpenseSheet` structurally: a `ModalBottomSheet` with amount input, account selector (`FilterChip` row), optional description, and a confirm button that calls `IncomesViewModel.createIncome(description, amountCents, accountId, IncomeFrequency.ONE_TIME, System.currentTimeMillis())`.

---

## Hypothesized Root Cause

**Bug 1.1** — `softWrap = false` tells Compose to lay out the text in a single line without any wrapping, but does not instruct the layout to clip gracefully; the text is simply cut off at the container boundary. The correct approach for single-line constrained text is `maxLines = 1` + `overflow = TextOverflow.Ellipsis`, which Compose handles via the text engine's ellipsis insertion.

**Bug 1.2** — The padding and spacing values were likely authored on a ~5.5" reference device. On 6.7" screens the larger physical width means the 20 dp side margins consume a larger share of the total card width, while the 14 dp item spacing adds up to ~140 dp of vertical dead space across ten sections.

**Bug 1.3** — `CashFlowChartCard` already implements a guard: `if (onViewModeChange != null)` before rendering its toggle. The bug is entirely at the call site in `DashboardScreen` where a non-null lambda is passed, defeating that guard. The composable itself is correct.

**Bug 1.4** — The `Spacer(72.dp)` item and the `contentPadding(bottom = 96.dp)` were likely added at different times by different authors to ensure content clears the FAB. Neither was removed when the other was added.

**Bug 2.1** — The FAB was originally designed for a single action (expense). No expandable FAB pattern was implemented, and `IncomesViewModel` was injected in `MainScaffold` only for observing data — no quick-entry income path was ever wired up from the FAB layer.

---

## Fix Implementation

### Architecture Overview

```
MainScaffold.kt
├── Scaffold
│   ├── bottomBar: NavigationBar
│   ├── floatingActionButton: ExpandableFabMenu (NEW)  ← replaces single FAB
│   └── content: HorizontalPager
│       ├── page 0: DashboardScreen (MODIFIED)
│       │   └── LazyColumn
│       │       ├── CompactFinancialHeroCard (owns toggle)
│       │       ├── RecurringItemsSection
│       │       ├── AllocationEnvelopesSection
│       │       └── CashFlowChartCard (onViewModeChange = null)
│       └── pages 1–4: unchanged
├── QuickExpenseSheet (existing, unchanged)
└── QuickIncomeSheet (NEW)
```

### Component / Dependency Diagram

```
MainScaffold
  ├─ hiltViewModel<IncomesViewModel>()    (already injected, reused for income save)
  ├─ hiltViewModel<AccountsViewModel>()   (accounts: List<AccountEntity>, reused for sheet)
  ├─ ExpandableFabMenu                    (new private composable)
  │   ├─ FloatingActionButton (main "+")
  │   ├─ SmallFloatingActionButton "Gasto"   → showQuickExpenseSheet = true
  │   └─ SmallFloatingActionButton "Ingreso" → showQuickIncomeSheet = true
  ├─ QuickExpenseSheet                    (unchanged)
  └─ QuickIncomeSheet                     (new)
       └─ onSaveIncome(accountId, amountCents, description)
            └─ IncomesViewModel.createIncome(desc, cents, accountId,
                                             IncomeFrequency.ONE_TIME,
                                             System.currentTimeMillis())

DashboardScreen
  ├─ isProjectedView: Boolean  (single source of truth, owned here)
  ├─ CompactFinancialHeroCard(onViewModeChange = { isProjectedView = it })  ← writes
  └─ CashFlowChartCard(isProjectedView = isProjectedView, onViewModeChange = null)  ← reads
```

---

### Fix 1.1 — Label `Text` overflow in `CompactFinancialHeroCard`

**File:** `presentation/dashboard/DashboardComponents.kt`

**Change:** Replace `softWrap = false` with `overflow = TextOverflow.Ellipsis` on every label `Text` inside the three-metric `Row`. The value `Text`s (showing formatted amounts) already use `maxLines = 1` without `softWrap = false` — they are unchanged.

```kotlin
// BEFORE (all three label Texts)
Text(
    text = "Libre hoy",   // or "Por entrar", "Por día (Xd)", etc.
    style = MaterialTheme.typography.labelSmall,
    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
    maxLines = 1, softWrap = false
)

// AFTER
Text(
    text = "Libre hoy",
    style = MaterialTheme.typography.labelSmall,
    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)
```

Locations in `DashboardComponents.kt` (inside `CompactFinancialHeroCard`, metric `Row`):
- Left column: label for "Libre hoy" / "Libre proyectado"
- Centre column: label for "Por entrar" / "Ahorro protegido" / "Líquido gastable"
- Right column: label for "Por día (${n}d)"

---

### Fix 1.2 — Reduce padding and spacing in `DashboardScreen`

**File:** `presentation/dashboard/DashboardScreen.kt`

```kotlin
// BEFORE
LazyColumn(
    modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 20.dp),
    contentPadding = PaddingValues(bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
)

// AFTER
LazyColumn(
    modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 12.dp),           // 20 → 12
    contentPadding = PaddingValues(bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)   // 14 → 10
)
```

---

### Fix 1.3 — Remove duplicate toggle from `CashFlowChartCard`

**File:** `presentation/dashboard/DashboardScreen.kt`

**Data flow for `isProjectedView`:**

```
DashboardScreen
│
│  var isProjectedView by remember { mutableStateOf(false) }   ← single owner
│
├─► CompactFinancialHeroCard(
│       isProjectedView = isProjectedView,
│       onViewModeChange = { isProjectedView = it }     ← writes via callback
│   )                                                   ← renders toggle pill
│
└─► CashFlowChartCard(
        isProjectedView = isProjectedView,              ← reads only
        onViewModeChange = null                         ← guard suppresses toggle
    )
```

```kotlin
// BEFORE
CashFlowChartCard(
    projections = if (isProjectedView) cashFlowProjection.dailyProjections
                  else actualCashFlowProjection.dailyProjections,
    selectedHorizon = selectedHorizon,
    onHorizonSelected = { selectedHorizon = it },
    isProjectedView = isProjectedView,
    onViewModeChange = { isProjectedView = it }    // ← causes duplicate
)

// AFTER
CashFlowChartCard(
    projections = if (isProjectedView) cashFlowProjection.dailyProjections
                  else actualCashFlowProjection.dailyProjections,
    selectedHorizon = selectedHorizon,
    onHorizonSelected = { selectedHorizon = it },
    isProjectedView = isProjectedView,
    onViewModeChange = null                        // ← guard in CashFlowChartCard suppresses toggle
)
```

`CashFlowChartCard` in `DashboardComponents.kt` already guards correctly with `if (onViewModeChange != null)` — no changes needed inside it.

---

### Fix 1.4 — Remove redundant bottom spacer

**File:** `presentation/dashboard/DashboardScreen.kt`

Remove the following item entirely from the `LazyColumn`:

```kotlin
// REMOVE:
item {
    Spacer(modifier = Modifier.height(72.dp))
}
```

The `contentPadding = PaddingValues(bottom = 96.dp)` alone provides sufficient clearance above the FAB.

---

### Fix 2.1 — Expandable FAB in `MainScaffold`

**File:** `presentation/navigation/MainScaffold.kt`

**New state variables:**

```kotlin
var showFabMenu by remember { mutableStateOf(false) }
var showQuickIncomeSheet by remember { mutableStateOf(false) }
```

**FAB action flow:**

```
User taps "+"
       │
       ▼
showFabMenu = true
       │
       ├── User taps "Gasto" (TrendingDown icon)
       │       └─► showFabMenu = false; showQuickExpenseSheet = true
       │               └─► QuickExpenseSheet → TransactionsViewModel.recordQuickExpense(...)
       │
       ├── User taps "Ingreso" (TrendingUp icon)
       │       └─► showFabMenu = false; showQuickIncomeSheet = true
       │               └─► QuickIncomeSheet → IncomesViewModel.createIncome(...)
       │
       └── User taps scrim or presses Back
               └─► showFabMenu = false  (menu collapses, no sheet opened)
```

**`ExpandableFabMenu` composable signature:**

```kotlin
@Composable
private fun ExpandableFabMenu(
    expanded: Boolean,
    onToggle: () -> Unit,
    onExpenseClick: () -> Unit,
    onIncomeClick: () -> Unit,
    modifier: Modifier = Modifier
)
```

**Implementation sketch:**

```kotlin
@Composable
private fun ExpandableFabMenu(
    expanded: Boolean,
    onToggle: () -> Unit,
    onExpenseClick: () -> Unit,
    onIncomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Full-screen transparent scrim to dismiss on outside tap
    if (expanded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onToggle() }
        )
    }

    // System Back collapses the menu
    BackHandler(enabled = expanded) { onToggle() }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mini FABs animate in/out above the main FAB
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ingreso mini-FAB
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "Ingreso",
                            style = MaterialTheme.typography.labelMedium
                                .copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    SmallFloatingActionButton(
                        onClick = { onIncomeClick() },
                        containerColor = FinancialGreen,
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.TrendingUp,
                             contentDescription = "Registrar Ingreso")
                    }
                }

                // Gasto mini-FAB
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "Gasto",
                            style = MaterialTheme.typography.labelMedium
                                .copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    SmallFloatingActionButton(
                        onClick = { onExpenseClick() },
                        containerColor = FinancialRed,
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.TrendingDown,
                             contentDescription = "Registrar Gasto")
                    }
                }
            }
        }

        // Main FAB — icon rotates 45° when expanded
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
                Icons.Default.Add,
                contentDescription = if (expanded) "Cerrar menú" else "Registrar",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}
```

**Replacement in `MainScaffold` `floatingActionButton` slot:**

```kotlin
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
}
```

---

### Fix 2.2 — New `QuickIncomeSheet`

**New file:** `presentation/transactions/QuickIncomeSheet.kt`

**Function signature:**

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickIncomeSheet(
    sheetState: SheetState,
    accounts: List<AccountEntity>,
    onDismiss: () -> Unit,
    onSaveIncome: (accountId: Long, amountCents: Long, description: String) -> Unit
)
```

**Implementation sketch:**

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickIncomeSheet(
    sheetState: SheetState,
    accounts: List<AccountEntity>,
    onDismiss: () -> Unit,
    onSaveIncome: (accountId: Long, amountCents: Long, description: String) -> Unit
) {
    var rawAmount by remember { mutableStateOf("") }
    var selectedAccountId by remember {
        mutableStateOf(accounts.firstOrNull()?.id ?: 0L)
    }
    var description by remember { mutableStateOf("") }

    val amountCents = remember(rawAmount) {
        CurrencyFormatter.parseCopToCents(rawAmount)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registrar Ingreso Rápido",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            // Amount field
            OutlinedTextField(
                value = rawAmount,
                onValueChange = { newVal ->
                    val filtered = newVal.filter { it.isDigit() || it == ',' }
                    if (filtered.count { it == ',' } <= 1) {
                        val parts = filtered.split(",")
                        if (parts.size == 2 && parts[1].length > 2) return@OutlinedTextField
                        rawAmount = filtered
                    }
                },
                label = { Text("Monto recibido") },
                prefix = { Text("$ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Account selector
            Text(
                text = "Cuenta destino:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Start)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(accounts) { account ->
                    FilterChip(
                        selected = selectedAccountId == account.id,
                        onClick = { selectedAccountId = account.id },
                        label = {
                            Text(
                                "${account.name} (${CurrencyFormatter.formatCentsToCop(account.currentBalanceCents)})"
                            )
                        }
                    )
                }
            }

            // Optional description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Confirm button
            Button(
                onClick = {
                    if (amountCents > 0L && selectedAccountId != 0L) {
                        onSaveIncome(
                            selectedAccountId,
                            amountCents,
                            description.trim()
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                enabled = amountCents > 0L && selectedAccountId != 0L,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FinancialGreen)
            ) {
                Icon(Icons.Default.TrendingUp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (amountCents > 0L)
                               "Registrar ${CurrencyFormatter.formatCentsToCop(amountCents)}"
                           else "Registrar Ingreso",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
```

**Wiring in `MainScaffold`:**

```kotlin
if (showQuickIncomeSheet) {
    val incomeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    QuickIncomeSheet(
        sheetState = incomeSheetState,
        accounts = accounts,
        onDismiss = { showQuickIncomeSheet = false },
        onSaveIncome = { accountId, amountCents, desc ->
            incomesViewModel.createIncome(
                description = desc.ifBlank { "Ingreso rápido" },
                amountCents = amountCents,
                accountId = accountId,
                frequency = IncomeFrequency.ONE_TIME,
                expectedDateMillis = System.currentTimeMillis()
            )
        }
    )
}
```

---

## Correctness Properties

### Property 1: Metric labels never hard-clipped (Bug 1.1)

**Validates: Requirements 2.1, 3.1**

For any `SafeToSpendCalculation` input and any screen width between 320 dp and 430 dp, none of the three metric label `Text`s inside `CompactFinancialHeroCard`'s metric `Row` should have `softWrap = false` set, and all three should be addressable nodes in the Compose semantics tree (i.e. they were laid out and not zero-sized).

```pascal
PROPERTY P1: MetricLabelsNeverHardClipped
FOR ALL calc IN {GREEN, YELLOW, RED} status variants DO
  FOR ALL screenWidthDp IN [320, 360, 393, 412, 430] DO
    rendered ← renderCompactFinancialHeroCard(calc, screenWidthDp)
    ASSERT rendered.leftLabel.softWrap   ≠ false
       AND rendered.centreLabel.softWrap ≠ false
       AND rendered.rightLabel.softWrap  ≠ false
       AND rendered.leftLabel.isDisplayed   = true
       AND rendered.centreLabel.isDisplayed = true
       AND rendered.rightLabel.isDisplayed  = true
  END FOR
END FOR
```

### Property 2: Toggle appears exactly once (Bug 1.3)

**Validates: Requirements 2.3, 3.3**

In a fully rendered `DashboardScreen`, semantic nodes with text "Actual" and "Proyectado" each appear exactly once across the entire composition tree.

```pascal
PROPERTY P2: ToggleAppearsExactlyOnce
FOR ALL dashboardState IN validDashboardStates() DO
  rendered ← renderDashboardScreen(dashboardState)
  ASSERT count(rendered, text="Actual")     = 1
     AND count(rendered, text="Proyectado") = 1
END FOR
```

### Property 3: FAB always exposes both actions (Bug 2.1)

**Validates: Requirements 2.5, 3.2**

After the main FAB is tapped, both mini-FABs with their respective content descriptions are visible, regardless of which pager page is currently displayed.

```pascal
PROPERTY P3: FabExpandsToTwoActions
FOR ALL page IN [0, 1, 2, 3, 4] DO
  rendered ← renderMainScaffold(initialPage = page)
  tapFab(rendered)
  ASSERT isDisplayed(rendered, contentDescription="Registrar Ingreso") = true
     AND isDisplayed(rendered, contentDescription="Registrar Gasto")   = true
END FOR
```

### Property 4: QuickIncomeSheet confirm disabled when input is invalid (Bug 2.2)

**Validates: Requirements 2.6**

The confirm button must never be enabled when `amountCents ≤ 0` or `selectedAccountId = 0L`, regardless of the description value.

```pascal
PROPERTY P4: ConfirmDisabledWhenInputInvalid
FOR ALL amountCents IN [0L, -1L] DO
  FOR ALL accountId IN [0L] DO
    FOR ALL description IN ["", "Salario", longString(100)] DO
      sheet ← renderQuickIncomeSheet(amountCents, accountId, description)
      ASSERT sheet.confirmButton.enabled = false
    END FOR
  END FOR
END FOR

FOR ALL amountCents IN positiveAmounts() DO
  FOR ALL accountId IN validAccountIds() DO
    sheet ← renderQuickIncomeSheet(amountCents, accountId, "any")
    ASSERT sheet.confirmButton.enabled = true
  END FOR
END FOR
```

### Property 5: createIncome called with correct arguments (Bug 2.2)

**Validates: Requirements 2.6, 3.5**

When the user confirms `QuickIncomeSheet` with given `(accountId, amountCents, description)`, `IncomesViewModel.createIncome` must be invoked exactly once with `frequency = IncomeFrequency.ONE_TIME` and the matching account/amount values.

```pascal
PROPERTY P5: CreateIncomeCalledWithCorrectArguments
FOR ALL accountId IN validAccountIds() DO
  FOR ALL amountCents IN [1L, 100_000L, 9_999_999L] DO
    FOR ALL description IN ["", "Salario", "Freelance enero"] DO
      confirmSheet(QuickIncomeSheet, accountId, amountCents, description)
      ASSERT lastCall(incomesViewModel.createIncome).accountId    = accountId
         AND lastCall(incomesViewModel.createIncome).amountCents  = amountCents
         AND lastCall(incomesViewModel.createIncome).frequency    = IncomeFrequency.ONE_TIME
         AND lastCall(incomesViewModel.createIncome).description  =
               IF description.isBlank() THEN "Ingreso rápido" ELSE description
    END FOR
  END FOR
END FOR
```

---

## Testing Strategy

### Unit Testing Approach

**Bug 1 — Layout properties** (Compose UI tests with `createComposeRule()`):

- P1: Verify label `Text`s in `CompactFinancialHeroCard` are displayed across five reference widths (320–430 dp).
- P2: Use `onAllNodesWithText("Actual").assertCountEquals(1)` and `onAllNodesWithText("Proyectado").assertCountEquals(1)` on a rendered `DashboardScreen`.
- P1 and P2 also serve as regression guards for 3.1 and 3.3 from the requirements.

**Bug 2 — Behavior properties** (Compose UI tests + ViewModel unit tests):

- P3: Tap the FAB and assert both mini-FAB content descriptions are visible.
- P4: Render `QuickIncomeSheet` with zero/invalid amounts and assert button is disabled.
- P5: Use a fake `IncomesViewModel` (or `mockk`) and verify the correct arguments on confirm.

### Property-Based Testing Approach

**P1** — Parameterize over the five device widths and the three `SafeToSpendStatus` variants (9 combinations total). This is small enough for exhaustive parameterized tests rather than random generation.

**P4** — The invalid-input space is well-bounded (`amountCents ≤ 0`, `accountId = 0L`). Use explicit boundary values: `{0L, -1L, Long.MIN_VALUE}` for amount and `{0L}` for account ID. Valid-input cases use a small representative set.

**P5** — Generate three representative `amountCents` values and three `description` values. The blank-description substitution rule (`ifBlank { "Ingreso rápido" }`) is the key invariant to verify.

No external PBT library (e.g. `kotest-property`) is required; JUnit4 `@Parameters` or Kotlin `forEach` loops cover these bounded property spaces adequately.

### Regression Prevention

Tests 3.1–3.6 from the requirements map directly to:
- 3.1 → P1 (multiple screen widths)
- 3.2 → existing `QuickExpenseSheet` tests (no changes, no new risk)
- 3.3 → P2 (toggle state propagates correctly to `CashFlowChartCard`)
- 3.4 → no test needed (IncomesScreen is not modified)
- 3.5 → P5 (correct ViewModel call)
- 3.6 → no test needed (scroll behavior is unchanged)

---

## Error Handling

**`QuickIncomeSheet` — empty accounts list:** If `accounts` is empty, `selectedAccountId` stays `0L` and the confirm button stays disabled. No crash or blank state issue.

**`QuickIncomeSheet` — ViewModel validation:** `IncomesViewModel.createIncome` validates `amountCents > 0` and `accountId != 0L` server-side and emits a user message via `_userMessage` if invalid. The UI guard in the sheet prevents reaching this path under normal use.

**`ExpandableFabMenu` — `BackHandler` conflict:** `BackHandler(enabled = expanded)` is active only while the menu is open. When `expanded = false` the handler is a no-op and does not intercept pager back navigation.

**`ExpandableFabMenu` — `isSettingsOpen` guard:** The entire FAB block is wrapped in `if (!isSettingsOpen)` already in `MainScaffold`. `ExpandableFabMenu` inherits this guard.

---

## Performance Considerations

- `AnimatedVisibility` in `ExpandableFabMenu` uses `fadeIn`/`fadeOut` and `slideInVertically`/`slideOutVertically` — GPU-composited, negligible CPU cost.
- The full-screen scrim `Box` is fully transparent and does not cause any child recomposition on its own.
- `QuickIncomeSheet` uses a short static `Column` with `verticalScroll` — no `LazyColumn` required.
- The padding/spacing reduction in `DashboardScreen` (12 dp / 10 dp) has zero runtime cost; it only affects the measure/layout pass.

## Dependencies

No new external dependencies. All new composables use APIs already present in `build.gradle.kts`:
- `androidx.compose.material3`: `ModalBottomSheet`, `SmallFloatingActionButton`, `BackHandler`
- `androidx.compose.animation`: `AnimatedVisibility`, `fadeIn`, `fadeOut`, `slideInVertically`, `slideOutVertically`
- `androidx.compose.animation.core`: `animateFloatAsState`
- `androidx.compose.ui.draw`: `Modifier.rotate`
- `com.personalfinance.cuentas.data.local.entities.IncomeFrequency` (existing)
- `com.personalfinance.cuentas.core.ui.theme.FinancialGreen` / `FinancialRed` (existing)

## Glossary

| Term | Meaning |
|---|---|
| `CompactFinancialHeroCard` | The large card at the top of `DashboardScreen` showing consolidated balance and three-metric row |
| `CashFlowChartCard` | The card in `DashboardScreen` showing the 30/60/90-day cash flow sparkline |
| `isProjectedView` | Boolean state in `DashboardScreen` controlling whether projected or actual data is shown |
| `ExpandableFabMenu` | New private composable replacing the single FAB, showing Gasto/Ingreso mini-FABs on expansion |
| `QuickIncomeSheet` | New `ModalBottomSheet` composable for fast income registration from the FAB |
| `IncomeFrequency.ONE_TIME` | Enum value used for incomes registered via `QuickIncomeSheet` (not recurring) |
| `softWrap = false` | Compose `Text` parameter that disables word-wrap, causing hard clipping when container is narrow |
| `TextOverflow.Ellipsis` | Compose `Text` overflow strategy that appends "…" instead of hard-clipping |
| `contentPadding` | `LazyColumn` parameter that adds inset padding inside the scroll area without affecting item spacing |
