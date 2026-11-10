# Implementation Plan

## Overview

Plan de implementación para el bugfix de datos cruzados entre cuentas Nu/Rappi y el dashboard. Se sigue la metodología de condición de bug: primero se escribe el test de exploración (que falla en código sin corregir), luego los tests de preservación, y finalmente se implementa el fix en el orden Migración Room 11→12 → nuevas queries DAO → nuevo StateFlow en DashboardViewModel.

## Task Dependency Graph

```json
{
  "waves": [
    { "wave": 1, "tasks": ["1", "2"] },
    { "wave": 2, "tasks": ["3.1", "3.2"] },
    { "wave": 3, "tasks": ["3.3"] },
    { "wave": 4, "tasks": ["3.4", "3.5", "3.6"] },
    { "wave": 5, "tasks": ["4"] }
  ]
}
```

## Tasks

- [ ] 1. Write bug condition exploration test
  - **Property 1: Bug Condition** - Transacciones duplicadas y tipo de cuenta incorrecto
  - **CRITICAL**: This test MUST FAIL on unfixed code - failure confirms the bug exists
  - **DO NOT attempt to fix the test or the code when it fails**
  - **NOTE**: This test encodes the expected behavior - it will validate the fix when it passes after implementation
  - **GOAL**: Surface counterexamples that demonstrate the bug exists
  - **Scoped PBT Approach**: Scope the property to the concrete failing cases for reproducibility
  - Open an in-memory Room database at version 11 with a representative pre-migration dataset
  - Insert transactions IDs 6–27 (account_id=3), ID 87 (account_id=1), IDs 52–86 and 91–96 (account_id=2), and account ID=2 with type='CHECKING'
  - Assert: `transactions` where id IN (6..27) AND account_id=3 returns 22 rows (isBugCondition C1)
  - Assert: `transactions` where id=87 AND account_id=1 returns 1 row (isBugCondition C2)
  - Assert: `transactions` where id IN (52..86, 91..96) AND account_id=2 returns 41 rows (isBugCondition C3)
  - Assert: `accounts` where id=2 has type='CHECKING' (isBugCondition C4)
  - Run test on UNFIXED code (DB version 11, no MIGRATION_11_12 applied)
  - **EXPECTED OUTCOME**: Test FAILS when migration is applied — i.e. data is NOT yet clean (confirms the bug exists in raw state)
  - Document counterexamples found: "22 débitos falsos Nu presentes", "ID 87 duplicado presente", "41 débitos falsos Rappi presentes", "type='CHECKING' en cuenta 2"
  - Mark task complete when test is written, run, and failure is documented
  - Test file location: `app/src/androidTest/java/com/personalfinance/cuentas/DashboardBugExplorationTest.kt`
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.9_

- [ ] 2. Write preservation property tests (BEFORE implementing fix)
  - **Property 2: Preservation** - Registros no afectados por la migración
  - **IMPORTANT**: Follow observation-first methodology
  - Observe behavior on UNFIXED code for non-buggy inputs (transactions NOT in IDs 6–27, 52–96, 87; accounts NOT id=2)
  - Observe: transaction ID=34 exists with amount_cents=72_280_836 on account_id=1
  - Observe: card_purchases table COUNT is unchanged before and after migration
  - Observe: accounts with id=1 (Bancolombia) and id=3 (Nu) retain all their fields unchanged
  - Observe: recurring_items table is untouched by migration
  - Write property-based test: for all transaction IDs NOT IN (6..27, 52..96, 87), the row is identical before and after MIGRATION_11_12
  - Write property-based test: for all accounts with id != 2, all fields are identical before and after MIGRATION_11_12
  - Write property-based test: for any card_id, COUNT of card_purchases with is_fully_paid=false is the same before and after MIGRATION_11_12
  - Verify tests PASS on UNFIXED code (baseline behavior confirmed)
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9, 3.10_

