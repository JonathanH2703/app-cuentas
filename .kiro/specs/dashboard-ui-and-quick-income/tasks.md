# Implementation Plan

## Overview

This plan implements the two bugfixes for the Dashboard UI overflow issues and the FAB single-action limitation, following the exploratory bugfix workflow: explore the bugs with property tests before fixing, capture preservation baselines, then apply the fixes and validate.

## Tasks

- [ ] 1. Write bug condition exploration test
  - **Property 1: Bug Condition** - Dashboard Visual Overflow & FAB Single Action
  - **CRITICAL**: This test MUST FAIL on unfixed code — failure confirms both bugs exist
  - **DO NOT attempt to fix the test or the code when it fails**
  - **NOTE**: This test encodes the expected behavior — it will validate the fix when it passes after implementation
  - **GOAL**: Surface counterexamples that demonstrate both bugs exist
  - **Scoped PBT Approach**: For Bug 1, scope to the concrete failing condition: screenWidthDp ≤ 420 with softWrap=false labels. For Bug 2, scope to the concrete failing case: FAB tap results in only "expense_only" in availableActions
  - **Bug 1 — P1 Metric Labels**: Render `CompactFinancialHeroCard` across the five reference widths [320, 360, 393, 412, 430] dp. Assert that the three label `Text`s (`leftLabel`, `centreLabel`, `rightLabel`) are all displayed and none use `softWrap = false`. On unfixed code this FAILS because labels use `softWrap = false` and are hard-clipped on narrow widths.
  - **Bug 1 — P2 Toggle Duplicated**: Render `DashboardScreen`. Use `onAllNodesWithText("Actual").assertCountEquals(1)` — on unfixed code this FAILS because toggle appears twice (once in `CompactFinancialHeroCard`, once in `CashFlowChartCard`).
  - **Bug 2 — P3 FAB Single Action**: Render `MainScaffold`. Tap the FAB. Assert `isDisplayed(contentDescription="Registrar Ingreso") = true` AND `isDisplayed(contentDescription="Registrar Gasto") = true` — on unfixed code this FAILS because no expandable menu exists.
  - Run test on UNFIXED code
  - **EXPECTED OUTCOME**: Tests FAIL (this is correct — it proves the bugs exist)
  - Document counterexamples found:
    - P1: e.g. "label 'Por día (14d)' at 320 dp returns isDisplayed=false" or has softWrap=false
    - P2: e.g. `onAllNodesWithText("Actual")` returns count=2
    - P3: e.g. `onNodeWithContentDescription("Registrar Ingreso")` throws AssertionError (node not found)
  - Mark task complete when tests are written, run, and failures are documented
  - _Requirements: 1.1, 1.3, 1.5_

- [~] 2. Write preservation property tests (BEFORE implementing fix)
  - **Property 2: Preservation** - Unchanged Dashboard Rendering & QuickExpenseSheet Behavior
  - **IMPORTANT**: Follow observation-first methodology
  - **Observe on UNFIXED code** (non-bug-condition inputs where `isBugCondition1/2` returns false):
    - P1 preservation: On screens > 420 dp with `softWrap = false`, observe that labels are not clipped (wider screens have enough room). Record displayed state.
    - P2 preservation: Toggle state changes in `CompactFinancialHeroCard` propagate to `CashFlowChartCard` — observe that `CashFlowChartCard.isProjectedView` updates correctly.
    - P3 preservation: Tap FAB and select the expense path → `QuickExpenseSheet` opens. Observe and record this behavior is intact.
  - **Write property-based tests** capturing observed behavior for all non-bug-condition inputs:
    - **Preserve 1**: For all screen widths in [320, 360, 393, 412, 430] dp (post-fix all pass; this validates regression prevention for requirement 3.1). Assert all three metric label nodes have `isDisplayed = true`.
    - **Preserve 2**: `DashboardScreen` toggle state change — `CompactFinancialHeroCard` callback writes `isProjectedView`, and `CashFlowChartCard` reads it. Assert toggle count stays = 1 after state flip. (Validates requirement 3.3)
    - **Preserve 3**: Tapping "Registrar Gasto" mini-FAB opens `QuickExpenseSheet`. Assert `QuickExpenseSheet` is displayed after tapping the Gasto action. (Validates requirement 3.2)
  - Run tests on UNFIXED code
  - **EXPECTED OUTCOME**: Tests PASS on UNFIXED code (confirms baseline behavior to preserve)
  - Mark task complete when tests are written, run, and passing on unfixed code
  - _Requirements: 3.1, 3.2, 3.3_

