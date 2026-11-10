# Nu Account Data & Dashboard Crosscheck Bugfix Design

## Overview

Este bugfix corrige cinco defectos en la base de datos local y en el ViewModel del dashboard
de la app Android de finanzas personales (Kotlin + Jetpack Compose + Room + MVVM).

Los cambios son:

1. **Eliminación de transacciones duplicadas Nu (IDs 6–27)**: 22 débitos falsos sobre la cuenta
   Nu (account_id: 3) que replican compras ya registradas en `card_purchases` (card_id: 2).
2. **Eliminación del pago duplicado a Nu (ID 87)**: la transacción 87 duplica la 34;
   ambas representan el mismo pago de $722.808,36 a Nu sobre Bancolombia (account_id: 1).
3. **Eliminación de transacciones duplicadas Rappi (IDs 52–86, 91–96)**: 41 débitos falsos
   sobre la cuenta Rappi (account_id: 2) que replican compras ya registradas en
   `card_purchases` (card_id: 1).
4. **Corrección del tipo de la cuenta Rappi**: el campo `type` de account_id: 2 debe cambiar
   de `CHECKING` a `SAVINGS`.
5. **Nuevo cálculo de "Disponible Real" en el dashboard**: el `DashboardViewModel` debe
   calcular `Bancolombia − cuota BancoBogotá − saldo pendiente ciclo Rappi card −
   saldo pendiente ciclo Nu card − gastos recurrentes activos`.

La corrección de datos se ejecuta mediante una **migración Room (versión 11 → 12)** que
aplica las sentencias SQL de limpieza en una única transacción atómica al arrancar la app.
El cálculo del dashboard se extiende con un nuevo `StateFlow<Long>` en `DashboardViewModel`
sin modificar la firma de ningún flujo existente.

---

## Glossary

- **Bug_Condition (C)**: condición que identifica los registros o el estado incorrecto en la BD.
- **Property (P)**: comportamiento correcto esperado tras aplicar el fix.
- **Preservation**: comportamientos existentes que no deben cambiar.
- **account_id: 1** — Cuenta Bancolombia (cuenta principal de débito/pago).
- **account_id: 2** — Cuenta Rappi (`is_static_saving: true`, `include_in_total: false`).
- **account_id: 3** — Cuenta Nu (cuenta débito Nu).
- **card_id: 1** — Tarjeta de crédito Rappi, vinculada a account_id: 2.
- **card_id: 2** — Tarjeta de crédito Nu, vinculada a account_id: 3.
- **transactions** — Tabla Room con débitos/créditos reales sobre cuentas de débito.
- **card_purchases** — Tabla Room con compras en tarjetas de crédito (cuotas diferidas).
- **MIGRATION_11_12** — Objeto `Migration(11, 12)` a definir en `AppDatabase.kt`.
- **realAvailableCents** — Nuevo `StateFlow<Long>` en `DashboardViewModel` que expone el
  dinero realmente disponible para gastar (Bancolombia neto de todas las obligaciones).
- **BackupManager.restoreBackup** — Flujo alternativo de corrección si el usuario importa
  el backup corregido en lugar de actualizar la app.

---

## Bug Details

### Bug Condition

El bug se manifiesta cuando la base de datos Room contiene transacciones de débito en tablas
de cuentas que en realidad corresponden a cargos de tarjeta de crédito ya registrados en
`card_purchases`, y cuando el campo `type` de la cuenta Rappi tiene el valor incorrecto.
Adicionalmente, el cálculo de disponible del dashboard no descuenta obligaciones de tarjetas
ni deudas activas.

**Formal Specification:**

