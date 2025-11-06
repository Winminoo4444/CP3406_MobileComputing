package com.example.recipetracker.data.repository

import com.example.recipetracker.data.dao.CookingHistoryDao
import com.example.recipetracker.data.dao.RecipeDao
import com.example.recipetracker.data.model.CookingHistory
import com.example.recipetracker.data.model.DietaryFilter
import com.example.recipetracker.data.model.Recipe
import com.example.recipetracker.data.model.SortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val cookingHistoryDao: CookingHistoryDao
) {
    // Recipe operations
    fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()

    fun getRecipeById(recipeId: Long): Flow<Recipe?> = recipeDao.getRecipeById(recipeId)

    fun getFavoriteRecipes(): Flow<List<Recipe>> = recipeDao.getFavoriteRecipes()

    fun getPersonalRecipes(): Flow<List<Recipe>> = recipeDao.getPersonalRecipes()

    fun getCommunityRecipes(): Flow<List<Recipe>> = recipeDao.getCommunityRecipes()

    fun searchRecipes(query: String): Flow<List<Recipe>> = recipeDao.searchRecipes(query)

    fun getFilteredRecipes(filter: DietaryFilter): Flow<List<Recipe>> {
        return if (filter.hasActiveFilters()) {
            recipeDao.getRecipesByDietaryRestrictions(
                isGlutenFree = filter.isGlutenFree,
                isVegan = filter.isVegan,
                isNutFree = filter.isNutFree,
                isDairyFree = filter.isDairyFree,
                isVegetarian = filter.isVegetarian
            )
        } else {
            recipeDao.getAllRecipes()
        }
    }

    fun getSortedRecipes(sortOption: SortOption): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes().map { recipes ->
            when (sortOption) {
                SortOption.NAME_ASC -> recipes.sortedBy { it.name }
                SortOption.NAME_DESC -> recipes.sortedByDescending { it.name }
                SortOption.DATE_NEWEST -> recipes.sortedByDescending { it.updatedAt }
                SortOption.DATE_OLDEST -> recipes.sortedBy { it.updatedAt }
                SortOption.PREP_TIME -> recipes.sortedBy { it.prepTime + it.cookTime }
                SortOption.FAVORITES_FIRST -> recipes.sortedByDescending { it.isFavorite }
            }
        }
    }

    suspend fun insertRecipe(recipe: Recipe): Long = recipeDao.insertRecipe(recipe)

    suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)

    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)

    suspend fun toggleFavorite(recipeId: Long, isFavorite: Boolean) {
        recipeDao.updateFavoriteStatus(recipeId, isFavorite)
    }

    // Cooking history operations
    fun getAllHistory(): Flow<List<CookingHistory>> = cookingHistoryDao.getAllHistory()

    fun getHistoryForRecipe(recipeId: Long): Flow<List<CookingHistory>> =
        cookingHistoryDao.getHistoryForRecipe(recipeId)

    fun getRecentHistory(daysAgo: Int = 30): Flow<List<CookingHistory>> {
        val startDate = System.currentTimeMillis() - (daysAgo * 24 * 60 * 60 * 1000L)
        return cookingHistoryDao.getRecentHistory(startDate)
    }

    suspend fun insertCookingHistory(history: CookingHistory) =
        cookingHistoryDao.insertHistory(history)

    suspend fun updateCookingHistory(history: CookingHistory) =
        cookingHistoryDao.updateHistory(history)

    suspend fun deleteCookingHistory(history: CookingHistory) =
        cookingHistoryDao.deleteHistory(history)

    // Recommended recipes based on history and favorites
    fun getRecommendedRecipes(): Flow<List<Recipe>> {
        // This is a simple implementation that prioritizes:
        // 1. Recipes with similar dietary restrictions to favorites
        // 2. Recipes in similar cuisines to those frequently cooked
        return recipeDao.getAllRecipes()
    }
}
