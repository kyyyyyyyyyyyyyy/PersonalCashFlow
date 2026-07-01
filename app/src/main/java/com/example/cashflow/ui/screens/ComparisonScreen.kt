package com.example.cashflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cashflow.domain.ComparisonType
import com.example.cashflow.ui.CashflowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonScreen(navController: NavController, viewModel: CashflowViewModel) {
    val currentType by viewModel.comparisonType.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perbandingan") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) { Text("< Kembali") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Pilih perbandingan nilai tukar yang akan ditampilkan di Dashboard:", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            ComparisonType.entries.forEach { type ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    onClick = {
                        viewModel.setComparisonType(type)
                        navController.popBackStack()
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = type == currentType,
                            onClick = {
                                viewModel.setComparisonType(type)
                                navController.popBackStack()
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(type.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(
                                when (type) {
                                    ComparisonType.USD -> "1 Dolar AS dalam Rupiah"
                                    ComparisonType.XAU -> "1 gram Emas dalam Rupiah"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
