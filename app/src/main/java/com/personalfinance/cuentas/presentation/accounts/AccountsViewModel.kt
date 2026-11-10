package com.personalfinance.cuentas.presentation.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalfinance.cuentas.data.local.dao.AccountDao
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.AccountType
import com.personalfinance.cuentas.domain.usecases.AdjustAccountBalanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountDao: AccountDao,
    private val adjustAccountBalanceUseCase: AdjustAccountBalanceUseCase
) : ViewModel() {

    val accounts = accountDao.getAllActiveAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBalanceCents = accountDao.getTotalConsolidatedBalanceCents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Total líquido neto real si se retirara todo descontando el 4x1000 de cuentas no exentas
    val totalNetAvailableAfterGmfCents = accounts.map { list ->
        list.filter { it.includeInTotal && !it.isArchived }
            .sumOf { it.netBalanceAfterGmfCents }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Total de dinero protegido / estático en ahorros
    val totalStaticSavingsCents = accounts.map { list ->
        list.filter { it.includeInTotal && !it.isArchived && it.isStaticSaving }
            .sumOf { it.currentBalanceCents }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    fun createAccount(
        name: String,
        type: AccountType,
        initialBalanceCents: Long,
        colorHex: String,
        includeInTotal: Boolean = true,
        isExemptFromGmf: Boolean = false,
        annualYieldRate: Double? = null,
        isStaticSaving: Boolean = false,
        iconName: String = "account_balance"
    ) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _userMessage.emit("El nombre de la cuenta no puede estar vacío")
                return@launch
            }
            val account = AccountEntity(
                name = name.trim(),
                type = type,
                currentBalanceCents = initialBalanceCents,
                colorHex = colorHex,
                iconName = iconName,
                includeInTotal = includeInTotal,
                isExemptFromGmf = isExemptFromGmf,
                annualYieldRate = annualYieldRate,
                isStaticSaving = isStaticSaving
            )
            accountDao.insertAccount(account)
            _userMessage.emit("Cuenta \"${name.trim()}\" creada exitosamente")
        }
    }

    fun updateAccountDetails(
        account: AccountEntity,
        name: String,
        type: AccountType,
        colorHex: String,
        includeInTotal: Boolean,
        isExemptFromGmf: Boolean,
        annualYieldRate: Double?,
        isStaticSaving: Boolean
    ) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _userMessage.emit("El nombre no puede estar vacío")
                return@launch
            }
            val updated = account.copy(
                name = name.trim(),
                type = type,
                colorHex = colorHex,
                includeInTotal = includeInTotal,
                isExemptFromGmf = isExemptFromGmf,
                annualYieldRate = annualYieldRate,
                isStaticSaving = isStaticSaving
            )
            accountDao.updateAccount(updated)
            _userMessage.emit("Características de la cuenta actualizadas")
        }
    }

    fun toggleIncludeInTotal(account: AccountEntity) {
        viewModelScope.launch {
            val newValue = !account.includeInTotal
            accountDao.updateIncludeInTotal(account.id, newValue)
            val msg = if (newValue) "Cuenta incluida en el saldo total" else "Cuenta excluida del saldo total"
            _userMessage.emit(msg)
        }
    }

    fun adjustBalance(accountId: Long, newBalanceCents: Long, reason: String? = null) {
        viewModelScope.launch {
            try {
                adjustAccountBalanceUseCase(accountId, newBalanceCents, reason)
                _userMessage.emit("Saldo ajustado correctamente")
            } catch (e: Exception) {
                _userMessage.emit("Error al ajustar saldo: ${e.message}")
            }
        }
    }

    fun archiveAccount(account: AccountEntity) {
        viewModelScope.launch {
            accountDao.updateAccount(account.copy(isArchived = true))
            _userMessage.emit("Cuenta archivada")
        }
    }
}
