package com.example.recipetracker

import android.app.Application
import com.example.recipetracker.data.database.RecipeDatabase
import com.example.recipetracker.data.repository.RecipeRepository

class RecipeTrackerApplication : Application() {
    val database by lazy { RecipeDatabase.getDatabase(this) }
    val repository by lazy {
        RecipeRepository(
            recipeDao = database.recipeDao(),
            cookingHistoryDao = database.cookingHistoryDao()
        )
    }
}
