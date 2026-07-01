package com.example.cashflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cashflow.domain.formatRupiahInput
import com.example.cashflow.ui.CashflowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinBalanceScreen(navController: NavController, viewModel: CashflowViewModel) {
    val currentBalance = viewModel.minBalance.collectAsState().value
    var amount by remember { mutableStateOf(
        if (currentBalance > 0) currentBalance.toLong().toString().formatRupiahInput() else ""
    ) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Batas Saldo Aman") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) { Text("< Kembali") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(
                "Atur batas minimal saldo. Jika saldo di bawah batas ini, dashboard akan menampilkan peringatan.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    val raw = it.replace(".", "")
                    if (raw.all { c -> c.isDigit() } || raw.isEmpty()) {
                        amount = raw.formatRupiahInput()
                    }
                },
                label = { Text("Batas Saldo (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val raw = amount.replace(".", "")
                    val value = raw.toDoubleOrNull() ?: 0.0
                    viewModel.setMinBalance(value)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan")
            }
        }
    }
}