```
FUNCTION isBugCondition(db)
  INPUT: db — estado de la base de datos Room
  OUTPUT: boolean

  hasDuplicateNuTransactions    := EXISTS tx IN db.transactions
                                    WHERE tx.id IN [6,7,8,9,10,11,12,13,14,15,
                                                    16,17,18,19,20,21,22,23,24,25,26,27]
                                      AND tx.account_id = 3

  hasDuplicateNuPayment         := EXISTS tx IN db.transactions WHERE tx.id = 87
                                    AND tx.account_id = 1

  hasDuplicateRappiTransactions := EXISTS tx IN db.transactions
                                    WHERE tx.id IN [52,53,54,55,56,57,58,59,60,61,62,
                                                    63,64,65,66,67,68,69,70,71,72,73,74,
                                                    75,76,77,78,79,80,81,82,83,84,85,86,
                                                    91,92,93,94,95,96]
                                      AND tx.account_id = 2

  hasWrongRappiAccountType      := EXISTS acc IN db.accounts
                                    WHERE acc.id = 2 AND acc.type = 'CHECKING'

  RETURN hasDuplicateNuTransactions
      OR hasDuplicateNuPayment
      OR hasDuplicateRappiTransactions
      OR hasWrongRappiAccountType
END FUNCTION
```

### Examples

- `transactions` ID 6–27 tienen `account_id: 3` y merchant/monto coincidente con
  entradas en `card_purchases` con `card_id: 2` → el saldo Nu aparece artificialmente
  negativo y los gastos del mes se duplican en el dashboard.
- `transactions` ID 87 duplica la ID 34 (mismo `account_id: 1`, mismo `amount_cents:
  72_280_836`) → el saldo Bancolombia se debita $722.808,36 extra.
- `transactions` IDs 52–86 y 91–96 tienen `account_id: 2` y merchant/monto coincidente
  con `card_purchases` `card_id: 1` → el saldo Rappi aparece inflado negativamente.
- La cuenta Rappi (account_id: 2) muestra icono y etiqueta de "Cuenta Corriente" cuando
  el usuario la usa como ahorro estático (`is_static_saving: true`).
- El semáforo "Libre hoy" muestra, p. ej., $3.500.000 disponibles cuando en realidad
  quedan < $500.000 después de descontar obligaciones de tarjetas y BancoBogotá.

---

## Expected Behavior

### Preservation Requirements

**Unchanged Behaviors:**
- Los registros de `card_purchases` para card_id: 1 (Rappi) y card_id: 2 (Nu) no se modifican.
- La transacción ID 34 (pago real a Nu, $722.808,36) permanece intacta en `transactions`.
- Todas las transacciones de Bancolombia (account_id: 1) que no sean la ID 87 permanecen.
- El campo `include_in_total: false` de la cuenta Rappi no cambia.
- El campo `is_static_saving: true` de la cuenta Rappi no cambia.
- El saldo de la cuenta Nu (account_id: 3) queda en $0 si no hay transacciones de débito reales
  distintas de los IDs eliminados (requisito 3.6).
- Los flujos de pago de tarjeta y la lógica `cardNextPaymentCents` no cambian.
- La pantalla de Ingresos (pestaña 5) y todos los demás ViewModels quedan sin cambios.
- El flujo de importación de backup (`BackupManager.restoreBackup`) no cambia en su lógica;
  solo el JSON del backup exportado después del fix contendrá los datos limpios.

**Scope:**
Cualquier transacción con `account_id` distinto de 1, 2, 3 no se toca. Cualquier
transacción con `account_id: 1` cuyo ID no sea 87 no se toca. Cualquier transacción con
`account_id: 2` cuyo ID no esté en el rango 52–96 no se toca. Cualquier transacción con
`account_id: 3` cuyo ID sea menor a 6 o mayor a 27 no se toca.

---

## Hypothesized Root Cause

1. **Doble registro por flujo SMS + entrada manual**: el parser de SMS creó las transacciones
   1–27 en `transactions` al recibir notificaciones de Nu. Posteriormente el usuario también
   importó las mismas operaciones como compras de tarjeta en `card_purchases`. Ambos registros
   convivieron sin lógica de deduplicación.

2. **Pago duplicado manual**: la transacción 87 fue creada manualmente por el usuario mientras
   la transacción 34 (detectada automáticamente por SMS) ya existía en la BD. No existe ningún
   guard de unicidad para pagos de tarjeta por fecha+monto en `transactions`.

