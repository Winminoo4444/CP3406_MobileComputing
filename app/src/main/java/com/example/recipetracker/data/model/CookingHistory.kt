package com.example.recipetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "cooking_history",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CookingHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long,
    val cookedAt: Long = System.currentTimeMillis(),
    val rating: Int = 0, // 1-5 stars
    val notes: String = "",
    val modifications: String = "" // What changes were made
)
