package com.example.cashflow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cashflow.ui.CashflowViewModel
import com.example.cashflow.ui.screens.*

object Routes {
    const val SPLASH = "splash"
    const val SIGN_IN = "sign_in"
    const val DASHBOARD = "dashboard"
    const val TRANSACTION_LIST = "transaction_list"
    const val ADD_TRANSACTION = "add_transaction"
    const val DETAIL_TRANSACTION = "detail_transaction/{id}"
    const val EDIT_TRANSACTION = "edit_transaction/{id}"
    const val BUDGET_LIST = "budget_list"
    const val ADD_BUDGET = "add_budget"
    const val REPORT = "report"
    const val PROFILE = "profile"
    const val COMPARISON = "comparison"
    const val MIN_BALANCE = "min_balance"
}

@Composable
fun AppNavGraph(navController: NavHostController, viewModel: CashflowViewModel) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) { SplashScreen(navController) }
        composable(Routes.SIGN_IN) { SignInScreen(navController) }
        composable(Routes.DASHBOARD) { DashboardScreen(navController, viewModel) }
        composable(Routes.TRANSACTION_LIST) { TransactionListScreen(navController, viewModel) }
        composable(Routes.ADD_TRANSACTION) { AddTransactionScreen(navController, viewModel) }
        composable(Routes.BUDGET_LIST) { BudgetListScreen(navController, viewModel) }
        composable(Routes.ADD_BUDGET) { AddBudgetScreen(navController, viewModel) }
        
        composable(
            route = Routes.DETAIL_TRANSACTION,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            DetailTransactionScreen(navController, viewModel, id)
        }
        
        composable(
            route = Routes.EDIT_TRANSACTION,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            EditTransactionScreen(navController, viewModel, id)
        }
        
        composable(Routes.REPORT) { ReportScreen(navController, viewModel) }
        composable(Routes.PROFILE) { ProfileScreen(navController) }
        composable(Routes.COMPARISON) { ComparisonScreen(navController, viewModel) }
        composable(Routes.MIN_BALANCE) { MinBalanceScreen(navController, viewModel) }
    }
}