3. **Mismo patrón SMS + importación para Rappi**: las IDs 52–96 (con el salto de 87–90 por los
   IDs usados en la deuda BancoBogotá) siguen el mismo patrón que las IDs 6–27 pero para la
   tarjeta Rappi.

4. **Tipo de cuenta incorrecto en seed/backup**: la cuenta Rappi se creó originalmente con
   `type = CHECKING` (valor por defecto de `AccountType`); nunca se actualizó al importar el
   backup que incluía `is_static_saving: true`.

5. **Dashboard ignora obligaciones de tarjeta en la fórmula de disponible**: la propiedad
   `realAvailableCents` no existe; el dashboard usa `spendableBalanceCents` (suma de saldos
   de cuentas con `is_static_saving = false`) sin descontar saldos pendientes de tarjetas ni
   la cuota de BancoBogotá. `commitmentsSummary.totalNeedsCommitments` calcula las obligaciones
   pero no hay un StateFlow que exponga `Bancolombia − obligaciones` como métrica independiente.

---

## Correctness Properties

Property 1: Bug Condition — Transacciones duplicadas eliminadas y tipo de cuenta corregido

_For any_ estado de la base de datos donde `isBugCondition(db)` retorna `true`, tras ejecutar
la migración Room 11 → 12, el sistema SHALL dejar la base de datos en un estado donde ninguno
de los IDs 6–27, 52–96 (sin IDs 88–90) ni el 87 exista en `transactions`, y donde
`accounts.type` para `id = 2` sea `SAVINGS`.

**Validates: Requirements 2.2, 2.3, 2.4, 2.7**

Property 2: Preservation — Registros no afectados intactos

_For any_ transacción en `transactions` con `id NOT IN (6..27, 52..96, 87)` o con
`account_id NOT IN (1, 2, 3)`, la migración SHALL dejar ese registro sin cambios. La
transacción 34 SHALL existir y su `amount_cents` SHALL seguir siendo 72.280.836.

**Validates: Requirements 3.1, 3.2, 3.3, 3.4, 3.5, 3.6**

Property 3: Bug Condition — Disponible real calculado correctamente

_For any_ estado de la app donde `account_id: 1` (Bancolombia) tiene saldo `B`, la deuda
BancoBogotá tiene cuota mensual `D`, la tarjeta Rappi tiene saldo pendiente sin pagar `R`,
la tarjeta Nu tiene saldo pendiente sin pagar `N`, y los recurrentes activos suman `E`,
el `StateFlow realAvailableCents` SHALL emitir `B − D − R − N − E`.

**Validates: Requirements 2.5, 2.6**

Property 4: Preservation — Flujos existentes del ViewModel sin cambios de firma

_For any_ `StateFlow` ya expuesto por `DashboardViewModel` (incluyendo `safeToSpend`,
`commitmentsSummary`, `creditCardObligations`, `spendableBalanceCents`, etc.), la adición
de `realAvailableCents` SHALL NO modificar su tipo, nombre ni comportamiento observable.

**Validates: Requirements 3.1, 3.7**

---

## Fix Implementation

### Bug 1, 2, 3 y 4 — Migración Room MIGRATION_11_12

**Archivo:** `app/src/main/java/com/personalfinance/cuentas/core/database/AppDatabase.kt`

**Nuevo objeto de migración** (añadir en el bloque `companion object`, después de
`MIGRATION_10_11`):

```kotlin
val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Fix 1 — Eliminar los 22 débitos falsos de la cuenta Nu (IDs 6–27)
        db.execSQL(
            "DELETE FROM transactions WHERE id IN " +
            "(6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27)"
        )

        // Fix 2 — Eliminar el pago duplicado a Nu (ID 87); conservar el 34
        db.execSQL("DELETE FROM transactions WHERE id = 87")

        // Fix 3 — Eliminar los 41 débitos falsos de la cuenta Rappi (IDs 52–86, 91–96)
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

**Actualizar la anotación `@Database`:**

```kotlin
// ANTES
version = 11,