- [ ] 3. Implement the fix for Nu account data & dashboard crosscheck

  - [ ] 3.1 Add new DAO queries
    - Add `getBalanceCents(accountId: Long): Flow<Long>` to `AccountDao.kt`
      ```kotlin
      @Query("SELECT current_balance_cents FROM accounts WHERE id = :accountId LIMIT 1")
      fun getBalanceCents(accountId: Long): Flow<Long>
      ```
    - Add `getPendingCycleCents(cardId: Long): Flow<Long>` to `CreditCardDao.kt`
      ```kotlin
      @Query("""
          SELECT COALESCE(SUM(total_amount_cents), 0)
          FROM card_purchases
          WHERE card_id = :cardId AND is_fully_paid = 0
      """)
      fun getPendingCycleCents(cardId: Long): Flow<Long>
      ```
    - File: `app/src/main/java/com/personalfinance/cuentas/data/local/dao/AccountDao.kt`
    - File: `app/src/main/java/com/personalfinance/cuentas/data/local/dao/CreditCardDao.kt`
    - _Bug_Condition: isBugCondition(db) — DAO queries needed to compute realAvailableCents_
    - _Expected_Behavior: getBalanceCents returns Flow<Long> for the specified account; getPendingCycleCents returns COALESCE SUM of total_amount_cents where is_fully_paid=0_
    - _Preservation: No existing DAO methods are modified; only new methods are added_
    - _Requirements: 2.5, 2.6_

  - [ ] 3.2 Define MIGRATION_11_12 and bump DB version
    - Open `app/src/main/java/com/personalfinance/cuentas/core/database/AppDatabase.kt`
    - Change `@Database(version = 11, ...)` to `version = 12`
    - Add in `companion object` after `MIGRATION_10_11`:
      ```kotlin
      val MIGRATION_11_12 = object : Migration(11, 12) {
          override fun migrate(db: SupportSQLiteDatabase) {
              // Fix 1 — 22 débitos falsos de la cuenta Nu (IDs 6–27)
              db.execSQL(
                  "DELETE FROM transactions WHERE id IN " +
                  "(6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27)"
              )
              // Fix 2 — Pago duplicado a Nu (ID 87); conservar ID 34
              db.execSQL("DELETE FROM transactions WHERE id = 87")
              // Fix 3 — 41 débitos falsos de la cuenta Rappi (IDs 52–86, 91–96)
              db.execSQL(
                  "DELETE FROM transactions WHERE id IN " +
                  "(52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71," +
                  "72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,91,92,93,94,95,96)"
              )
              // Fix 4 — Corregir tipo de la cuenta Rappi: CHECKING → SAVINGS
              db.execSQL("UPDATE accounts SET type = 'SAVINGS' WHERE id = 2")
          }
      }
      ```
    - Register migration in the Room builder: `.addMigrations(..., AppDatabase.MIGRATION_11_12)`
    - _Bug_Condition: isBugCondition(db) where duplicate transaction IDs and CHECKING type exist_
    - _Expected_Behavior: After migration, IDs 6–27, 87, 52–86, 91–96 do NOT exist in transactions; account id=2 has type='SAVINGS'_
    - _Preservation: Transactions with IDs not in the deleted sets are untouched; card_purchases is untouched; account id=1 and id=3 are untouched; transaction ID=34 (amount_cents=72_280_836) remains intact_
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.7_

  - [ ] 3.3 Add `realAvailableCents` StateFlow to DashboardViewModel
    - Open `app/src/main/java/com/personalfinance/cuentas/presentation/dashboard/DashboardViewModel.kt`
    - Inject or access `accountDao` and `creditCardDao` (verify they are already injected; add if missing)
    - Add private flows after the `staticSavingsBalanceCents` block:
      ```kotlin
      private val bancolombiaBalanceCents: Flow<Long> =
          accountDao.getBalanceCents(accountId = 1L)

      private val bancoBogotaMonthlyCents: Flow<Long> =
          debtDao.getActiveDebts().map { debts ->
              debts.sumOf { it.monthlyInstallmentCents }
          }

      private val rappiCardPendingCents: Flow<Long> =
          creditCardDao.getPendingCycleCents(cardId = 1L)

      private val nuCardPendingCents: Flow<Long> =
          creditCardDao.getPendingCycleCents(cardId = 2L)

      private val activeRecurringTotal: Flow<Long> =
          recurringItemDao.getAllActiveRecurringItems().map { items ->
              items.sumOf { it.amountCents }
          }
      ```
    - Add the public StateFlow:
      ```kotlin
      val realAvailableCents: StateFlow<Long> = combine(
          bancolombiaBalanceCents,
          bancoBogotaMonthlyCents,
          rappiCardPendingCents,
          nuCardPendingCents,
          activeRecurringTotal
      ) { bancolombia, bancoBogota, rappiCard, nuCard, recurring ->
          bancolombia - bancoBogota - rappiCard - nuCard - recurring
      }.stateIn(
          viewModelScope,
          SharingStarted.WhileSubscribed(5000),
          0L
      )
      ```
    - No existing StateFlow signatures or names may be changed
    - _Bug_Condition: isBugCondition(db) — dashboard disponible inflado because card obligations not deducted_
    - _Expected_Behavior: realAvailableCents = bancolombiaBalance − bancoBogotaMonthly − rappiCardPending − nuCardPending − activeRecurring; may be negative_
    - _Preservation: safeToSpend, commitmentsSummary, creditCardObligations, spendableBalanceCents and all other existing StateFlows remain unchanged in name, type and behavior_
    - _Requirements: 2.5, 2.6, 3.1, 3.7_

  - [ ] 3.4 Verify bug condition exploration test now passes
    - **Property 1: Expected Behavior** - Transacciones duplicadas eliminadas y tipo de cuenta corregido
    - **IMPORTANT**: Re-run the SAME test from task 1 — do NOT write a new test
    - Apply MIGRATION_11_12 to the in-memory database that was set up in task 1
    - The test from task 1 encodes the expected behavior: IDs 6–27, 87, 52–86, 91–96 do not exist; account id=2 has type='SAVINGS'
    - When this test passes, it confirms the migration produces the expected behavior
    - Run bug condition exploration test from step 1
    - **EXPECTED OUTCOME**: Test PASSES (confirms bug is fixed)
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.7_

  - [ ] 3.5 Verify preservation tests still pass
    - **Property 2: Preservation** - Registros no afectados intactos
    - **IMPORTANT**: Re-run the SAME tests from task 2 — do NOT write new tests
    - Run preservation property tests from step 2 against the fixed codebase
    - Confirm: transaction ID=34 still has amount_cents=72_280_836
    - Confirm: COUNT of card_purchases is unchanged
    - Confirm: accounts id=1 and id=3 are untouched
    - Confirm: recurring_items table is untouched
    - **EXPECTED OUTCOME**: Tests PASS (confirms no regressions)

  - [ ] 3.6 Write and run unit tests for new DAO queries and StateFlow
    - Test `AccountDao.getBalanceCents`: verify it returns the balance for account_id=1 and not for other accounts
    - Test `CreditCardDao.getPendingCycleCents`: verify it sums only `total_amount_cents` where `is_fully_paid=0` for the given card_id; verify returns 0 when all records have `is_fully_paid=1`
    - Test `realAvailableCents` StateFlow with mocked Flows: verify formula `B − D − R − N − E` is computed correctly for various combinations of positive and negative values
    - Test that `realAvailableCents` can emit a negative value when obligations exceed balance
    - Test file: `app/src/test/java/com/personalfinance/cuentas/presentation/dashboard/DashboardViewModelTest.kt`
    - _Requirements: 2.5, 2.6, 3.1_

- [ ] 4. Checkpoint - Ensure all tests pass
  - Run all unit tests and instrumentation tests
  - Confirm Property 1 (bug condition) test passes with migration applied
  - Confirm Property 2 (preservation) tests pass with migration applied
  - Confirm unit tests for new DAO queries pass
  - Confirm unit tests for `realAvailableCents` StateFlow pass
  - Ensure no regressions in existing ViewModels or UI tests
  - Ensure all tests pass; ask the user if questions arise

## Notes

- La migración Room opera directamente sobre `SupportSQLiteDatabase` con `execSQL`, sin necesidad de nuevas queries en `TransactionDao`.
- `BackupManager.restoreBackup` no requiere cambios: al importar un backup exportado post-fix los datos quedarán limpios por sí solos.
- `realAvailableCents` puede ser negativo; la UI debe manejarlo mostrando el valor en rojo.
- Los tests de exploración (tarea 1) deben ejecutarse antes del fix para confirmar que el bug existe; NO se deben corregir cuando fallen.
- Para los tests de preservación basados en propiedades, generar IDs de transacción aleatorios fuera del rango [6–27, 52–96, 87] y verificar que la migración los deja intactos.
