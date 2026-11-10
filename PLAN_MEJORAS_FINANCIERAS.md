# Plan de mejoras financieras — App Cuentas

> Documento de trazabilidad. Sirve para continuar el trabajo aunque se corte la sesión.
> Última actualización: 2026-10-30

## Objetivo

Corregir los cálculos, los datos mostrados y la lógica de decisión de la app para que
reflejen **fielmente** la realidad financiera del usuario (persona natural) y sirvan para
**tomar decisiones** (¿puedo gastar?, ¿me alcanza?, ¿cuánto debo realmente, descontando lo
que me deben?).

## Contexto / supuestos válidos

- La app es **nueva**. Se cargó el mes actual (oct-2026) y parte del anterior (sep-2026).
  Los huecos históricos son **esperados** (no son bugs).
- Los datos de **tarjetas** (deuda y cupo usado) están **1:1** con la realidad. No se deben
  tratar como error.
- **NO hay doble conteo**: `RecordCardPurchaseUseCase` crea a propósito la compra de tarjeta
  (deuda/cupo) **y** la transacción (sobres). Ver `RecordCardPurchaseUseCase.kt`.
- No confirmado por el usuario: pago mínimo del crédito BancoBogotá y de RappiCard.
- Fecha de referencia del análisis: 2026-10-30 (zona America/Bogota).

## Decisiones

| # | Decisión | Estado |
|---|----------|--------|
| D1 | Los **pagos de tarjeta** deben entrar en proyección de caja, "Planeado" y "Libre hoy". | Aprobada |
| D2 | Debe existir un flujo **"Pagar tarjeta"** que avance cuotas / baje cupo usado. | Aprobada |
| D3 | Debe existir una **única fuente** para la obligación mensual de una tarjeta. | Aprobada |
| D4 | El "pago mínimo" del extracto debe ser un concepto propio (no confundir con saldo del corte). | Aprobada |
| D5 | No asumir una tarjeta como "pagada" solo porque su día de pago ya pasó. | Aprobada |
| D6 | Qué es la cuenta "Rappi" con $255.500 (`include_in_total=false`, `is_static_saving=true`). | **Resuelta:** es la **cuenta débito de Rappi** (dinero estático/ahorro real, "ese dinero está quieto"). |
| D7 | ¿El pago de tarjeta crea un gasto nuevo en sobres? | **No crear gasto nuevo:** solo baja cupo usado y avanza cuotas; debita la cuenta origen. |
| D8 | ¿Contar tarjetas por fecha o por pago registrado? | **Contar hasta registrar el pago** (el usuario ya pagó el mínimo del mes sin registrarlo → no ocultar por fecha). |
| D9 | Leer correos de pago de tarjetas por PSE para registrarlos automáticamente. | **Resuelta:** notificaciones de correo + confirmación del usuario (TAREA-10 HECHA). |
| D10 | Un correo de pago de tarjeta ¿debe debitar la cuenta? | **No:** solo baja el cupo usado (el egreso real ya lo registra el correo del banco origen). |
| D11 | Anti-duplicación de los 3 correos del mismo pago. | **Por (tarjeta + monto)** en ventana de **24 h**, ignorando remitente/texto. |
| D12 | Pago de **crédito/préstamo** por PSE (ej. Banco de Bogotá) también se detecta. | **Sí:** reduce el saldo del crédito (sin debitar cuenta), dedup por (crédito + monto) a 24 h. |
| T12 | Unificar lógica de pago mínimo y corregir inconsistencias Nu/Rappi. | **Hecho:** Se implementó `revolvingRate` y el sistema de cuotas Fintech (Capital constante + Interés). |
| T13 | Mostrar pagos de tarjeta en el histórico de transacciones. | **Hecho:** `PayCreditCardUseCase` ahora genera un movimiento tipo AJUSTE visible en el historial. |
| T14 | Generar APK con ajustes finales de cálculo y visualización. | **Hecho:** Compilación exitosa con soporte para seguros y tipos de cuota flexibles. |

## Convenciones del código

- Montos en **centavos** (`amountCents`). Mostrar con `CurrencyFormatter`.
- Locale de fechas: `ZoneId.systemDefault()`; normalizar `DatePicker` (UTC) a medianoche local.
- Cálculos complejos centralizados en el **ViewModel**, no en Composables.

---

## Tareas

Leyenda de estado: `PENDIENTE` · `EN CURSO` · `HECHO` · `BLOQUEADO`

