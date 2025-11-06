package com.example.recipetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recipetracker.data.model.CookingHistory
import com.example.recipetracker.data.model.DietaryFilter
import com.example.recipetracker.data.model.Recipe
import com.example.recipetracker.data.model.SortOption
import com.example.recipetracker.data.repository.RecipeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RecipeUiState(
    val recipes: List<Recipe> = emptyList(),
    val filteredRecipes: List<Recipe> = emptyList(),
    val favoriteRecipes: List<Recipe> = emptyList(),
    val personalRecipes: List<Recipe> = emptyList(),
    val communityRecipes: List<Recipe> = emptyList(),
    val currentRecipe: Recipe? = null,
    val searchQuery: String = "",
    val dietaryFilter: DietaryFilter = DietaryFilter(),
    val sortOption: SortOption = SortOption.DATE_NEWEST,
    val isLoading: Boolean = false,
    val error: String? = null
)

class RecipeViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Combine multiple flows
            combine(
                repository.getAllRecipes(),
                repository.getFavoriteRecipes(),
                repository.getPersonalRecipes(),
                repository.getCommunityRecipes()
            ) { all, favorites, personal, community ->
                RecipeUiState(
                    recipes = all,
                    filteredRecipes = applyFilters(all),
                    favoriteRecipes = favorites,
                    personalRecipes = personal,
                    communityRecipes = community,
                    searchQuery = _uiState.value.searchQuery,
                    dietaryFilter = _uiState.value.dietaryFilter,
                    sortOption = _uiState.value.sortOption,
                    isLoading = false
                )
            }.catch { exception ->
                _uiState.update { it.copy(error = exception.message, isLoading = false) }
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun loadRecipeById(recipeId: Long) {
        viewModelScope.launch {
            repository.getRecipeById(recipeId).collect { recipe ->
                _uiState.update { it.copy(currentRecipe = recipe) }
            }
        }
    }

    fun searchRecipes(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            _uiState.update { it.copy(filteredRecipes = applyFilters(it.recipes)) }
        } else {
            viewModelScope.launch {
                repository.searchRecipes(query).collect { results ->
                    _uiState.update { it.copy(filteredRecipes = applyFilters(results)) }
                }
            }
        }
    }

    fun updateDietaryFilter(filter: DietaryFilter) {
        _uiState.update { 
            it.copy(
                dietaryFilter = filter,
                filteredRecipes = applyFilters(it.recipes)
            )
        }
    }

    fun updateSortOption(sortOption: SortOption) {
        _uiState.update { it.copy(sortOption = sortOption) }
        viewModelScope.launch {
            repository.getSortedRecipes(sortOption).collect { sortedRecipes ->
                _uiState.update { it.copy(filteredRecipes = applyFilters(sortedRecipes)) }
            }
        }
    }

    private fun applyFilters(recipes: List<Recipe>): List<Recipe> {
        val filter = _uiState.value.dietaryFilter
        return if (filter.hasActiveFilters()) {
            recipes.filter { recipe -> filter.matchesRecipe(recipe) }
        } else {
            recipes
        }
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            repository.toggleFavorite(recipe.id, !recipe.isFavorite)
        }
    }

    fun insertRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.insertRecipe(recipe)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to save recipe: ${e.message}") }
            }
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.updateRecipe(recipe)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update recipe: ${e.message}") }
            }
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.deleteRecipe(recipe)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to delete recipe: ${e.message}") }
            }
        }
    }

    fun addCookingHistory(recipeId: Long, rating: Int = 0, notes: String = "") {
        viewModelScope.launch {
            repository.insertCookingHistory(
                CookingHistory(
                    recipeId = recipeId,
                    rating = rating,
                    notes = notes
                )
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

class RecipeViewModelFactory(
    private val repository: RecipeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
