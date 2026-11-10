# Bugfix Requirements Document

## Introduction

La aplicación de finanzas personales presenta datos cruzados entre cuentas de débito y tarjetas de crédito, causando que el dashboard no refleje el estado financiero real del usuario. Los problemas abarcan cinco áreas: (1) 22 transacciones de la tarjeta de crédito Nu registradas incorrectamente como débitos de la cuenta Nu, (2) un pago a Nu duplicado que dobla el débito sobre Bancolombia, (3) 41 transacciones de la tarjeta Rappi registradas como débitos de la cuenta Rappi, (4) el cálculo de "disponible para gastar" en el dashboard que ignora obligaciones de tarjetas de crédito y deudas activas, y (5) el tipo de cuenta Rappi incorrecto (`CHECKING` en lugar de `SAVINGS`). En conjunto, estos bugs generan gastos inflados, saldos negativos implícitos y una lectura distorsionada del estado financiero.

## Bug Analysis

### Current Behavior (Defect)

1.1 WHEN una transacción tiene `account_id: 3` (cuenta débito Nu) Y el merchant/concepto corresponde a una compra que también existe en `card_purchases` con `card_id: 2` (tarjeta Nu) THEN el sistema registra el gasto dos veces: una como débito de la cuenta y otra como cargo de tarjeta

1.2 WHEN las transacciones con IDs 6–27 (22 registros) se incluyen en el cálculo de saldo de la cuenta débito Nu (account_id: 3) THEN el sistema muestra gastos inflados y un saldo implícitamente negativo para dicha cuenta

1.3 WHEN el pago a "Nu Compañía de Financiamiento" del 4 de noviembre de 2026 existe en `transactions` como transacción 34 y también como transacción 87, ambas con `account_id: 1` y amount: 72.280.836 centavos THEN el sistema debita Bancolombia dos veces por $722.808,36 resultando en un débito total de $1.445.616,72

1.4 WHEN una transacción tiene `account_id: 2` (cuenta débito Rappi) Y el merchant/concepto corresponde a una compra que también existe en `card_purchases` con `card_id: 1` (tarjeta Rappi) THEN el sistema registra el gasto dos veces: una como débito de la cuenta y otra como cargo de tarjeta

1.5 WHEN las transacciones con IDs 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 91, 92, 93, 94, 95, 96 (41 registros) se incluyen en el cálculo de saldo de la cuenta débito Rappi (account_id: 2) THEN el sistema muestra gastos inflados para dicha cuenta

1.6 WHEN el dashboard calcula "dinero disponible para gastar" THEN el sistema no descuenta las obligaciones de tarjetas de crédito pendientes (saldo no pagado de la tarjeta Rappi ni de la tarjeta Nu, incluyendo intereses diferidos)

1.7 WHEN el dashboard calcula "dinero disponible para gastar" THEN el sistema no descuenta la cuota mensual de la deuda con Banco de Bogotá ($35.831.600/mes, saldo total: $958.843.582)

1.8 WHEN el dashboard calcula "dinero disponible para gastar" THEN el sistema incluye el saldo de la cuenta Rappi (account_id: 2, `is_static_saving: true`) como dinero disponible

1.9 WHEN la cuenta Rappi (account_id: 2) se muestra en la UI THEN el sistema la representa con tipo `CHECKING` (cuenta corriente) aunque el usuario la usa como cuenta de ahorros estática (`is_static_saving: true`)

### Expected Behavior (Correct)

2.1 WHEN una transacción en `transactions` corresponde a un cargo de tarjeta de crédito (misma fecha, monto y comercio que un registro en `card_purchases`) THEN el sistema SHALL eliminar o reclasificar dicha transacción para que no debite la cuenta débito asociada, evitando la doble contabilidad

2.2 WHEN las transacciones con IDs 6–27 existen en el sistema THEN el sistema SHALL reclasificarlas como registros vinculados a `card_id: 2` sin debitar `account_id: 3`, de modo que el saldo de la cuenta débito Nu refleje únicamente transacciones de débito reales