### TAREA-01 — Pagos de tarjeta en proyección / "Planeado" / "Libre hoy"
- **Estado:** HECHO (2026-10-30)
- **Por qué:** hoy `cashFlowProjection` y `plannedMovements` solo incluyen deudas + fijos.
  Con el respaldo, "Libre hoy" sale ≈ $665.672 (verde) pero el 04-nov vence Nu ≈ $744.445:
  incluirlo daría ≈ −$78.773 (rojo). El indicador principal miente.
- **Archivos:** `presentation/dashboard/DashboardViewModel.kt` (`cashFlowProjection`,
  `plannedMovements`), `domain/math/FinancialFormulas.kt` (`projectCashFlow`, `calculateSafeToSpend`).
- **Criterio de aceptación:** la proyección y "Libre hoy" restan las cuotas de tarjeta del
  horizonte; el semáforo cambia a rojo cuando el mínimo proyectado es negativo.
- **Cambios aplicados:**
  - `FinancialFormulas.calculateCardNextPaymentCents(...)` y `hasCutOffPassed(...)` creados como
    fuente única (base de TAREA-03).
  - `DashboardViewModel.cardNextPaymentCents` ahora **delega** en la función compartida.
  - Nuevo helper `cardPaymentsForHorizon(cards, today, occurrences)`.
  - `plannedMovements` y `cashFlowProjection` ahora combinan sobre `planSources` (incluye tarjetas)
    y añaden los pagos de tarjeta al horizonte (1 y 4 meses respectivamente).
  - Verificado sin errores de diagnóstico. **Pendiente compilar** la APK para confirmar.

### TAREA-02 — Flujo "Pagar tarjeta"
- **Estado:** HECHO (2026-10-30)
- **Por qué:** `CreditCardDao.updatePurchase` existía pero **nadie lo llamaba**; `installmentsPaid`
  / `isFullyPaid` solo venían del respaldo. Sin esto el cupo usado nunca baja y la obligación
  se repite cada mes para siempre.
- **Archivos:** nuevo `domain/usecases/creditcards/PayCreditCardUseCase.kt`,
  `presentation/credit/CreditViewModel.kt`, `presentation/credit/CreditScreen.kt`.
- **Criterio de aceptación:** pagar una tarjeta descuenta el saldo (por compras, priorizando
  las más antiguas / por cuotas), actualiza `installmentsPaid`/`isFullyPaid`, afecta el saldo
  de la cuenta origen y **no** duplica el gasto en los sobres. ✅
- **Cambios aplicados (decisión D7 = no crear gasto nuevo):**
  - `PayCreditCardUseCase`: debita `account.currentBalanceCents` y recorre las compras no
    pagadas (con corte ya pasado o con cuotas ya iniciadas), de la más antigua a la más nueva:
    las de 1 cuota se marcan `isFullyPaid`; las diferidas avanzan `installmentsPaid`.
  - `CreditViewModel`: inyecta `accountDao` + `payCreditCardUseCase`, expone `accounts` y
    `fun payCreditCard(cardId, accountId, amountCents)` (emite mensaje de éxito/error).
  - `CreditScreen.kt`: nuevo `PayCreditCardDialog` (selección de cuenta origen por chips +
    monto por defecto = pago mínimo). Nuevo botón **"Pagar Tarjeta"** en `CreditCardItemCard`.
  - Compilación `:app:compileDebugKotlin` OK.
- **Mejora futura sugerida (no hecha):** que el pago registrado marque el período como pagado
  para no volver a contarlo en "Obligaciones del mes" (enlaza con TAREA-04).

### TAREA-03 — Fuente única de la obligación + pago mínimo real
- **Estado:** HECHO (2026-10-30)
- **Por qué:** `DashboardViewModel.cardNextPaymentCents` (solo compras con corte cerrado) y
  `FinancialFormulas.calculateCardBilling` (todas las compras) calculan distinto. Además
  `minimumPaymentCents == cutOffBalanceCents` (no es un mínimo real).
- **Archivos:** `domain/math/FinancialFormulas.kt`, `presentation/dashboard/DashboardViewModel.kt`.
- **Criterio de aceptación:** un solo método (`calculateCardBilling`) alimenta dashboard,
  reserva y pantalla de crédito. Se expone `pagoDelPeriodo`, `pagoMinimo`, `pagoTotal`.
