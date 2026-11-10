# Bugfix Requirements Document

## Introduction

La aplicación de finanzas personales presenta dos defectos en la pantalla principal del Honor 400 Smart (~6.7" FHD+):

1. **Dashboard visual desbordado**: La `DashboardScreen` muestra textos truncados en el hero card, espaciado excesivo entre secciones, toggle Actual/Proyectado duplicado (aparece tanto en `CompactFinancialHeroCard` como en `CashFlowChartCard`) y tarjetas que no escalan correctamente al ancho real del dispositivo.

2. **FAB solo registra gastos**: El botón flotante "+" en `MainScaffold` abre exclusivamente `QuickExpenseSheet` (gastos). No existe ninguna ruta rápida para registrar un ingreso/depósito desde la pantalla principal, aunque la lógica de `IncomesViewModel.createIncome` ya existe y funciona.

---

## Bug Analysis

### Current Behavior (Defect)

1.1 CUANDO el usuario abre la pantalla de inicio ENTONCES los textos de la fila de tres métricas del `CompactFinancialHeroCard` (Libre hoy | columna central | Por día) se truncan o se salen del contenedor porque usan `maxLines = 1, softWrap = false` con `weight(1f)` sin restricción de ancho mínimo.

1.2 CUANDO el usuario abre la pantalla de inicio ENTONCES el `LazyColumn` aplica `padding(horizontal = 20.dp)` fijo y `verticalArrangement = Arrangement.spacedBy(14.dp)`, generando márgenes laterales apretados y demasiado espacio vertical muerto entre secciones en pantallas de 6.7".

1.3 CUANDO el usuario abre la pantalla de inicio ENTONCES el toggle Actual/Proyectado aparece duplicado: una vez dentro del `CompactFinancialHeroCard` (parte superior de la tarjeta) y otra vez dentro del `CashFlowChartCard`, mostrando el mismo control de estado en dos lugares diferentes de la misma pantalla.

1.4 CUANDO el usuario hace scroll hasta el final de la `DashboardScreen` ENTONCES hay un `Spacer(72.dp)` explícito justo antes del `contentPadding(bottom = 96.dp)` del `LazyColumn`, acumulando ~168 dp de espacio vacío redundante al final del scroll.

1.5 CUANDO el usuario toca el botón flotante "+" ENTONCES el sistema abre únicamente `QuickExpenseSheet`, que solo permite registrar gastos (débito de cuenta). No existe ningún control ni opción para registrar un ingreso/depósito directo a una cuenta.

1.6 CUANDO el usuario quiere registrar un ingreso rápido ENTONCES el sistema obliga a navegar manualmente a la pestaña de Ingresos (pestaña 5), que es la única pantalla donde `IncomesViewModel.createIncome` es accesible desde la UI.

---

### Expected Behavior (Correct)

2.1 CUANDO el usuario abre la pantalla de inicio ENTONCES el sistema SHALL mostrar la fila de tres métricas del hero card sin truncamiento, ajustando el tamaño de fuente o usando `overflow = TextOverflow.Ellipsis` en lugar de `softWrap = false`, de forma que todas las cifras sean legibles en pantallas de 6.7".

2.2 CUANDO el usuario abre la pantalla de inicio ENTONCES el sistema SHALL aplicar padding horizontal adaptativo (usando `WindowInsets` o un valor reducido como 12.dp-16.dp) y un `verticalArrangement` reducido (8-10.dp) para que el contenido respire sin espacio muerto excesivo.

2.3 CUANDO el usuario abre la pantalla de inicio ENTONCES el sistema SHALL mostrar el toggle Actual/Proyectado una sola vez (en el `CompactFinancialHeroCard`), y el `CashFlowChartCard` SHALL recibir el estado `isProjectedView` como parámetro sin renderizar su propio toggle, eliminando la duplicación visual.

2.4 CUANDO el usuario hace scroll hasta el final de la `DashboardScreen` ENTONCES el sistema SHALL tener un único espacio inferior gestionado por `contentPadding(bottom = 96.dp)` del `LazyColumn`, sin el `Spacer(72.dp)` redundante adicional.

2.5 CUANDO el usuario toca el botón flotante "+" ENTONCES el sistema SHALL presentar una selección entre "Registrar Gasto" y "Registrar Ingreso" (mediante un menú, bottom sheet selector o dos FABs expandibles), permitiendo iniciar cualquiera de los dos flujos desde la pantalla principal.

2.6 CUANDO el usuario elige "Registrar Ingreso" desde el FAB ENTONCES el sistema SHALL abrir un `QuickIncomeSheet` que permita ingresar monto, seleccionar cuenta destino y una descripción, invocando `IncomesViewModel.createIncome` para persistir el ingreso sin navegar a la pestaña de Ingresos.

---

### Unchanged Behavior (Regression Prevention)

3.1 CUANDO el usuario abre la pantalla de inicio en un dispositivo distinto al Honor 400 Smart ENTONCES el sistema SHALL CONTINUE TO renderizar el dashboard con el mismo diseño visual, sin romper layouts en tamaños de pantalla menores o mayores.

3.2 CUANDO el usuario toca el botón "+" y selecciona "Registrar Gasto" ENTONCES el sistema SHALL CONTINUE TO abrir `QuickExpenseSheet` con su comportamiento actual (teclado numérico, selector de cuenta, selector de categoría y nota opcional).

3.3 CUANDO el usuario cambia el toggle Actual/Proyectado en el `CompactFinancialHeroCard` ENTONCES el sistema SHALL CONTINUE TO actualizar el `CashFlowChartCard` para mostrar los datos de proyección o actuales correspondientes al estado seleccionado.

3.4 CUANDO el usuario navega a la pestaña de Ingresos (pestaña 5) ENTONCES el sistema SHALL CONTINUE TO mostrar la `IncomesScreen` completa con toda su funcionalidad de programación de ingresos recurrentes, confirmación y eliminación.

3.5 CUANDO el usuario registra un ingreso rápido desde el nuevo `QuickIncomeSheet` ENTONCES el sistema SHALL CONTINUE TO reflejar el cambio de saldo en las cuentas y en los cálculos del dashboard de forma consistente con el comportamiento existente de `IncomesViewModel.createIncome`.

3.6 CUANDO el usuario hace scroll en el `RecurringItemsSection` o en el `AllocationEnvelopesSection` ENTONCES el sistema SHALL CONTINUE TO permitir scroll fluido a través del `LazyColumn` contenedor, sin bloqueos ni comportamientos inesperados en la navegación vertical.

---

## Condiciones de Bug y Propiedades de Corrección

### Bug 1 — Dashboard visual

**Bug Condition C₁(X):**
```pascal
FUNCTION isBugCondition1(X)
  INPUT: X of type DashboardRenderContext
  OUTPUT: boolean

  RETURN X.screenWidthDp <= 420
     AND X.heroCardMetricsRow.uses_softWrap_false = true
     AND X.lazyColumnHorizontalPadding = 20.dp
     AND X.cashFlowCard.hasOwnToggle = true
END FUNCTION
```

**Fix Checking Property:**
```pascal
// Property: Dashboard métricas legibles y sin toggle duplicado
FOR ALL X WHERE isBugCondition1(X) DO
  result ← renderDashboard'(X)
  ASSERT result.heroMetrics.noTextClipped = true
     AND result.toggleCount = 1
     AND result.bottomScrollSpace <= 100.dp
END FOR
```

**Preservation:**
```pascal
FOR ALL X WHERE NOT isBugCondition1(X) DO
  ASSERT renderDashboard(X) = renderDashboard'(X)
END FOR
```

---

### Bug 2 — FAB solo gastos

**Bug Condition C₂(X):**
```pascal
FUNCTION isBugCondition2(X)
  INPUT: X of type FabTapEvent
  OUTPUT: boolean

  RETURN X.fabTapped = true
     AND X.availableActions = { "expense_only" }
END FUNCTION
```

**Fix Checking Property:**
```pascal
// Property: FAB ofrece opción de ingreso
FOR ALL X WHERE isBugCondition2(X) DO
  result ← handleFabTap'(X)
  ASSERT "register_income" IN result.availableActions
     AND "register_expense" IN result.availableActions
END FOR
```

**Preservation:**
```pascal
FOR ALL X WHERE NOT isBugCondition2(X) DO
  ASSERT handleFabTap(X) = handleFabTap'(X)
END FOR
```
