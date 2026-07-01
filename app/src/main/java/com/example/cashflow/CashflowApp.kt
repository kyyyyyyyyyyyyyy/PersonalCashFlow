package com.example.cashflow

import android.app.Application
import android.content.SharedPreferences
import com.example.cashflow.data.local.AppDatabase
import com.example.cashflow.data.remote.FirebaseService
import com.example.cashflow.data.remote.MetalClient
import com.example.cashflow.data.remote.RetrofitClient
import com.example.cashflow.domain.CashflowRepository

class CashflowApp : Application() {
    lateinit var repository: CashflowRepository
    lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        FirebaseService.init()
        prefs = getSharedPreferences("cashflow_prefs", MODE_PRIVATE)
        val database = AppDatabase.getDatabase(this)
        repository = CashflowRepository(database.budgetDao(), prefs, RetrofitClient.api, MetalClient.api)
    }
}
