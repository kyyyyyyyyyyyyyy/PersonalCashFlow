package com.example.cashflow.domain

enum class ComparisonType(val label: String, val key: String) {
    USD("Dolar AS (USD)", "USD"),
    XAU("Emas (XAU/gram)", "XAU")
}

data class GoldPrice(
    val price: Double = 0.0,
    val currency: String = "",
    val unit: String = ""
)

fun Double.formatRupiah(): String {
    val fmt = java.text.NumberFormat.getIntegerInstance(java.util.Locale("id", "ID"))
    return "Rp ${fmt.format(this)}"
}

fun Int.formatRupiah(): String = this.toDouble().formatRupiah()

fun String.formatRupiahInput(): String {
    val digits = filter { it.isDigit() }
    if (digits.isEmpty()) return ""
    val sb = StringBuilder()
    var count = 0
    for (i in digits.lastIndex downTo 0) {
        if (count > 0 && count % 3 == 0) sb.insert(0, '.')
        sb.insert(0, digits[i])
        count++
    }
    return sb.toString()
}

data class Transaction(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: Long = System.currentTimeMillis(),
    val note: String = ""
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