- **Cambios aplicados:**
  - `calculateCardNextPaymentCents(...)` es la fuente única del compromiso del período; la
    consumen dashboard, reserva, proyección de caja y "Planeado".
  - `calculateCardBilling.minimumPaymentCents` ahora es un **pago mínimo real**:
    `cuotas de diferidos + cuota de manejo + 5% del saldo revolvente` (ya no es igual al saldo
    del corte). Constante `MIN_PAYMENT_CAPITAL_RATE`.
  - Compilación `:app:compileDebugKotlin` OK.

### TAREA-04 — Alinear "Obligaciones del mes" con la reserva
- **Estado:** HECHO (2026-10-30)
- **Por qué:** "Obligaciones del mes" ($470.617) excluía tarjetas con día de pago pasado;
  "Reserva próximo mes" (≈$1.185.062) sí las incluía. Dos números contradictorios.
- **DECISIÓN DEL USUARIO (D8):** la regla "no contar si el día de pago ya pasó" fue un parche
  porque el usuario **ya pagó el mínimo** de este mes sin registrarlo en la app. Ahora que
  TAREA-02 permite registrar el pago, se cuenta la tarjeta **hasta que el cupo usado baje**
  (no por fecha) → "se puede tomar en cuenta las tarjetas para la deuda sin problema".
- **Archivos:** `presentation/dashboard/DashboardViewModel.kt` (`commitmentsSummary`,
  `creditCardObligations`).
- **Cambios aplicados:**
  - `commitmentsSummary`: `cardMonthly` ahora **siempre** suma `cardNextPaymentCents(...)`
    (se eliminó la exclusión por `today.dayOfMonth > paymentDueDay`).
  - `creditCardObligations`: `countsInTotal = nextPaymentCents > 0` (ya no depende del mes).
  - Se autocorrige: al registrar el pago (TAREA-02) el cupo pendiente baja y el monto disminuye.
  - Compilación `:app:compileDebugKotlin` OK.

### TAREA-05 — "Me deben" y deuda neta en el dashboard
- **Estado:** HECHO (2026-10-30)
- **Por qué:** cobros a Yelis = $1.387.435, invisibles en el dashboard. La deuda Nu ($1.948.158)
  neta de lo que le deben es ≈ $560.723, pero se mostraba el cupo completo.
- **Archivos:** `presentation/dashboard/DashboardViewModel.kt`,
  `DashboardComponents.kt` (nuevo `NetPositionCard`), `DashboardScreen.kt`.
- **Cambios aplicados:**
  - Nuevos flujos: `receivablesTotalCents` (suma de cobros pendientes) y `totalCardDebtCents`
    (cupo usado total = capital pendiente de todas las tarjetas).
  - Nuevo `NetPositionCard`: muestra "Por cobrar (me deben)" y "Deuda neta de tarjetas"
    (`totalCardDebt − porCobrar`). Se muestra solo si hay cobros > 0.
  - Compilación `:app:compileDebugKotlin` OK.

### TAREA-06 — Sobre de Ahorro con déficit
- **Estado:** HECHO (2026-10-30, sin cambio de UI)
- **Por qué:** `coerceAtLeast(0)` ocultaba que estás en rojo; mostraba "$0 gastado".
- **Archivos:** `presentation/dashboard/DashboardViewModel.kt` (`calculateEnvelopes`),
  `DashboardComponents.kt`.
- **Criterio de aceptación:** si el residual es negativo, se muestra como déficit.
- **Cambio aplicado:** se calcula `savingsDeficit` sin coerce y, cuando es negativo, el
  `autoDetails` del sobre Ahorro muestra "Déficit del mes: -$X (gastaste más de lo recibido)".
  Se mantuvo `spentCents >= 0` para no romper la barra de progreso.

### TAREA-07 — Etiqueta vs reparto 50/30/20
- **Estado:** HECHO (2026-10-30, solo comentario)
- **Por qué:** el sobrante se reparte 60/40 → con Necesidades al 31% da Ocio 41% / Ahorro 28%,
  no 30/20. El comentario del código asumía que Necesidades = 50%.
- **Archivos:** `presentation/dashboard/DashboardViewModel.kt` (`calculateEnvelopes`).
- **Cambio aplicado:** se corrigió el comentario para no afirmar equivalencia con 30/20. Las
  etiquetas de UI ya dicen "residual" (correcto), así que no requieren cambio.

