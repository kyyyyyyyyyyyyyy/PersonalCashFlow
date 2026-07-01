package com.example.cashflow.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashflow.domain.CashflowRepository
import com.example.cashflow.domain.CurrencyResponse
import com.example.cashflow.domain.Transaction
import com.example.cashflow.domain.Budget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CashflowViewModel(private val repository: CashflowRepository) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()

    private val _currencyRates = MutableStateFlow<CurrencyResponse?>(null)
    val currencyRates: StateFlow<CurrencyResponse?> = _currencyRates.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllTransactions().collect { list ->
                _transactions.value = list
            }
        }
        viewModelScope.launch {
            repository.getAllBudgets().collect { list ->
                _budgets.value = list
            }
        }
        fetchRates()
    }

    private fun fetchRates() {
        viewModelScope.launch {
            val result = repository.getExchangeRates()
            if (result.isSuccess) {
                _currencyRates.value = result.getOrNull()
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun addBudget(budget: Budget) {
        viewModelScope.launch {
            repository.insertBudget(budget)
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }
}
