package com.example.cashflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cashflow.domain.Budget
import com.example.cashflow.domain.formatRupiah
import com.example.cashflow.navigation.Routes
import com.example.cashflow.ui.CashflowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetListScreen(navController: NavController, viewModel: CashflowViewModel) {
    val budgets by viewModel.budgets.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Anggaran") }) },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.ADD_BUDGET) }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            items(budgets) { budget ->
                val spent = transactions
                    .filter { it.type == "EXPENSE" && it.category == budget.category }
                    .sumOf { it.amount }
                
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(budget.category, style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { viewModel.deleteBudget(budget) }, modifier = Modifier.size(24.dp)) {
                                Text("X", color = MaterialTheme.colorScheme.error)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Batas: ${budget.limitAmount.formatRupiah()}")
                        Text("Terpakai: ${spent.formatRupiah()}", color = if (spent > budget.limitAmount) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = (spent / budget.limitAmount).toFloat().coerceIn(0f, 1f),
                            modifier = Modifier.fillMaxWidth(),
                            color = if (spent > budget.limitAmount) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(navController: NavController, viewModel: CashflowViewModel) {
    var category by remember { mutableStateOf("") }
    var limitAmount by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("Tambah Anggaran") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = limitAmount, onValueChange = { limitAmount = it }, label = { Text("Batas Anggaran (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                if (category.isNotBlank() && limitAmount.isNotBlank()) {
                    viewModel.addBudget(Budget(category = category, limitAmount = limitAmount.toDoubleOrNull() ?: 0.0))
                    navController.popBackStack()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Simpan Anggaran")
            }
        }
    }
}
