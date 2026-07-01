package com.example.cashflow.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashflow.domain.CashflowRepository
import com.example.cashflow.domain.ComparisonType
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

    private val _comparisonType = MutableStateFlow(repository.getComparisonType())
    val comparisonType: StateFlow<ComparisonType> = _comparisonType.asStateFlow()

    private val _comparisonText = MutableStateFlow("Memuat...")
    val comparisonText: StateFlow<String> = _comparisonText.asStateFlow()

    private val _minBalance = MutableStateFlow(repository.getMinBalance())
    val minBalance: StateFlow<Double> = _minBalance.asStateFlow()

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
        fetchComparison()
    }

    fun setComparisonType(type: ComparisonType) {
        repository.setComparisonType(type)
        _comparisonType.value = type
        fetchComparison()
    }

    fun setMinBalance(value: Double) {
        repository.setMinBalance(value)
        _minBalance.value = value
    }

    private fun fetchComparison() {
        viewModelScope.launch {
            val cached = repository.getCachedComparisonText()
            if (cached != null) {
                _comparisonText.value = cached
                return@launch
            }
            _comparisonText.value = "Memuat..."
            val result = repository.refreshComparison()
            _comparisonText.value = result.getOrElse { "Gagal memuat" }
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
