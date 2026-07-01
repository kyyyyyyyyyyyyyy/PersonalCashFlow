package com.example.cashflow

import android.app.Application
import com.example.cashflow.data.local.AppDatabase
import com.example.cashflow.data.remote.RetrofitClient
import com.example.cashflow.domain.CashflowRepository

class CashflowApp : Application() {
    lateinit var repository: CashflowRepository

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getDatabase(this)
        repository = CashflowRepository(database.transactionDao(), database.budgetDao(), RetrofitClient.api)
    }
}
