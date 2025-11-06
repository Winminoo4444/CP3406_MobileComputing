package com.example.recipetracker.data.dao

import androidx.room.*
import com.example.recipetracker.data.model.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY updatedAt DESC")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeById(recipeId: Long): Flow<Recipe?>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE isPersonal = 1 ORDER BY updatedAt DESC")
    fun getPersonalRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE isPersonal = 0 ORDER BY updatedAt DESC")
    fun getCommunityRecipes(): Flow<List<Recipe>>

    @Query("""
        SELECT * FROM recipes 
        WHERE name LIKE '%' || :query || '%' 
        OR cuisine LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
    """)
    fun searchRecipes(query: String): Flow<List<Recipe>>

    @Query("""
        SELECT * FROM recipes 
        WHERE (:isGlutenFree = 0 OR isGlutenFree = 1)
        AND (:isVegan = 0 OR isVegan = 1)
        AND (:isNutFree = 0 OR isNutFree = 1)
        AND (:isDairyFree = 0 OR isDairyFree = 1)
        AND (:isVegetarian = 0 OR isVegetarian = 1)
        ORDER BY updatedAt DESC
    """)
    fun getRecipesByDietaryRestrictions(
        isGlutenFree: Boolean,
        isVegan: Boolean,
        isNutFree: Boolean,
        isDairyFree: Boolean,
        isVegetarian: Boolean
    ): Flow<List<Recipe>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :recipeId")
    suspend fun updateFavoriteStatus(recipeId: Long, isFavorite: Boolean)

    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: Long)
}