// DESPUÉS
version = 12,
```

**Registrar la migración en el builder de Room** (ubicar la llamada a `.addMigrations()`
en el módulo Hilt o en el singleton de la BD):

```kotlin
// Antes (fragmento ilustrativo del módulo de inyección):
.addMigrations(
    AppDatabase.MIGRATION_1_2,
    AppDatabase.MIGRATION_2_3,
    AppDatabase.MIGRATION_3_4,
    AppDatabase.MIGRATION_4_5,
    AppDatabase.MIGRATION_5_6,
    AppDatabase.MIGRATION_6_7,
    AppDatabase.MIGRATION_7_8,
    AppDatabase.MIGRATION_8_9,
    AppDatabase.MIGRATION_9_10,
    AppDatabase.MIGRATION_10_11
)

// Después:
.addMigrations(
    AppDatabase.MIGRATION_1_2,
    AppDatabase.MIGRATION_2_3,
    AppDatabase.MIGRATION_3_4,
    AppDatabase.MIGRATION_4_5,
    AppDatabase.MIGRATION_5_6,
    AppDatabase.MIGRATION_6_7,
    AppDatabase.MIGRATION_7_8,
    AppDatabase.MIGRATION_8_9,
    AppDatabase.MIGRATION_9_10,
    AppDatabase.MIGRATION_10_11,
    AppDatabase.MIGRATION_11_12          // ← nuevo
)
```

> **Nota sobre el flujo de backup**: `BackupManager.restoreBackup` borra y reinsertas todos
> los datos; si el usuario importa un backup ya corregido (exportado tras el fix), la BD
> queda limpia naturalmente. No se requiere ningún cambio en `BackupManager`.

---

### Bug 5 — Nuevo StateFlow `realAvailableCents` en `DashboardViewModel`

**Archivo:** `app/src/main/java/com/personalfinance/cuentas/presentation/dashboard/DashboardViewModel.kt`

**Objetivo**: exponer una métrica que represente el dinero del que el usuario realmente
puede disponer hoy:

```
realAvailableCents =
  saldo_Bancolombia
  − cuota_mensual_BancoBogotá
  − saldo_pendiente_ciclo_Rappi_card   (card_id: 1, is_fully_paid = false)
  − saldo_pendiente_ciclo_Nu_card      (card_id: 2, is_fully_paid = false,
                                        incluyendo id=58 Intereses diferidos)
  − suma_recurrentes_activos
```

**Dependencias de datos ya inyectadas en el constructor:**
- `accountDao` — para leer el saldo de Bancolombia
- `debtDao` — para leer la cuota mensual de BancoBogotá
- `creditCardDao` — para leer `card_purchases` con `is_fully_paid = false` por `card_id`
- `recurringItemDao` — para leer los recurrentes activos

**Nuevas queries necesarias en DAOs:**

```kotlin
// En AccountDao.kt — obtener saldo de una cuenta específica como Flow
@Query("SELECT current_balance_cents FROM accounts WHERE id = :accountId LIMIT 1")
fun getBalanceCents(accountId: Long): Flow<Long>

// En CreditCardDao.kt — saldo pendiente de compras no saldadas por tarjeta
@Query("""
    SELECT COALESCE(SUM(total_amount_cents), 0)
    FROM card_purchases
    WHERE card_id = :cardId AND is_fully_paid = 0
""")
fun getPendingCycleCents(cardId: Long): Flow<Long>
```

> La query `getPendingCycleCents` suma `total_amount_cents` de todas las compras donde
> `is_fully_paid = false`, por lo que incluye automáticamente la compra ID 58
> (Intereses corrientes diferidos Nu, $18.165,11) sin lógica adicional.

**Implementación del StateFlow en `DashboardViewModel`:**

```kotlin
// Saldo de Bancolombia en tiempo real
private val bancolombiaBalanceCents: Flow<Long> =
    accountDao.getBalanceCents(accountId = 1L)