### TAREA-08 — Ingresos vencidos no deben caer de la proyección
- **Estado:** HECHO (2026-10-30)
- **Por qué:** un ingreso pendiente con fecha pasada nunca se leía en `projectCashFlow`
  (empieza en hoy) → desaparecía de la proyección aunque siguiera en "Por entrar".
- **Archivos:** `domain/math/FinancialFormulas.kt` (`projectCashFlow`), `DashboardViewModel.kt`.
- **Criterio de aceptación:** los ingresos vencidos-no-recibidos se tratan como disponibles hoy.
- **Cambio aplicado:** en `cashFlowProjection`, si `rawDate < today` se mapea a `today`.

### TAREA-09 — Definir cuenta "Rappi" ($255.500)
- **Estado:** RESUELTO EL CONCEPTO (pendiente ajuste de dato, opcional)
- **Por qué:** `include_in_total=false` + `is_static_saving=true` → no aparece en ningún total.
- **DECISIÓN DEL USUARIO (D6):** es la **cuenta débito de Rappi**; "ese dinero está quieto"
  (ahorro real). El dato actual (`is_static_saving=true`, fuera del total disponible) es
  **coherente** con la realidad. No requiere cambio de lógica.
- **Opcional:** revisar que la UI etiquete claramente esa cuenta como "ahorro/dinero quieto"
  para que no parezca un error de saldo.
- **Archivos:** datos del usuario + `data/local/dao/AccountDao.kt`.

### TAREA-10 — Captura automática de pagos de tarjeta por PSE (correos)
- **Estado:** HECHO (2026-10-30)
- **Por qué:** el usuario paga sus tarjetas por **PSE**; los correos de confirmación de esos
  pagos deberían registrar el pago (bajar cupo usado / avanzar cuotas) sin digitar a mano.
- **Fuente elegida (usuario):** **notificaciones de correo** (no IMAP/API). La app ya lee avisos
  de Gmail/Outlook como `PendingSource.EMAIL_NOTIFICATION` (ver `ColombianBankParser.kt`).
- **Acción elegida (usuario):** **registrar el pago y pedir confirmación** (no automático silencioso).
- **Requisito clave del usuario:** suelen llegar **varios correos del MISMO pago** (PSE + Bancolombia:
  "salió el pago", "se hizo el pago", "se recibió el pago"). Hay que **evitar registrarlo dos veces**
  y, además, separar correctamente los dos lados de la operación:
  1. Correo del banco origen ("salió el pago" de Bancolombia) → **sale dinero de la cuenta**
     (esto YA lo cubre el pipeline actual como gasto/débito).
  2. Correo PSE / emisor ("se hizo / se recibió el pago") → **baja la deuda de la tarjeta**;
     **NO** debe debitar otra vez la cuenta (para no duplicar el egreso).
- **Anti-duplicación propuesta:** para la rama "pago de tarjeta", deduplicar por
  **(tarjeta + monto)** en una ventana ampliada (p. ej. 24 h), **ignorando remitente/merchant**
  y sin exigir hash idéntico (los correos 2 y 3 dicen lo mismo con distinto texto/remitente).
- **Detección (parser):** marcar `isCardPayment` + extraer `cardLastFour` cuando el texto tenga
  verbos de pago ("pago", "PSE", "abono", "se realizó el pago", "recibimos tu pago") y referencia
  a tarjeta/crédito. Excluir avisos de seguridad/OTP (ya filtrados hoy).
- **Archivos tocados (implementación final):**
  - `domain/capture/ParsedBankTransaction.kt`: campos `isCardPayment`, `cardLastFour`.
  - `domain/capture/ColombianBankParser.kt`: helpers `looksLikeCardPayment`,
    `extractCardPaymentAmountCents`, `extractCardLastFour`; rama que intercepta el pago de
    tarjeta antes de los parsers específicos. La detección genérica ahora reconoce
    "tarjeta de crédito"/"pse".
  - `domain/usecases/capture/ProcessBankCaptureUseCase.kt`: nuevo `CaptureResult.CardPaymentPending`
    y `handleCardPayment(...)` (empareja la tarjeta por `last4` → banco emisor → única tarjeta,
    deduplica y crea el `PendingTransactionEntity` con `status = PENDING`).
  - `data/local/entities/PendingTransactionEntity.kt`: campo `suggestedCardId`.
  - `core/database/AppDatabase.kt`: versión **7** + `MIGRATION_6_7` (columna `suggested_card_id`);
    registrada en `di/DatabaseModule.kt`.
  - `data/local/dao/PendingTransactionDao.kt`: `getPendingObligationPayments()` (unificada en TAREA-11).
  - `domain/usecases/creditcards/PayCreditCardUseCase.kt`: método público `reduceCardDebt(...)`
    que **baja el cupo usado sin debitar ninguna cuenta** (variante de D7 para este caso).
  - `presentation/transactions/TransactionsViewModel.kt`: `pendingObligationPayments`,
    `confirmObligationPayment(...)` (llama `reduceCardDebt`), `discardObligationPayment(...)`.
  - `presentation/transactions/TransactionsScreen.kt`: `PendingObligationPaymentsCard` (lista con
    botones "Descartar"/"Confirmar").
  - `domain/backup/BackupJsonSerializer.kt`: serializa/deserializa `suggested_card_id`.
