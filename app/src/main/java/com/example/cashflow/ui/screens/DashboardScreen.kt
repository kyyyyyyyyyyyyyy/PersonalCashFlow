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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cashflow.domain.formatRupiah
import com.example.cashflow.navigation.Routes
import com.example.cashflow.ui.CashflowViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, viewModel: CashflowViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val comparisonText by viewModel.comparisonText.collectAsState()
    val minBalance by viewModel.minBalance.collectAsState()

    val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dashboard") }) },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.ADD_TRANSACTION) }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Saldo Saat Ini", style = MaterialTheme.typography.titleMedium)
                    Text(
                        balance.formatRupiah(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (minBalance > 0 && balance < minBalance) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                    if (minBalance > 0 && balance < minBalance) {
                        Text(
                            "Saldo di bawah batas aman (min ${minBalance.formatRupiah()})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Masuk: ${totalIncome.formatRupiah()}", color = MaterialTheme.colorScheme.primary)
                        Text("Keluar: ${totalExpense.formatRupiah()}", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$comparisonText", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Transaksi Terakhir", style = MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(transactions.take(5)) { t ->
                    ListItem(
                        headlineContent = { Text(t.note) },
                        supportingContent = { Text(SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(t.date))) },
                        trailingContent = {
                            Text(
                                t.amount.formatRupiah(),
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
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") }, label = { Text("Dashboard") }, selected = false, onClick = { navController.navigate(Routes.DASHBOARD) })
        NavigationBarItem(icon = { Icon(Icons.Default.List, contentDescription = "Transaksi") }, label = { Text("Transaksi") }, selected = false, onClick = { navController.navigate(Routes.TRANSACTION_LIST) })
        NavigationBarItem(icon = { Icon(Icons.Default.Star, contentDescription = "Anggaran") }, label = { Text("Anggaran") }, selected = false, onClick = { navController.navigate(Routes.BUDGET_LIST) })
        NavigationBarItem(icon = { Icon(Icons.Default.Info, contentDescription = "Laporan") }, label = { Text("Laporan") }, selected = false, onClick = { navController.navigate(Routes.REPORT) })
        NavigationBarItem(icon = { Icon(Icons.Default.Person, contentDescription = "Profil") }, label = { Text("Profil") }, selected = false, onClick = { navController.navigate(Routes.PROFILE) })
    }
}
