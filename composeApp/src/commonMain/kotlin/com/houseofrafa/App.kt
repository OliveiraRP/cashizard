package com.houseofrafa

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.houseofrafa.core.ui.theme.CashizardTheme
import com.houseofrafa.feature.auth.presentation.AuthScreen
import com.houseofrafa.feature.home.presentation.HomeScreen
import com.houseofrafa.navigation.Route

@Composable
fun App() {
    val navController = rememberNavController()

    CashizardTheme {
        NavHost(
            navController    = navController,
            startDestination = Route.Auth,
        ) {
            composable<Route.Auth> {
                AuthScreen(
                    onNavigateToHome = {
                        navController.navigate(Route.Home) {
                            popUpTo<Route.Auth> { inclusive = true }
                        }
                    },
                )
            }
            composable<Route.Home> {
                HomeScreen()
            }
        }
    }
}