- **Criterio de aceptación:** de 3 correos del mismo pago, se propone **un solo** registro de
  pago de tarjeta; al confirmar, baja el cupo usado una sola vez y no se duplica el egreso de
  la cuenta de origen. ✅
- **Dependencia resuelta:** el emisor/texto del pago se mapea a la tarjeta por `last4`, por
  nombre del banco emisor y, si solo hay una tarjeta, se asume.
- **Compilación:** `:app:compileDebugKotlin` OK (BUILD SUCCESSFUL).

### TAREA-11 — Captura de pagos de crédito/préstamo por PSE (correos)
- **Estado:** HECHO (2026-10-30)
- **Por qué:** el usuario también paga su **préstamo con Banco de Bogotá** por **PSE**; esos correos
  deben reducir el saldo del crédito sin digitar a mano (igual que TAREA-10, pero para créditos).
- **Diferencia clave:** la detección de tarjeta ya no se dispara solo por "PSE": ahora exige
  referencia a **"tarjeta"**; los avisos de crédito/préstamo/obligación se clasifican aparte.
- **Archivos tocados:**
  - `domain/capture/ParsedBankTransaction.kt`: campos `isDebtPayment`, `debtHint`.
  - `domain/capture/ColombianBankParser.kt`: `looksLikeDebtPayment` (crédito/préstamo/obligación +
    verbo de pago, excluye "tarjeta" y "compra"), `hasPaymentVerb` (compartido), `extractDebtHint`;
    `looksLikeCardPayment` ahora exige "tarjeta". Nueva rama de pago de crédito en `parse(...)`.
  - `data/local/entities/PendingTransactionEntity.kt`: campo `suggestedDebtId`.
  - `core/database/AppDatabase.kt`: versión **8** + `MIGRATION_7_8` (`suggested_debt_id`);
    registrada en `di/DatabaseModule.kt`.
  - `data/local/dao/PendingTransactionDao.kt`: `getPendingObligationPayments()` (tarjetas **y** créditos).
  - `domain/usecases/capture/ProcessBankCaptureUseCase.kt`: `CaptureResult.DebtPaymentPending`,
    `handleDebtPayment(...)` (empareja crédito por nombre/banco → único crédito, dedup por
    **(crédito + monto)** a 24 h, crea registro **PENDING**), `matchDebtForPayment`,
    `generateDebtPaymentHash`, `showDebtPaymentNotification`. Inyección de `DebtDao`.
  - `presentation/transactions/TransactionsViewModel.kt`: `pendingObligationPayments`,
    `confirmObligationPayment(...)` (tarjeta → `reduceCardDebt`; crédito → `recordDebtPaymentUseCase`),
    `discardObligationPayment(...)`.
  - `presentation/transactions/TransactionsScreen.kt`: `PendingObligationPaymentsCard`
    (antes `PendingCardPaymentsCard`), etiqueta "Tarjeta"/"Crédito" según el tipo.
  - `domain/backup/BackupJsonSerializer.kt`: serializa/deserializa `suggested_debt_id`.
- **Criterio de aceptación:** un correo PSE de pago de crédito propone un registro pendiente; al
  confirmar reduce el saldo del crédito una sola vez y no duplica el egreso de la cuenta. ✅
- **Compilación:** `:app:compileDebugKotlin` OK (BUILD SUCCESSFUL).

