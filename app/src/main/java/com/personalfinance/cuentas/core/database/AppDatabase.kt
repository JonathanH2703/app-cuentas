package com.personalfinance.cuentas.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.personalfinance.cuentas.data.local.dao.AccountDao
import com.personalfinance.cuentas.data.local.dao.AllocationDao
import com.personalfinance.cuentas.data.local.dao.CategoryDao
import com.personalfinance.cuentas.data.local.dao.IncomeDao
import com.personalfinance.cuentas.data.local.dao.RecurringItemDao
import com.personalfinance.cuentas.data.local.dao.TagDao
import com.personalfinance.cuentas.data.local.dao.TransactionDao
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.AllocationBlockEntity
import com.personalfinance.cuentas.data.local.entities.AllocationModelEntity
import com.personalfinance.cuentas.data.local.entities.BudgetEntity
import com.personalfinance.cuentas.data.local.entities.CaptureRuleEntity
import com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity
import com.personalfinance.cuentas.data.local.entities.CategoryEntity
import com.personalfinance.cuentas.data.local.entities.ContactEntity
import com.personalfinance.cuentas.data.local.entities.CreditCardEntity
import com.personalfinance.cuentas.data.local.entities.DebtEntity
import com.personalfinance.cuentas.data.local.entities.DebtPaymentEntity
import com.personalfinance.cuentas.data.local.entities.IncomeAllocationEntity
import com.personalfinance.cuentas.data.local.entities.IncomeEntity
import com.personalfinance.cuentas.data.local.entities.MerchantLearningEntity
import com.personalfinance.cuentas.data.local.entities.PendingTransactionEntity
import com.personalfinance.cuentas.data.local.entities.EventEntity
import com.personalfinance.cuentas.data.local.entities.ReceivableEntity
import com.personalfinance.cuentas.data.local.entities.RecurringItemEntity
import com.personalfinance.cuentas.data.local.entities.SavingsGoalEntity
import com.personalfinance.cuentas.data.local.entities.TagEntity
import com.personalfinance.cuentas.data.local.entities.TransactionEntity
import com.personalfinance.cuentas.data.local.entities.TransactionTagEntity

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TagEntity::class,
        TransactionEntity::class,
        TransactionTagEntity::class,
        AllocationModelEntity::class,
        AllocationBlockEntity::class,
        IncomeEntity::class,
        IncomeAllocationEntity::class,
        CreditCardEntity::class,
        CardPurchaseEntity::class,
        DebtEntity::class,
        DebtPaymentEntity::class,
        BudgetEntity::class,
        SavingsGoalEntity::class,
        RecurringItemEntity::class,
        CaptureRuleEntity::class,
        PendingTransactionEntity::class,
        MerchantLearningEntity::class,
        ContactEntity::class,
        ReceivableEntity::class,
        EventEntity::class
    ],
    version = 17,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun allocationDao(): AllocationDao
    abstract fun categoryDao(): CategoryDao
    abstract fun incomeDao(): IncomeDao
    abstract fun tagDao(): TagDao
    abstract fun creditCardDao(): com.personalfinance.cuentas.data.local.dao.CreditCardDao
    abstract fun debtDao(): com.personalfinance.cuentas.data.local.dao.DebtDao
    abstract fun pendingTransactionDao(): com.personalfinance.cuentas.data.local.dao.PendingTransactionDao
    abstract fun merchantLearningDao(): com.personalfinance.cuentas.data.local.dao.MerchantLearningDao
    abstract fun backupDao(): com.personalfinance.cuentas.data.local.dao.BackupDao
    abstract fun recurringItemDao(): RecurringItemDao
    abstract fun contactDao(): com.personalfinance.cuentas.data.local.dao.ContactDao
    abstract fun receivableDao(): com.personalfinance.cuentas.data.local.dao.ReceivableDao
    abstract fun eventDao(): com.personalfinance.cuentas.data.local.dao.EventDao

    companion object {
        const val DATABASE_NAME = "secure_finances.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE accounts ADD COLUMN is_exempt_from_gmf INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE accounts ADD COLUMN annual_yield_rate REAL DEFAULT NULL")
                db.execSQL("ALTER TABLE accounts ADD COLUMN is_static_saving INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Anclaje de transacciones a obligaciones (deudas y gastos fijos) y control de cuota pendiente de deudas.
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN linked_debt_id INTEGER")
                db.execSQL("ALTER TABLE transactions ADD COLUMN linked_recurring_item_id INTEGER")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_linked_debt_id ON transactions(linked_debt_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_linked_recurring_item_id ON transactions(linked_recurring_item_id)")
                db.execSQL("ALTER TABLE debts ADD COLUMN next_due_date_millis INTEGER")
            }
        }

        // Cuentas por cobrar ("Me deben"): contrapartes y porciones de gastos o prestamos que otros deben.
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `contacts` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`name` TEXT NOT NULL, " +
                        "`kind` TEXT NOT NULL, " +
                        "`created_at` INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `receivables` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`contact_id` INTEGER NOT NULL, " +
                        "`source_transaction_id` INTEGER, " +
                        "`note` TEXT, " +
                        "`owed_cents` INTEGER NOT NULL, " +
                        "`is_settled` INTEGER NOT NULL, " +
                        "`settled_at_millis` INTEGER, " +
                        "`settled_account_id` INTEGER, " +
                        "`created_at` INTEGER NOT NULL, " +
                        "FOREIGN KEY(`contact_id`) REFERENCES `contacts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`source_transaction_id`) REFERENCES `transactions`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_receivables_contact_id` ON `receivables` (`contact_id`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_receivables_source_transaction_id` ON `receivables` (`source_transaction_id`)")
            }
        }

        // Anclaje del cobro ("Me deben") a un movimiento real: el ingreso que salda la cuenta por cobrar.
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN linked_receivable_id INTEGER")
                db.execSQL("ALTER TABLE receivables ADD COLUMN settled_transaction_id INTEGER")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_linked_receivable_id` ON `transactions` (`linked_receivable_id`)")
            }
        }

        // Campo is_leisure para marcar gastos de ocio fuera de categoria.
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN is_leisure INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Captura de pagos de tarjeta de credito (ej. PSE): guarda la tarjeta a la que aplicar el pago.
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE pending_transactions ADD COLUMN suggested_card_id INTEGER")
            }
        }

        // Captura de pagos de credito/prestamo (ej. PSE con Banco de Bogota): guarda el credito a aplicar.
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE pending_transactions ADD COLUMN suggested_debt_id INTEGER")
            }
        }

        // Tasa de pago minimo revolvente para tarjetas de credito (ej. 5% vs 100%).
        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE credit_cards ADD COLUMN revolving_rate REAL NOT NULL DEFAULT 1.0")
            }
        }

        // Seguro mensual para tarjetas y prestamos.
        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE credit_cards ADD COLUMN monthly_insurance_cents INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE debts ADD COLUMN monthly_insurance_cents INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Tipo de cuota (Fintech vs Tradicional)
        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE credit_cards ADD COLUMN installment_type INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Correccion de datos cruzados: transacciones de tarjetas registradas como debitos
        // de las cuentas de debito, pago duplicado a Nu, y tipo de cuenta Rappi incorrecto.
        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Desactivar FK temporalmente para evitar conflictos con ON DELETE SET NULL
                db.execSQL("PRAGMA foreign_keys = OFF")

                // Limpiar referencias en receivables antes de eliminar las transacciones
                // (por si ON DELETE SET NULL no se ejecutó automáticamente)
                val nuIds = "(6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27)"
                val rappiIds = "(52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71," +
                               "72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,91,92,93,94,95,96)"

                db.execSQL("UPDATE receivables SET source_transaction_id = NULL WHERE source_transaction_id IN $nuIds")
                db.execSQL("UPDATE receivables SET source_transaction_id = NULL WHERE source_transaction_id = 87")
                db.execSQL("UPDATE receivables SET source_transaction_id = NULL WHERE source_transaction_id IN $rappiIds")

                // Fix 1 - Eliminar 22 debitos falsos de la cuenta Nu (IDs 6-27)
                db.execSQL("DELETE FROM transactions WHERE id IN $nuIds")

                // Fix 2 - Eliminar pago duplicado a Nu (ID 87); conservar ID 34 (detectado por SMS)
                db.execSQL("DELETE FROM transactions WHERE id = 87")

                // Fix 3 - Eliminar 41 debitos falsos de la cuenta Rappi (IDs 52-86, 91-96)
                db.execSQL("DELETE FROM transactions WHERE id IN $rappiIds")

                // Fix 4 - Corregir tipo de cuenta Rappi: CHECKING -> SAVINGS
                db.execSQL("UPDATE accounts SET type = 'SAVINGS' WHERE id = 2")

                // Reactivar FK
                db.execSQL("PRAGMA foreign_keys = ON")
            }
        }

        // Tabla de eventos personales: cumpleanos, aniversarios, fechas importantes.
        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `events` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`title` TEXT NOT NULL, " +
                        "`type` TEXT NOT NULL DEFAULT 'CUSTOM', " +
                        "`day_of_month` INTEGER NOT NULL, " +
                        "`month_of_year` INTEGER NOT NULL, " +
                        "`year` INTEGER, " +
                        "`person_name` TEXT, " +
                        "`notify_days_before` INTEGER NOT NULL DEFAULT 1, " +
                        "`is_active` INTEGER NOT NULL DEFAULT 1, " +
                        "`created_at` INTEGER NOT NULL)"
                )
            }
        }
        // Campos financieros para obligaciones con ahorro programado (SOAT, tecnomecanica, etc.)
        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE events ADD COLUMN total_amount_cents INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE events ADD COLUMN accumulated_cents INTEGER NOT NULL DEFAULT 0")
            }
        }
        // Campo coverage_percent: porcentaje del costo a cubrir con ahorro planificado.
        val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE events ADD COLUMN coverage_percent INTEGER NOT NULL DEFAULT 100")
            }
        }
        // Campo block_type: bolsillo del dashboard al que se carga el evento.
        val MIGRATION_15_16 = object : Migration(15, 16) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE events ADD COLUMN block_type TEXT DEFAULT NULL")
            }
        }
        // Compras de RappiCard faltantes del periodo oct-nov 2026.
        // Las que estan en is_fully_paid=true con 1 cuota ya estan correctas (pago extracto anterior).
        // Se insertan las compras nuevas con installments=3 y las que faltan del periodo actual.
        val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // card_id = 1 (RappiCard *5171)
                // Marcamos las 2 compras de julio (Rappi Travel vuelos/hotel) que estan en cuotas:
                // card_purchase id=1 (vuelos): de 6 cuotas, paid=3 -> ahora paid=4 (cuota de nov ya vence)
                // card_purchase id=2 (hotel):  de 6 cuotas, paid=3 -> ahora paid=4
                db.execSQL("UPDATE card_purchases SET installments_paid = 4 WHERE id IN (1, 2) AND card_id = 1 AND installments_total = 6 AND installments_paid = 3")

                // Compras nuevas del 10 oct al 6 nov que NO estan en la tabla (no estaban en el backup original)
                // Se insertan con installments_total=3, installments_paid=0, is_fully_paid=0
                val inserts = listOf(
                    // (merchant, purchase_date_millis, total_amount_cents, installments_total, installments_paid)
                    Triple("DOLLARCITY LLANOGRANDE",   1728536400000L, 8000000L),   // 10 oct - ya existe id=22 con 8000010, skip
                    Triple("TERPEL",                   1730419200000L, 2409500L),   // 1 nov - ya existe id=63 paid=1, skip
                    Triple("TIENDAS D1",               1730505600000L, 1775000L),   // 2 nov - ya existe id=62 paid=1, skip
                    Triple("SURTIFAMILIAR CERRITO",    1730592000000L, 7338500L),   // 3 nov - ya existe id=61 paid=1, skip
                    Triple("MERCADO PAGO MERCADOLIBRE",1730678400000L, 3420000L),   // 4 nov - ya existe id=59 paid=1, skip
                    Triple("RAPPI SALCHIBURGER VALLE DE LILI", 1730678400000L, 3520000L), // 4 nov - ya existe id=60 paid=1, skip
                    Triple("EDS TERPEL EL PLACER DEL",  1730937600000L, 3100500L)   // 6 nov - NUEVA
                )
                // Solo insertar la de 6 nov que no existe
                val existsCheck = db.query("SELECT COUNT(*) FROM card_purchases WHERE merchant LIKE '%TERPEL%' AND purchase_date_millis > 1730800000000")
                existsCheck.moveToFirst()
                val count = existsCheck.getInt(0)
                existsCheck.close()
                if (count == 0) {
                    db.execSQL("INSERT INTO card_purchases (card_id, merchant, purchase_date_millis, total_amount_cents, installments_total, installments_paid, interest_rate_nm, is_fully_paid) VALUES (1, 'EDS Terpel El Placer Del', 1730937600000, 3100500, 3, 0, 0.0, 0)")
                }
            }
        }
    }
}