package com.example.cashflow.domain

import com.example.cashflow.data.local.BudgetDao
import com.example.cashflow.data.local.TransactionDao
import com.example.cashflow.data.local.toEntity
import com.example.cashflow.data.remote.CurrencyApi
import com.example.cashflow.domain.Budget
import com.example.cashflow.domain.CurrencyResponse
import com.example.cashflow.domain.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CashflowRepository(
    private val dao: TransactionDao,
    private val budgetDao: BudgetDao,
    private val api: CurrencyApi
) {
    fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { list -> list.map { it.toDomain() } }
    }

    suspend fun getTransactionById(id: Int): Transaction? {
        return dao.getTransactionById(id)?.toDomain()
    }

    suspend fun insertTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction.toEntity())
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction.toEntity())
    }

    fun getAllBudgets(): Flow<List<Budget>> {
        return budgetDao.getAllBudgets().map { list -> list.map { it.toDomain() } }
    }

    suspend fun insertBudget(budget: Budget) {
        budgetDao.insertBudget(budget.toEntity())
    }

    suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget.toEntity())
    }

    suspend fun getExchangeRates(base: String = "USD"): Result<CurrencyResponse> {
        return try {
            val response = api.getLatestRates(base)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
