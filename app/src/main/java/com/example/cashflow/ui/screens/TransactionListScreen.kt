package com.example.cashflow.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.cashflow.navigation.Routes
import com.example.cashflow.ui.CashflowViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(navController: NavController, viewModel: CashflowViewModel) {
    val transactions by viewModel.transactions.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Daftar Transaksi") }) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(transactions) { t ->
                ListItem(
                    headlineContent = { Text(t.note) },
                    supportingContent = { Text("${t.category} - ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(t.date))}") },
                    trailingContent = {
                        Text(
                            "Rp ${t.amount}",
                            color = if (t.type == "INCOME") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    },
                    modifier = Modifier.clickable { navController.navigate(Routes.DETAIL_TRANSACTION.replace("{id}", t.id.toString())) }
                )
                Divider()
            }
        }
    }
}
