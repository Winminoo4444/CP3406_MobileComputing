package com.example.recipetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipetracker.data.model.DietaryFilter
import com.example.recipetracker.ui.components.FilterBottomSheet
import com.example.recipetracker.ui.components.RecipeCard
import com.example.recipetracker.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RecipeViewModel,
    onRecipeClick: (Long) -> Unit,
    onAddRecipeClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showFilterSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hannibal") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Badge(
                            containerColor = if (uiState.dietaryFilter.hasActiveFilters()) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                        }
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("My Recipes") },
                                onClick = { /* Navigate to personal */ },
                                leadingIcon = { Icon(Icons.Filled.Person, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Community Recipes") },
                                onClick = { /* Navigate to community */ },
                                leadingIcon = { Icon(Icons.Filled.People, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Favorites") },
                                onClick = { /* Navigate to favorites */ },
                                leadingIcon = { Icon(Icons.Filled.Favorite, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Cooking History") },
                                onClick = { /* Navigate to history */ },
                                leadingIcon = { Icon(Icons.Filled.History, null) }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRecipeClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Recipe")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.filteredRecipes.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Restaurant,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (uiState.dietaryFilter.hasActiveFilters()) 
                                "No recipes match your filters" 
                            else 
                                "No recipes yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (uiState.dietaryFilter.hasActiveFilters()) 
                                "Try adjusting your filters" 
                            else 
                                "Add your first recipe to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            QuickAccessSection(
                                personalCount = uiState.personalRecipes.size,
                                favoriteCount = uiState.favoriteRecipes.size,
                                communityCount = uiState.communityRecipes.size
                            )
                        }

                        item {
                            Text(
                                text = "All Recipes",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(
                            items = uiState.filteredRecipes,
                            key = { it.id }
                        ) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                onClick = { onRecipeClick(recipe.id) },
                                onFavoriteClick = { viewModel.toggleFavorite(recipe) }
                            )
                        }
                    }
                }
            }

            if (showFilterSheet) {
                FilterBottomSheet(
                    currentFilter = uiState.dietaryFilter,
                    onFilterChange = { filter ->
                        viewModel.updateDietaryFilter(filter)
                    },
                    onDismiss = { showFilterSheet = false }
                )
            }
        }
    }
}

@Composable
fun QuickAccessSection(
    personalCount: Int,
    favoriteCount: Int,
    communityCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Access",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickAccessItem(
                    icon = Icons.Filled.Person,
                    label = "My Recipes",
                    count = personalCount
                )
                QuickAccessItem(
                    icon = Icons.Filled.Favorite,
                    label = "Favorites",
                    count = favoriteCount
                )
                QuickAccessItem(
                    icon = Icons.Filled.People,
                    label = "Community",
                    count = communityCount
                )
            }
        }
    }
}

@Composable
fun QuickAccessItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
