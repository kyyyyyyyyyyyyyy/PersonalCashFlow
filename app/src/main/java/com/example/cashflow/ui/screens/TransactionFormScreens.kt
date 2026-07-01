package com.example.cashflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cashflow.domain.Transaction
import com.example.cashflow.domain.formatRupiah
import com.example.cashflow.domain.formatRupiahInput
import com.example.cashflow.navigation.Routes
import com.example.cashflow.ui.CashflowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController, viewModel: CashflowViewModel) {
    var type by remember { mutableStateOf("EXPENSE") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("Tambah Transaksi") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = type == "EXPENSE", onClick = { type = "EXPENSE" }, label = { Text("Pengeluaran") })
                FilterChip(selected = type == "INCOME", onClick = { type = "INCOME" }, label = { Text("Pemasukan") })
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Jumlah (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Catatan") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                if (amount.isNotBlank()) {
                    viewModel.addTransaction(Transaction(type = type, amount = amount.replace(".", "").toDoubleOrNull() ?: 0.0, category = category, date = System.currentTimeMillis(), note = note))
                    navController.popBackStack()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Simpan")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTransactionScreen(navController: NavController, viewModel: CashflowViewModel, id: String) {
    val transactions by viewModel.transactions.collectAsState()
    val t = transactions.find { it.id == id }

    Scaffold(topBar = { TopAppBar(title = { Text("Detail Transaksi") }) }) { padding ->
        if (t != null) {
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text("Tipe: ${t.type}", style = MaterialTheme.typography.titleMedium)
                Text("Kategori: ${t.category}")
                Text("Jumlah: ${t.amount.formatRupiah()}", style = MaterialTheme.typography.headlineSmall)
                Text("Catatan: ${t.note}")
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { navController.navigate(Routes.EDIT_TRANSACTION.replace("{id}", id)) }, modifier = Modifier.weight(1f)) {
                        Text("Edit")
                    }
                    Button(onClick = {
                        viewModel.deleteTransaction(t)
                        navController.popBackStack()
                    }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                        Text("Hapus")
                    }
                }
            }
        } else {
            Text("Transaksi tidak ditemukan")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionScreen(navController: NavController, viewModel: CashflowViewModel, id: String) {
    val transactions by viewModel.transactions.collectAsState()
    val t = transactions.find { it.id == id }

    if (t != null) {
        var type by remember { mutableStateOf(t.type) }
        var amount by remember { mutableStateOf(t.amount.toLong().toString().formatRupiahInput()) }
        var category by remember { mutableStateOf(t.category) }
        var note by remember { mutableStateOf(t.note) }

        Scaffold(topBar = { TopAppBar(title = { Text("Edit Transaksi") }) }) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = type == "EXPENSE", onClick = { type = "EXPENSE" }, label = { Text("Pengeluaran") })
                    FilterChip(selected = type == "INCOME", onClick = { type = "INCOME" }, label = { Text("Pemasukan") })
                }
                Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = amount, onValueChange = {
                val raw = it.replace(".", "")
                if (raw.all { c -> c.isDigit() } || raw.isEmpty()) amount = raw.formatRupiahInput()
            }, label = { Text("Jumlah (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Catatan") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = {
                    if (amount.isNotBlank()) {
                        viewModel.addTransaction(Transaction(id = id, type = type, amount = amount.replace(".", "").toDoubleOrNull() ?: 0.0, category = category, date = t.date, note = note))
                        navController.popBackStack()
                        navController.popBackStack() // kembali ke list
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Simpan Perubahan")
                }
            }
        }
    }
}