2.3 WHEN el pago a "Nu Compañía de Financiamiento" del 4 de noviembre de 2026 está registrado dos veces (transacciones 34 y 87) THEN el sistema SHALL conservar únicamente la transacción 34 (detectada automáticamente por SMS) y eliminar la transacción 87 (entrada manual duplicada), resultando en un único débito de $722.808,36 sobre Bancolombia

2.4 WHEN las transacciones con IDs 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 91, 92, 93, 94, 95, 96 existen en el sistema THEN el sistema SHALL reclasificarlas como registros vinculados a `card_id: 1` sin debitar `account_id: 2`, de modo que el saldo de la cuenta débito Rappi refleje únicamente transacciones de débito reales

2.5 WHEN el dashboard calcula "dinero disponible para gastar" THEN el sistema SHALL descontar del saldo de Bancolombia: (a) la cuota mensual de Banco de Bogotá ($35.831.600), (b) el pago mínimo o saldo pendiente del ciclo actual de la tarjeta Rappi (card_id: 1), y (c) el pago mínimo o saldo pendiente del ciclo actual de la tarjeta Nu (card_id: 2, incluyendo intereses diferidos)

2.6 WHEN el dashboard calcula "dinero disponible para gastar" THEN el sistema SHALL excluir el saldo de la cuenta Rappi (account_id: 2) del monto disponible por ser `is_static_saving: true`

2.7 WHEN la cuenta Rappi (account_id: 2) se muestra en la UI THEN el sistema SHALL mostrar su tipo como `SAVINGS` para que el ícono y la etiqueta representen correctamente una cuenta de ahorros

### Unchanged Behavior (Regression Prevention)

3.1 WHEN una transacción tiene `account_id: 1` (Bancolombia) Y no es un duplicado identificado THEN el sistema SHALL CONTINUE TO registrarla como débito o crédito normal de Bancolombia sin modificación

3.2 WHEN se registra un nuevo pago de tarjeta de crédito detectado por SMS por primera vez (sin duplicado previo) THEN el sistema SHALL CONTINUE TO crear la transacción automáticamente y debitarla de la cuenta correspondiente

3.3 WHEN `card_purchases` contiene compras para `card_id: 1` (Rappi) o `card_id: 2` (Nu) THEN el sistema SHALL CONTINUE TO mantener todos esos registros intactos en `card_purchases` sin modificación

3.4 WHEN la cuenta Bancolombia (account_id: 1) tiene `include_in_total: true` THEN el sistema SHALL CONTINUE TO incluir su saldo en el total de activos disponibles del dashboard

3.5 WHEN la cuenta Rappi (account_id: 2) tiene `include_in_total: false` THEN el sistema SHALL CONTINUE TO excluir su saldo del total de activos líquidos mostrados en el dashboard

3.6 WHEN la cuenta Nu (account_id: 3) tiene saldo $0 y no hay transacciones de débito reales sobre ella THEN el sistema SHALL CONTINUE TO mostrar su saldo como $0

3.7 WHEN el dashboard muestra el resumen de deuda con Banco de Bogotá THEN el sistema SHALL CONTINUE TO mostrar el saldo total correcto ($958.843.582) y la cuota mensual ($35.831.600)

3.8 WHEN se agregan nuevas transacciones legítimas de débito a cuentas Rappi (account_id: 2) o Nu (account_id: 3) en el futuro THEN el sistema SHALL CONTINUE TO registrarlas correctamente como débitos de la cuenta correspondiente

3.9 WHEN la tarjeta Rappi (card_id: 1) tiene `account_id: 2` como cuenta de respaldo THEN el sistema SHALL CONTINUE TO mantener esa asociación para el flujo de pago de la tarjeta

3.10 WHEN la tarjeta Nu (card_id: 2) tiene `account_id: 3` como cuenta de respaldo THEN el sistema SHALL CONTINUE TO mantener esa asociación para el flujo de pago de la tarjeta