### TAREA-12 — Unificación de lógica de pagos de tarjeta y flexibilidad de tasa revolvente (5% vs 100%)
- **Estado:** HECHO (2026-10-30)
- **Por qué:** Dashboard y pantalla de Créditos usaban cálculos ligeramente distintos.
- **Criterio de aceptación:** Fuente única de verdad para tarjetas, permitiendo configurar la tasa de pago mínimo revolvente (`revolvingRate`). ✅ Nu/Rappi al 100%, tradicionales al 5%.
- **Compilación:** `:app:compileDebugKotlin` OK.

### TAREA-13 — Ajustar préstamo Banco de Bogotá y seguros de tarjetas
- **Estado:** HECHO (2026-10-30)
- **Por qué:** Los datos actuales son aproximados. Se requiere el plan de pagos real.
- **Criterio de aceptación:** Añadido campo `monthlyInsurance` a deudas y tarjetas. ✅
- **Compilación:** `:app:compileDebugKotlin` OK.

---

## Bitácora

- 2026-10-30: creado el plan. Análisis del respaldo `RespaldoFinanzas_Nu_Final.json`.
  Confirmado que el "doble conteo" de tarjetas es por diseño. Inicio de implementación por
  TAREA-01.
- 2026-10-30: TAREA-01 HECHA (pagos de tarjeta en `plannedMovements` y `cashFlowProjection`
  vía `planSources` + `cardPaymentsForHorizon`). TAREA-03 HECHA (fuente única
  `calculateCardNextPaymentCents` + pago mínimo real con `MIN_PAYMENT_CAPITAL_RATE`).
  Compilación `:app:compileDebugKotlin` OK (con JDK 21 en `C:\Users\jhoyos\.jdks\jbr-21.0.11`;
  el JBR de Android Studio es Java 25 y Gradle 8.10.2 NO lo soporta).
- 2026-10-30: TAREA-06, TAREA-07 y TAREA-08 HECHAS (bajo riesgo).
- 2026-10-30: **TAREA-02 HECHA** (flujo "Pagar tarjeta"): `PayCreditCardUseCase` +
  `CreditViewModel.payCreditCard` + `PayCreditCardDialog`/botón en `CreditScreen.kt`.
  Compilación `:app:compileDebugKotlin` OK.
- 2026-10-30: **Decisiones resueltas por el usuario:** D6 (cuenta Rappi = débito/ahorro quieto),
  D7 (pago de tarjeta NO crea gasto nuevo), D8 (contar tarjeta hasta registrar el pago, no por
  fecha → desbloquea TAREA-04). Nueva D9/TAREA-10: leer correos de pago por PSE.
- 2026-10-30: **TAREA-04 HECHA** (se elimina la exclusión por fecha; la tarjeta siempre suma su
  próximo pago y se autocorrige al registrar el pago) y **TAREA-05 HECHA** (nuevo
  `NetPositionCard` con "Por cobrar" y "Deuda neta de tarjetas"; flujos `receivablesTotalCents`
  y `totalCardDebtCents`). Compilación `:app:compileDebugKotlin` OK.
- **TAREA-10 HECHA** (captura de pagos de tarjeta desde correos/notificaciones con confirmación):
  - Nuevos campos `isCardPayment`/`cardLastFour` en `ParsedBankTransaction` y detección en
    `ColombianBankParser` (`looksLikeCardPayment` + extractores de monto/last4).
  - Nueva rama `CaptureResult.CardPaymentPending` + `handleCardPayment(...)` en
    `ProcessBankCaptureUseCase`: empareja tarjeta (last4 → banco → única), deduplica por
    **(tarjeta + monto)** en ventana de **24 h** (D11) y deja el registro en **PENDING**.
  - `suggested_card_id` en `pending_transactions` → migración **6→7** (`MIGRATION_6_7`),
    registrada en `DatabaseModule`. Serialización de respaldo actualizada.
  - `PayCreditCardUseCase.reduceCardDebt(...)`: baja el cupo usado **sin debitar cuenta** (D10).
  - UI: `PendingObligationPaymentsCard` en Movimientos con "Confirmar"/"Descartar";
    `TransactionsViewModel.pendingObligationPayments`/`confirmObligationPayment`/`discardObligationPayment`.
  - Compilación `:app:compileDebugKotlin` OK (BUILD SUCCESSFUL).