// Cuota mensual de BancoBogotá: primera deuda activa cuyo name contenga "Bogotá" o id conocido.
// Se suma todo para no depender de un ID hardcodeado que podría cambiar al restaurar backups.
private val bancoBogotaMonthlyCents: Flow<Long> =
    debtDao.getActiveDebts().map { debts ->
        // BancoBogotá es la única deuda activa; si en el futuro hay varias,
        // se debe filtrar por un campo diferenciador (p. ej. name.contains("Bogotá")).
        debts.sumOf { it.monthlyInstallmentCents }
    }

// Saldo pendiente ciclo Rappi card (card_id = 1)
private val rappiCardPendingCents: Flow<Long> =
    creditCardDao.getPendingCycleCents(cardId = 1L)

// Saldo pendiente ciclo Nu card (card_id = 2, incluye ID 58 por is_fully_paid = false)
private val nuCardPendingCents: Flow<Long> =
    creditCardDao.getPendingCycleCents(cardId = 2L)

// Suma de recurrentes activos
private val activeRecurringTotal: Flow<Long> =
    recurringItemDao.getAllActiveRecurringItems().map { items ->
        items.sumOf { it.amountCents }
    }

/**
 * Dinero realmente disponible para gastar hoy.
 * Fórmula: Bancolombia − cuota BancoBogotá − saldo pendiente Rappi card
 *          − saldo pendiente Nu card − recurrentes activos
 *
 * Puede ser negativo si las obligaciones superan el saldo; ese caso debe mostrarse en rojo.
 */
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

**Localización en el archivo**: insertar inmediatamente después del bloque de
`staticSavingsBalanceCents` y antes de `accounts`, para mantener el orden
"saldos → compromisos → métricas derivadas".

---

### Queries faltantes — TransactionDao

`TransactionDao` no tiene una query para eliminar transacciones por lista de IDs. La
migración Room opera directamente sobre `SupportSQLiteDatabase` mediante `db.execSQL()`,
por lo que **no es necesario añadir ninguna query** a `TransactionDao` para el fix de datos.
Las sentencias SQL de la migración son auto-contenidas.

---

## Testing Strategy

### Validation Approach

La estrategia sigue dos fases: primero verificar los counterexamples en código sin migrar
(confirmación de que el bug existe), luego verificar que la migración y el nuevo StateFlow
corrigen el comportamiento.

### Exploratory Bug Condition Checking

**Goal**: demostrar el bug ANTES de aplicar el fix. Confirmar o refutar las hipótesis
de causa raíz.

**Test Plan**: conectar a la BD en estado pre-migración (versión 11) y verificar la
presencia de los IDs problemáticos y el tipo incorrecto de la cuenta.

**Test Cases:**

1. **Transacciones Nu duplicadas presentes**: consultar `transactions` donde
   `id IN (6..27) AND account_id = 3`; deben existir 22 registros → confirma C1.
2. **Pago Nu duplicado presente**: consultar `transactions` donde `id = 87`;
   debe existir → confirma C2.
3. **Transacciones Rappi duplicadas presentes**: consultar `transactions` donde
   `id IN (52..96 sin 88–90) AND account_id = 2`; deben existir 41 registros → confirma C3.
4. **Tipo de cuenta Rappi incorrecto**: consultar `accounts` donde `id = 2`;
   `type` debe ser `CHECKING` → confirma C4.
5. **Disponible inflado**: calcular `spendableBalanceCents` sin descontar obligaciones
   de tarjeta; la diferencia vs. `realAvailableCents` (post-fix) debe ser > 0 → confirma C5.

**Expected Counterexamples:**
- 22 filas retornadas por la query 1 (bug confirmado).
- 1 fila retornada por la query 2 (bug confirmado).
- 41 filas retornadas por la query 3 (bug confirmado).
- `type = 'CHECKING'` en la query 4 (bug confirmado).

### Fix Checking

**Goal**: verificar que para todos los inputs donde `isBugCondition(db)` es `true`,
la migración produce el comportamiento correcto.

