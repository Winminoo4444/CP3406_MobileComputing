package com.example.recipetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val ingredients: String, // JSON string of list
    val steps: String, // JSON string of list
    val notes: String = "",
    val prepTime: Int = 0, // in minutes
    val cookTime: Int = 0, // in minutes
    val servings: Int = 1,
    val difficulty: String = "Medium", // Easy, Medium, Hard
    val cuisine: String = "",
    val imageUrl: String = "",
    val isGlutenFree: Boolean = false,
    val isVegan: Boolean = false,
    val isNutFree: Boolean = false,
    val isDairyFree: Boolean = false,
    val isVegetarian: Boolean = false,
    val isFavorite: Boolean = false,
    val isPersonal: Boolean = true, // true for user's own recipes
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Helper data class for ingredients
data class Ingredient(
    val name: String,
    val quantity: String,
    val unit: String = ""
)

// Helper data class for steps
data class CookingStep(
    val stepNumber: Int,
    val instruction: String,
    val duration: Int = 0 // in minutes
)