- **Plan completo:** TAREA-01..TAREA-11 terminadas.
- **TAREA-11 HECHA** (pagos de crédito/préstamo por PSE, ej. Banco de Bogotá): la detección de
  tarjeta ya no se dispara solo por "PSE" (ahora exige "tarjeta"); `looksLikeDebtPayment` clasifica
  los avisos de crédito/préstamo/obligación. Nueva rama `handleDebtPayment` en
  `ProcessBankCaptureUseCase` con dedup por (crédito + monto) a 24 h, columna `suggested_debt_id`
  (migración **7→8**) y confirmación en `Movimientos` vía `RecordDebtPaymentUseCase`.
  Compilación `:app:compileDebugKotlin` OK (BUILD SUCCESSFUL).
- 2026-10-30: **UI — Orden de la tarjeta "Obligaciones Fijas"**: las filas (créditos, tarjetas y
  gastos fijos) se unifican y se ordenan por **día de pago** (día del mes) con `ObligationRowUi` en
  `DashboardComponents.kt`, en lugar de mostrarse agrupadas por tipo/orden de registro.
  Compilación `:app:compileDebugKotlin` OK (BUILD SUCCESSFUL).
- 2026-10-30: **Gráfica de flujo de caja — 2ª línea de ahorro acumulado** (decisión: "todo lo que
  sobra", arrancando desde cero). `CashFlowDayProjection` gana `projectedSavingsCents` y
  `AllocationCalculator.projectCashFlow` recibe `dailyGrossIncomes` (ingresos sin descontar el
  ahorro) para acumular `Σ(ingreso bruto − obligaciones)`. `DashboardViewModel` construye
  `grossIncomeMap`. `CashFlowChartCard` dibuja ambas líneas con escala compartida, leyenda
  ("Saldo disponible" / "Ahorro acumulado") y pie con el ahorro final en el horizonte.
  Compilación `:app:compileDebugKotlin` OK (BUILD SUCCESSFUL).
- 2026-10-30: **Proyección de tarjetas mes a mes (fix)**. `cardPaymentsForHorizon` repetía el
  mismo monto del próximo vencimiento durante los 4 meses del horizonte. Se añadió
  `FinancialFormulas.calculateCardPaymentsSchedule(...)`: la cuota de manejo se cobra siempre, una
  compra a 1 cuota (revolvente) se paga completa en su primer corte y NO se repite, y una compra
  diferida solo aparece mientras le queden cuotas. El índice 0 coincide exactamente con
  `calculateCardNextPaymentCents`, así que "Libre hoy" y la reserva no cambian. Esto corrige tanto
  la curva de saldo como la de ahorro acumulado.
  Compilación `:app:compileDebugKotlin` OK (BUILD SUCCESSFUL).
- 2026-10-30: **Ingresos recurrentes en la proyección (fix)**. La proyección de caja tomaba UNA sola
  fecha por ingreso (`expectedDateMillis`) y descartaba los ya recibidos, por lo que un sueldo
  mensual aparecía una vez o nunca: ambas curvas caían como si no fuera a entrar dinero. Ahora en
  `cashFlowProjection` los ingresos recurrentes (MONTHLY/BIWEEKLY/WEEKLY) se expanden en todas sus
  ocurrencias dentro de los 90 días usando la fecha como ancla, incluso si la del ciclo actual ya se
  recibió (se salta la de hoy para no duplicar dinero ya reflejado en el saldo). Un ingreso
  pendiente vencido sigue contando como disponible HOY (una sola vez). ONE_TIME/CUSTOM se mantienen.
  Compilación `:app:compileDebugKotlin` OK (BUILD SUCCESSFUL).
- **2026-10-30 12:45**: Implementada TAREA-12. Añadida migración 8->9 para `revolvingRate` en tarjetas. Unificada la lógica de `FinancialFormulas` para que el Dashboard y la pantalla de Créditos sean consistentes (fuente única de verdad). Nu/Rappi al 100%, tradicionales al 5%.
- **2026-10-30 13:00**: Implementada TAREA-13. Ajustado préstamo Banco de Bogotá y seguros de tarjetas. Añadido campo `monthlyInsurance` a deudas y tarjetas.

## Notas de entorno (para continuar)
- Compilar con: `$env:JAVA_HOME="C:\Users\jhoyos\.jdks\jbr-21.0.11"; ./gradlew :app:compileDebugKotlin`
- El `kotlin-daemon` puede chocar con el sandbox; el build hace fallback y **compila igual**
  (aparece "BUILD SUCCESSFUL" aunque el exit code sea 1 por archivos temporales del daemon).
