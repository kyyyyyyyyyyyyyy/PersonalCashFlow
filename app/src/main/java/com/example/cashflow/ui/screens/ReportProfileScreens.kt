package com.example.cashflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cashflow.navigation.Routes
import com.example.cashflow.ui.CashflowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController, viewModel: CashflowViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val incomeByCategory = transactions.filter { it.type == "INCOME" }.groupBy { it.category }.mapValues { it.value.sumOf { t -> t.amount } }
    val expenseByCategory = transactions.filter { it.type == "EXPENSE" }.groupBy { it.category }.mapValues { it.value.sumOf { t -> t.amount } }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Laporan Keuangan") }) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Pengeluaran Berdasarkan Kategori", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            expenseByCategory.forEach { (cat, total) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(cat)
                    Text("Rp $total", color = MaterialTheme.colorScheme.error)
                }
                Divider(modifier = Modifier.padding(vertical = 4.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Pemasukan Berdasarkan Kategori", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            incomeByCategory.forEach { (cat, total) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(cat)
                    Text("Rp $total", color = MaterialTheme.colorScheme.primary)
                }
                Divider(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Profil") }) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nama: User Test", style = MaterialTheme.typography.titleMedium)
                    Text("Email: user@example.com")
                }
            }
        }
    }
}