**Pseudocode:**
```
FOR ALL db WHERE isBugCondition(db) DO
  db' := applyMigration_11_12(db)
  ASSERT NOT EXISTS tx IN db'.transactions WHERE tx.id IN (6..27)
  ASSERT NOT EXISTS tx IN db'.transactions WHERE tx.id = 87
  ASSERT NOT EXISTS tx IN db'.transactions WHERE tx.id IN (52..96 EXCEPT 88,89,90)
  ASSERT EXISTS acc IN db'.accounts WHERE acc.id = 2 AND acc.type = 'SAVINGS'
  ASSERT EXISTS tx IN db'.transactions WHERE tx.id = 34
END FOR
```

### Preservation Checking

**Goal**: verificar que para todos los inputs donde `isBugCondition(db)` es `false`
(registros no afectados), la migración no los modifica.

**Pseudocode:**
```
FOR ALL tx IN transactions WHERE tx.id NOT IN (6..27, 52..96, 87) DO
  tx_before := readTransaction(db, tx.id)
  applyMigration_11_12(db)
  tx_after  := readTransaction(db', tx.id)
  ASSERT tx_before = tx_after
END FOR

FOR ALL acc IN accounts WHERE acc.id != 2 DO
  acc_before := readAccount(db, acc.id)
  applyMigration_11_12(db)
  acc_after  := readAccount(db', acc.id)
  ASSERT acc_before = acc_after
END FOR
```

**Testing Approach**: property-based testing es recomendable para la preservación porque:
- Genera combinaciones de IDs de transacción aleatorios fuera del rango problemático.
- Confirma que la sentencia `DELETE WHERE id IN (...)` no afecta filas adyacentes.
- Proporciona garantía estadística sobre la selectividad de las sentencias SQL.

**Test Cases:**
1. **Preservación de transacción 34**: verificar que `amount_cents = 72_280_836` tras migración.
2. **Preservación de card_purchases**: verificar que el COUNT de `card_purchases` es el mismo
   antes y después de la migración.
3. **Preservación de account_id 1 y 3**: verificar que sus campos no cambian en la migración.
4. **Preservación de recurrentes**: verificar que `recurring_items` queda intacta.

### Unit Tests

- Abrir una BD Room en memoria (versión 11), insertar un subconjunto de las filas buggy,
  ejecutar `MIGRATION_11_12` y verificar los asserts de Fix Checking.
- Verificar que `realAvailableCents` emite el valor correcto con mocks de los cuatro Flows.
- Verificar que `realAvailableCents` puede ser negativo si las obligaciones superan el saldo.
- Verificar que el `AccountDao.getBalanceCents` retorna el saldo de account_id: 1 y no de otro.
- Verificar que `CreditCardDao.getPendingCycleCents` suma solo las compras con
  `is_fully_paid = false` del `card_id` solicitado.

### Property-Based Tests

- Generar IDs de transacción aleatorios fuera del rango [6–27, 52–96, 87] y verificar que
  la migración los deja intactos (preservación estadística de `transactions`).
- Generar saldos arbitrarios de Bancolombia y obligaciones; verificar que
  `realAvailableCents = bancolombia − bancoBogota − rappiCard − nuCard − recurring`
  para cualquier combinación de valores positivos y negativos.
- Verificar que `getPendingCycleCents` retorna 0 cuando todos los registros tienen
  `is_fully_paid = true`, para cualquier `card_id`.

### Integration Tests

- Test de migración completa con BD en memoria: partir de versión 11 con datos de prueba
  que incluyan los IDs problemáticos, ejecutar hasta versión 12, y verificar el estado final.
- Test de extremo a extremo del dashboard: tras la migración, verificar que el `ViewModel`
  emite `realAvailableCents < spendableBalanceCents` (las obligaciones reducen el disponible).
- Test de backup round-trip: exportar la BD limpia post-migración, importarla via
  `BackupManager.restoreBackup` en una BD limpia, y verificar que los IDs 6–27, 87 y 52–96
  no reaparecen.
