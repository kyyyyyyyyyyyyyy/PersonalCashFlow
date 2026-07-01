package com.example.cashflow.domain

import android.content.SharedPreferences
import com.example.cashflow.data.local.BudgetDao
import com.example.cashflow.data.local.toEntity
import com.example.cashflow.data.remote.CurrencyApi
import com.example.cashflow.data.remote.FirebaseService
import com.example.cashflow.data.remote.MetalApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREFS_COMPARISON = "comparison_type"
private const val PREFS_CACHE_TEXT = "comparison_cache_text"
private const val PREFS_CACHE_TIME = "comparison_cache_time"
private const val PREFS_CACHE_WINDOW = "comparison_cache_window"

class CashflowRepository(
    private val budgetDao: BudgetDao,
    private val prefs: SharedPreferences,
    private val forexApi: CurrencyApi,
    private val metalApi: MetalApi
) {
    fun getAllTransactions(): Flow<List<Transaction>> {
        return FirebaseService.getTransactionsStream()
    }

    suspend fun insertTransaction(transaction: Transaction) {
        FirebaseService.addTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        FirebaseService.deleteTransaction(transaction.id)
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

    fun getComparisonType(): ComparisonType {
        val key = prefs.getString(PREFS_COMPARISON, ComparisonType.USD.key) ?: ComparisonType.USD.key
        return ComparisonType.entries.firstOrNull { it.key == key } ?: ComparisonType.USD
    }

    fun setComparisonType(type: ComparisonType) {
        prefs.edit().putString(PREFS_COMPARISON, type.key).apply()
    }

    private fun currentWindow(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return if (hour in 6..17) "morning" else "evening"
    }

    fun getCachedComparisonText(): String? {
        val time = prefs.getLong(PREFS_CACHE_TIME, 0L)
        val window = prefs.getString(PREFS_CACHE_WINDOW, null) ?: return null
        val now = System.currentTimeMillis()
        if (now - time > 24 * 60 * 60 * 1000L) return null
        if (window != currentWindow()) return null
        return prefs.getString(PREFS_CACHE_TEXT, null)
    }

    private fun saveCachedComparisonText(text: String) {
        prefs.edit()
            .putString(PREFS_CACHE_TEXT, text)
            .putLong(PREFS_CACHE_TIME, System.currentTimeMillis())
            .putString(PREFS_CACHE_WINDOW, currentWindow())
            .apply()
    }

    fun getMinBalance(): Double = prefs.getFloat("min_balance", 0f).toDouble()

    fun setMinBalance(value: Double) {
        prefs.edit().putFloat("min_balance", value.toFloat()).apply()
    }

    suspend fun refreshComparison(): Result<String> {
        val type = getComparisonType()
        return try {
            val text = when (type) {
                ComparisonType.USD -> {
                    val rates = forexApi.getLatestRates("USD")
                    val idr = rates.rates["IDR"] ?: return Result.failure(Exception("Rate IDR not found"))
                    "1 USD = ${idr.formatRupiah()}"
                }
                ComparisonType.XAU -> {
                    val gold = metalApi.getGoldPrice()
                    val usdPerOunce = gold.price
                    val rates = forexApi.getLatestRates("USD")
                    val usdIdr = rates.rates["IDR"] ?: return Result.failure(Exception("Rate IDR not found"))
                    val idrPerGram = usdPerOunce * usdIdr / 31.1035
                    "1 gram Emas = ${idrPerGram.formatRupiah()}"
                }
            }
            saveCachedComparisonText(text)
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
