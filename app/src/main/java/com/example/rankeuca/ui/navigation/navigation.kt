package com.example.rankeuca.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rankeuca.ui.screens.massvote.MassVoteScreen
import com.example.rankeuca.ui.screens.menu.MenuScreen
import com.example.rankeuca.ui.screens.register.RegisterScreen
import com.example.rankeuca.ui.screens.results.ResultsScreen

sealed class Screen(val route: String) {
    object Register : Screen("register")
    object Menu : Screen("menu")
    object MassVote : Screen("mass_vote")
    object Results : Screen("results/{questionId}") {
        fun passQuestionId(questionId: Int): String = "results/$questionId"
    }
}

@Composable
fun RankeUcaNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route
    ) {
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegistered = { navController.navigate(Screen.Menu.route) }
            )
        }

        composable(Screen.Menu.route) {
            MenuScreen(
                onMassVoteClick = { navController.navigate(Screen.MassVote.route) }
            )
        }

        composable(Screen.MassVote.route) {
            MassVoteScreen(
                onVoteSuccess = { questionId ->
                    navController.navigate(Screen.Results.passQuestionId(questionId))
                },
                onBack = { navController.popBackStack() },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(
            route = Screen.Results.route,
            arguments = listOf(navArgument("questionId") { defaultValue = 0 })
        ) { backStackEntry ->
            val questionId = backStackEntry.arguments?.getInt("questionId") ?: 0
            ResultsScreen(
                questionId = questionId,
                onNewVote = { navController.navigate(Screen.MassVote.route) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}