- [ ] 3. Fix dashboard visual overflow and FAB expandable menu

  - [~] 3.1 Fix metric label text overflow in `CompactFinancialHeroCard`
    - File: `presentation/dashboard/DashboardComponents.kt`
    - In the three-metric `Row` inside `CompactFinancialHeroCard`, replace `softWrap = false` with `overflow = TextOverflow.Ellipsis` on all three label `Text` composables (left: "Libre hoy"/"Libre proyectado", centre: "Por entrar"/"Ahorro protegido"/"Líquido gastable", right: "Por día (Xd)")
    - Value `Text`s (displaying formatted amounts) already use `maxLines = 1` without `softWrap = false` — leave them unchanged
    - _Bug_Condition: isBugCondition1(X) where X.heroCardMetricsRow.uses_softWrap_false = true_
    - _Expected_Behavior: result.heroMetrics.noTextClipped = true (labels rendered with TextOverflow.Ellipsis)_
    - _Preservation: Dashboard renders correctly on all screen sizes; other Text composables in the card are not modified_
    - _Requirements: 2.1, 3.1_

  - [~] 3.2 Reduce `LazyColumn` padding and spacing in `DashboardScreen`
    - File: `presentation/dashboard/DashboardScreen.kt`
    - Change `.padding(horizontal = 20.dp)` → `.padding(horizontal = 12.dp)` on the `LazyColumn` modifier
    - Change `verticalArrangement = Arrangement.spacedBy(14.dp)` → `Arrangement.spacedBy(10.dp)`
    - Keep `contentPadding = PaddingValues(bottom = 96.dp)` unchanged
    - _Bug_Condition: isBugCondition1(X) where X.lazyColumnHorizontalPadding = 20.dp_
    - _Expected_Behavior: LazyColumn uses horizontal=12.dp and spacedBy(10.dp) for better card width and reduced dead space_
    - _Preservation: Layout hierarchy and scroll behavior unchanged; all sections still render in order_
    - _Requirements: 2.2, 3.1, 3.6_

  - [~] 3.3 Pass `onViewModeChange = null` to `CashFlowChartCard` to remove duplicate toggle
    - File: `presentation/dashboard/DashboardScreen.kt`
    - Change the `CashFlowChartCard` call site: replace `onViewModeChange = { isProjectedView = it }` with `onViewModeChange = null`
    - `CompactFinancialHeroCard` remains the sole owner of `isProjectedView` state and its `onViewModeChange` callback — do not modify it
    - `CashFlowChartCard` in `DashboardComponents.kt` already guards with `if (onViewModeChange != null)` — no changes needed inside it
    - _Bug_Condition: isBugCondition1(X) where X.cashFlowCard.hasOwnToggle = true_
    - _Expected_Behavior: result.toggleCount = 1 (toggle only in CompactFinancialHeroCard)_
    - _Preservation: CashFlowChartCard still receives and reacts to isProjectedView state changes (requirement 3.3)_
    - _Requirements: 2.3, 3.3_

  - [~] 3.4 Remove redundant `Spacer(72.dp)` from `DashboardScreen` `LazyColumn`
    - File: `presentation/dashboard/DashboardScreen.kt`
    - Remove the `item { Spacer(modifier = Modifier.height(72.dp)) }` item from the `LazyColumn`
    - `contentPadding = PaddingValues(bottom = 96.dp)` provides sufficient clearance above the FAB on its own
    - _Bug_Condition: isBugCondition1(X) — redundant Spacer causing ~168dp dead scroll space_
    - _Expected_Behavior: result.bottomScrollSpace <= 100.dp (only 96.dp from contentPadding)_
    - _Preservation: Scroll behavior and FAB clearance unchanged; content still scrolls fully without being obscured by FAB_
    - _Requirements: 2.4, 3.6_

  - [~] 3.5 Create `QuickIncomeSheet` composable
    - New file: `presentation/transactions/QuickIncomeSheet.kt`
    - Implement `QuickIncomeSheet(sheetState, accounts, onDismiss, onSaveIncome)` as a `ModalBottomSheet`
    - UI fields: amount `OutlinedTextField` (digits + comma, max 2 decimal places, prefixed with "$ "), account selector as `FilterChip` `LazyRow`, optional description `OutlinedTextField`
    - Initial `selectedAccountId` = `accounts.firstOrNull()?.id ?: 0L`
    - Parse raw amount string to cents via `CurrencyFormatter.parseCopToCents(rawAmount)`
    - Confirm button: `enabled = amountCents > 0L && selectedAccountId != 0L`; on click calls `onSaveIncome(selectedAccountId, amountCents, description.trim())` then `onDismiss()`
    - If `accounts` is empty, `selectedAccountId` stays `0L` and confirm button stays disabled — no crash
    - _Bug_Condition: isBugCondition2(X) where X.availableActions = {"expense_only"}_
    - _Expected_Behavior: QuickIncomeSheet renders and calls onSaveIncome with correct (accountId, amountCents, description)_
    - _Preservation: QuickExpenseSheet is not modified; IncomesScreen tab (pestaña 5) is not modified_
    - _Requirements: 2.6, 3.4, 3.5_

  - [~] 3.6 Replace single FAB with `ExpandableFabMenu` in `MainScaffold`
    - File: `presentation/navigation/MainScaffold.kt`
    - Add state variables: `var showFabMenu by remember { mutableStateOf(false) }` and `var showQuickIncomeSheet by remember { mutableStateOf(false) }`
    - Implement private `ExpandableFabMenu(expanded, onToggle, onExpenseClick, onIncomeClick, modifier)` composable:
      - Full-screen transparent scrim `Box` (with `indication = null`) dismisses menu on outside tap when `expanded = true`
      - `BackHandler(enabled = expanded)` collapses menu on system Back
      - `AnimatedVisibility` (fadeIn + slideInVertically / fadeOut + slideOutVertically) shows two `SmallFloatingActionButton`s with label `Surface`s above the main FAB
      - "Ingreso" mini-FAB: `containerColor = FinancialGreen`, `contentDescription = "Registrar Ingreso"`, `Icons.Default.TrendingUp`, calls `onIncomeClick()`
      - "Gasto" mini-FAB: `containerColor = FinancialRed`, `contentDescription = "Registrar Gasto"`, `Icons.Default.TrendingDown`, calls `onExpenseClick()`
      - Main FAB icon rotates 45° via `animateFloatAsState` when `expanded = true`
    - Replace `floatingActionButton` slot content with `ExpandableFabMenu(expanded = showFabMenu, onToggle = { showFabMenu = !showFabMenu }, onExpenseClick = { showFabMenu = false; showQuickExpenseSheet = true }, onIncomeClick = { showFabMenu = false; showQuickIncomeSheet = true })`
    - Wire `QuickIncomeSheet` below the existing `QuickExpenseSheet` block: when `showQuickIncomeSheet = true`, show `QuickIncomeSheet` with `onSaveIncome = { accountId, amountCents, desc -> incomesViewModel.createIncome(description = desc.ifBlank { "Ingreso rápido" }, amountCents = amountCents, accountId = accountId, frequency = IncomeFrequency.ONE_TIME, expectedDateMillis = System.currentTimeMillis()) }`
    - _Bug_Condition: isBugCondition2(X) where X.fabTapped = true AND X.availableActions = {"expense_only"}_
    - _Expected_Behavior: "register_income" AND "register_expense" both in result.availableActions after FAB tap_
    - _Preservation: QuickExpenseSheet flow unchanged (requirement 3.2); BackHandler does not interfere with pager navigation when menu is closed_
    - _Requirements: 2.5, 2.6, 3.2_

  - [~] 3.7 Verify bug condition exploration test now passes
    - **Property 1: Expected Behavior** - Dashboard Visual Overflow & FAB Single Action Fixed
    - **IMPORTANT**: Re-run the SAME tests from task 1 — do NOT write new tests
    - The tests from task 1 encode the expected behavior; when they pass, the bugs are fixed
    - Run P1 (metric labels displayed across all 5 widths, no softWrap=false), P2 (toggle count = 1), P3 (both FAB actions visible) from task 1
    - **EXPECTED OUTCOME**: All three tests PASS (confirms bugs 1.1, 1.3, and 2.1 are fixed)
    - _Requirements: 2.1, 2.3, 2.5_

  - [~] 3.8 Verify preservation tests still pass
    - **Property 2: Preservation** - Unchanged Dashboard Rendering & QuickExpenseSheet Behavior
    - **IMPORTANT**: Re-run the SAME tests from task 2 — do NOT write new tests
    - Run all three preservation tests: label display across widths, toggle propagation, QuickExpenseSheet still opens from Gasto action
    - **EXPECTED OUTCOME**: Tests PASS (confirms no regressions in requirements 3.1, 3.2, 3.3)
    - Confirm all tests still pass after fix

  - [~] 3.9 Write and run targeted unit tests for `QuickIncomeSheet`
    - **P4 — Confirm button disabled when input invalid**: Render `QuickIncomeSheet` with `amountCents ∈ {0L, -1L, Long.MIN_VALUE}` and `accountId = 0L`. For all `description ∈ {"", "Salario", longString(100)}`, assert `confirmButton.enabled = false`. Also assert `confirmButton.enabled = true` for representative valid `(amountCents > 0, accountId ≠ 0L)` pairs. (Validates requirement 2.6)
    - **P5 — createIncome called with correct arguments**: Use a fake/mock `IncomesViewModel`. For each `(accountId, amountCents, description)` in the cross-product of `validAccountIds()` × `{1L, 100_000L, 9_999_999L}` × `{"", "Salario", "Freelance enero"}`, confirm the sheet and assert `lastCall(incomesViewModel.createIncome)` has `accountId`, `amountCents`, `frequency = IncomeFrequency.ONE_TIME`, and `description = if (desc.isBlank()) "Ingreso rápido" else desc`. (Validates requirements 2.6, 3.5)
    - _Requirements: 2.6, 3.5_

