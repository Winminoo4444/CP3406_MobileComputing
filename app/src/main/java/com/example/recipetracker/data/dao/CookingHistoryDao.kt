package com.example.recipetracker.data.dao

import androidx.room.*
import com.example.recipetracker.data.model.CookingHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface CookingHistoryDao {
    @Query("SELECT * FROM cooking_history ORDER BY cookedAt DESC")
    fun getAllHistory(): Flow<List<CookingHistory>>

    @Query("SELECT * FROM cooking_history WHERE recipeId = :recipeId ORDER BY cookedAt DESC")
    fun getHistoryForRecipe(recipeId: Long): Flow<List<CookingHistory>>

    @Query("""
        SELECT * FROM cooking_history 
        WHERE cookedAt >= :startDate 
        ORDER BY cookedAt DESC
    """)
    fun getRecentHistory(startDate: Long): Flow<List<CookingHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: CookingHistory)

    @Update
    suspend fun updateHistory(history: CookingHistory)

    @Delete
    suspend fun deleteHistory(history: CookingHistory)

    @Query("DELETE FROM cooking_history WHERE recipeId = :recipeId")
    suspend fun deleteHistoryForRecipe(recipeId: Long)
}
