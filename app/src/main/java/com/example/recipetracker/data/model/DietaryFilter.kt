package com.example.recipetracker.data.model

data class DietaryFilter(
    val isGlutenFree: Boolean = false,
    val isVegan: Boolean = false,
    val isNutFree: Boolean = false,
    val isDairyFree: Boolean = false,
    val isVegetarian: Boolean = false
) {
    fun hasActiveFilters(): Boolean {
        return isGlutenFree || isVegan || isNutFree || isDairyFree || isVegetarian
    }

    fun matchesRecipe(recipe: Recipe): Boolean {
        if (!hasActiveFilters()) return true
        
        return (!isGlutenFree || recipe.isGlutenFree) &&
               (!isVegan || recipe.isVegan) &&
               (!isNutFree || recipe.isNutFree) &&
               (!isDairyFree || recipe.isDairyFree) &&
               (!isVegetarian || recipe.isVegetarian)
    }
}

enum class SortOption {
    NAME_ASC,
    NAME_DESC,
    DATE_NEWEST,
    DATE_OLDEST,
    PREP_TIME,
    FAVORITES_FIRST
}
