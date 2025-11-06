package com.example.recipetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.recipetracker.ui.screens.*
import com.example.recipetracker.viewmodel.RecipeViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Favorites : Screen("favorites")
    object Personal : Screen("personal")
    object Community : Screen("community")
    object RecipeDetail : Screen("recipe/{recipeId}") {
        fun createRoute(recipeId: Long) = "recipe/$recipeId"
    }
    object AddRecipe : Screen("add_recipe")
    object EditRecipe : Screen("edit_recipe/{recipeId}") {
        fun createRoute(recipeId: Long) = "edit_recipe/$recipeId"
    }
    object Search : Screen("search")
    object History : Screen("history")
}

@Composable
fun RecipeNavGraph(
    navController: NavHostController,
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onAddRecipeClick = {
                    navController.navigate(Screen.AddRecipe.route)
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                viewModel = viewModel,
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(Screen.Personal.route) {
            PersonalRecipesScreen(
                viewModel = viewModel,
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onAddRecipeClick = {
                    navController.navigate(Screen.AddRecipe.route)
                },
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(Screen.Community.route) {
            CommunityRecipesScreen(
                viewModel = viewModel,
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
            RecipeDetailScreen(
                recipeId = recipeId,
                viewModel = viewModel,
                onEditClick = { 
                    navController.navigate(Screen.EditRecipe.createRoute(recipeId))
                },
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(Screen.AddRecipe.route) {
            AddRecipeScreen(
                viewModel = viewModel,
                onSaveSuccess = { navController.navigateUp() },
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.EditRecipe.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
            EditRecipeScreen(
                recipeId = recipeId,
                viewModel = viewModel,
                onSaveSuccess = { navController.navigateUp() },
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                viewModel = viewModel,
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(Screen.History.route) {
            CookingHistoryScreen(
                viewModel = viewModel,
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}
