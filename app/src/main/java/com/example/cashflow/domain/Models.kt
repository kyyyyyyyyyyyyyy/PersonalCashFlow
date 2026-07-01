package com.example.cashflow.domain

data class Transaction(
    val id: Int = 0,
    val type: String, // "INCOME" or "EXPENSE"
    val amount: Double,
    val category: String,
    val date: Long,
    val note: String
)

data class CurrencyResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)

data class Budget(
    val id: Int = 0,
    val category: String,
    val limitAmount: Double
)