- [~] 4. Checkpoint — Ensure all tests pass
  - Run the full test suite (unit + Compose UI tests)
  - Confirm P1, P2, P3 (bug condition tests from task 1) all PASS
  - Confirm P2 preservation tests (task 2) all PASS
  - Confirm P4 and P5 (QuickIncomeSheet tests from task 3.9) all PASS
  - Ensure all tests pass; ask the user if any questions arise

## Task Dependency Graph

```json
{
  "waves": [
    {
      "wave": 1,
      "tasks": ["1", "2"],
      "description": "Exploration and preservation tests written on unfixed code"
    },
    {
      "wave": 2,
      "tasks": ["3.1", "3.2", "3.3", "3.4", "3.5"],
      "description": "Independent fix sub-tasks: label overflow, padding/spacing, toggle dedup, Spacer removal, and new QuickIncomeSheet"
    },
    {
      "wave": 3,
      "tasks": ["3.6"],
      "description": "ExpandableFabMenu + QuickIncomeSheet wiring in MainScaffold (depends on 3.5)"
    },
    {
      "wave": 4,
      "tasks": ["3.7", "3.8", "3.9"],
      "description": "Validation: re-run bug condition tests, preservation tests, and write/run QuickIncomeSheet unit tests P4+P5"
    },
    {
      "wave": 5,
      "tasks": ["4"],
      "description": "Checkpoint: all tests pass"
    }
  ]
}
```

## Notes

- **Test framework**: Use `createComposeRule()` for Compose UI tests (P1–P3, P4). Use `mockk` or a hand-rolled fake for `IncomesViewModel` in P5 unit tests. No external PBT library required — bounded input spaces are covered with explicit parameterized loops (`forEach`).
- **Screen width simulation**: Use `setContent { ... }` with a `Box(Modifier.requiredWidth(widthDp.dp))` wrapper to simulate specific screen widths in Compose tests.
- **`FinancialGreen` / `FinancialRed`**: These color constants are expected to already exist in the project's theme (`core/ui/theme/Color.kt`). Verify their names before referencing them in `ExpandableFabMenu` and `QuickIncomeSheet`.
- **`CurrencyFormatter.parseCopToCents`**: Confirm this utility method exists and its exact signature before using it in `QuickIncomeSheet`. If missing, implement the parse logic inline.
- **`IncomeFrequency.ONE_TIME`**: Confirm the exact enum value name in `IncomeEntity` / domain model before wiring in `MainScaffold`.
- **Files modified**: `DashboardComponents.kt`, `DashboardScreen.kt`, `MainScaffold.kt`. **New file**: `presentation/transactions/QuickIncomeSheet.kt`. No other files are changed.
