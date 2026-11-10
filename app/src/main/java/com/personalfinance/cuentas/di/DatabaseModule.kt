package com.personalfinance.cuentas.di

import android.content.Context
import androidx.room.Room
import com.personalfinance.cuentas.core.database.AppDatabase
import com.personalfinance.cuentas.core.security.KeystoreManager
import com.personalfinance.cuentas.data.local.dao.AccountDao
import com.personalfinance.cuentas.data.local.dao.AllocationDao
import com.personalfinance.cuentas.data.local.dao.CategoryDao
import com.personalfinance.cuentas.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        keystoreManager: KeystoreManager
    ): AppDatabase {
        // Obtener la contraseña de 256 bits generada criptográficamente y custodiada en el Keystore
        val passphraseBytes = keystoreManager.getOrCreateDatabasePassphrase()

        // Factory de SQLCipher para cifrado transparente AES-256 en Room
        val openHelperFactory = SupportOpenHelperFactory(passphraseBytes)

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .openHelperFactory(openHelperFactory)
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
                AppDatabase.MIGRATION_11_12,
                AppDatabase.MIGRATION_12_13,
                AppDatabase.MIGRATION_13_14,
                AppDatabase.MIGRATION_14_15,
                AppDatabase.MIGRATION_15_16,
                AppDatabase.MIGRATION_16_17
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideAllocationDao(db: AppDatabase): AllocationDao = db.allocationDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideIncomeDao(db: AppDatabase): com.personalfinance.cuentas.data.local.dao.IncomeDao = db.incomeDao()

    @Provides
    fun provideTagDao(db: AppDatabase): com.personalfinance.cuentas.data.local.dao.TagDao = db.tagDao()

    @Provides
    fun provideCreditCardDao(db: AppDatabase): com.personalfinance.cuentas.data.local.dao.CreditCardDao = db.creditCardDao()

    @Provides
    fun provideDebtDao(db: AppDatabase): com.personalfinance.cuentas.data.local.dao.DebtDao = db.debtDao()

    @Provides
    fun providePendingTransactionDao(db: AppDatabase): com.personalfinance.cuentas.data.local.dao.PendingTransactionDao = db.pendingTransactionDao()

    @Provides
    fun provideMerchantLearningDao(db: AppDatabase): com.personalfinance.cuentas.data.local.dao.MerchantLearningDao = db.merchantLearningDao()

    @Provides
    fun provideBackupDao(db: AppDatabase): com.personalfinance.cuentas.data.local.dao.BackupDao = db.backupDao()

    @Provides
    fun provideRecurringItemDao(db: AppDatabase): com.personalfinance.cuentas.data.local.dao.RecurringItemDao = db.recurringItemDao()

    @Provides
    fun provideContactDao(db: AppDatabase): com.personalfinance.cuentas.data.local.dao.ContactDao = db.contactDao()

    @Provides
    fun provideReceivableDao(db: AppDatabase): com.personalfinance.cuentas.data.local.dao.ReceivableDao = db.receivableDao()

    @Provides
    fun provideEventDao(db: AppDatabase): com.personalfinance.cuentas.data.local.dao.EventDao = db.eventDao()
}
