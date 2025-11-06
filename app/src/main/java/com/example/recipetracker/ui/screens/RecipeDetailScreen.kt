package com.example.recipetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipetracker.data.model.CookingStep
import com.example.recipetracker.data.model.Ingredient
import com.example.recipetracker.viewmodel.RecipeViewModel
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    viewModel: RecipeViewModel,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showShareDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(recipeId) {
        viewModel.loadRecipeById(recipeId)
    }

    val recipe = uiState.currentRecipe

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (recipe != null) {
                        IconButton(onClick = { viewModel.toggleFavorite(recipe) }) {
                            Icon(
                                imageVector = if (recipe.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Toggle favorite",
                                tint = if (recipe.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { showShareDialog = true }) {
                            Icon(Icons.Filled.Share, contentDescription = "Share")
                        }
                        if (recipe.isPersonal) {
                            IconButton(onClick = onEditClick) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (recipe != null) {
                ExtendedFloatingActionButton(
                    text = { Text("Mark as Cooked") },
                    icon = { Icon(Icons.Filled.Check, contentDescription = null) },
                    onClick = {
                        viewModel.addCookingHistory(recipeId)
                    }
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        if (recipe == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Info
                item {
                    RecipeHeaderInfo(
                        prepTime = recipe.prepTime,
                        cookTime = recipe.cookTime,
                        servings = recipe.servings,
                        difficulty = recipe.difficulty,
                        cuisine = recipe.cuisine
                    )
                }

                // Description
                if (recipe.description.isNotEmpty()) {
                    item {
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Description",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = recipe.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Dietary Info
                if (recipe.isVegan || recipe.isGlutenFree || recipe.isNutFree || recipe.isDairyFree || recipe.isVegetarian) {
                    item {
                        DietaryInfoCard(recipe)
                    }
                }

                // Ingredients
                item {
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ingredients",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            val ingredients = parseIngredients(recipe.ingredients)
                            ingredients.forEach { ingredient ->
                                IngredientItem(ingredient)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                // Instructions
                item {
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Instructions",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            val steps = parseSteps(recipe.steps)
                            steps.forEachIndexed { index, step ->
                                StepItem(step, index)
                                if (index < steps.size - 1) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }

                // Notes
                if (recipe.notes.isNotEmpty()) {
                    item {
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Notes",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = recipe.notes,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Share Dialog
        if (showShareDialog) {
            ShareRecipeDialog(
                recipeName = recipe?.name ?: "",
                onDismiss = { showShareDialog = false }
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog && recipe != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Recipe") },
                text = { Text("Are you sure you want to delete \"${recipe.name}\"? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteRecipe(recipe)
                            showDeleteDialog = false
                            onBackClick()
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun RecipeHeaderInfo(
    prepTime: Int,
    cookTime: Int,
    servings: Int,
    difficulty: String,
    cuisine: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoColumn(
                icon = Icons.Filled.Timer,
                label = "Total Time",
                value = "${prepTime + cookTime} min"
            )
            InfoColumn(
                icon = Icons.Filled.Restaurant,
                label = "Servings",
                value = servings.toString()
            )
            InfoColumn(
                icon = Icons.Filled.TrendingUp,
                label = "Difficulty",
                value = difficulty
            )
        }
        if (cuisine.isNotEmpty()) {
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Public,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$cuisine Cuisine",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun InfoColumn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun DietaryInfoCard(
    recipe: com.example.recipetracker.data.model.Recipe,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Dietary Information",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (recipe.isVegan) {
                    AssistChip(
                        onClick = { },
                        label = { Text("Vegan") },
                        leadingIcon = { Icon(Icons.Filled.CheckCircle, null, Modifier.size(18.dp)) }
                    )
                }
                if (recipe.isVegetarian && !recipe.isVegan) {
                    AssistChip(
                        onClick = { },
                        label = { Text("Vegetarian") },
                        leadingIcon = { Icon(Icons.Filled.CheckCircle, null, Modifier.size(18.dp)) }
                    )
                }
                if (recipe.isGlutenFree) {
                    AssistChip(
                        onClick = { },
                        label = { Text("Gluten-Free") },
                        leadingIcon = { Icon(Icons.Filled.CheckCircle, null, Modifier.size(18.dp)) }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (recipe.isNutFree) {
                    AssistChip(
                        onClick = { },
                        label = { Text("Nut-Free") },
                        leadingIcon = { Icon(Icons.Filled.CheckCircle, null, Modifier.size(18.dp)) }
                    )
                }
                if (recipe.isDairyFree) {
                    AssistChip(
                        onClick = { },
                        label = { Text("Dairy-Free") },
                        leadingIcon = { Icon(Icons.Filled.CheckCircle, null, Modifier.size(18.dp)) }
                    )
                }
            }
        }
    }
}

@Composable
fun IngredientItem(
    ingredient: Ingredient,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Circle,
            contentDescription = null,
            modifier = Modifier.size(8.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = buildString {
                append(ingredient.quantity)
                if (ingredient.unit.isNotEmpty()) append(" ${ingredient.unit}")
                append(" ${ingredient.name}")
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun StepItem(
    step: CookingStep,
    index: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = step.instruction,
                style = MaterialTheme.typography.bodyMedium
            )
            if (step.duration > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${step.duration} minutes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ShareRecipeDialog(
    recipeName: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Recipe") },
        text = { Text("Share \"$recipeName\" with friends and family via various platforms.") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

fun parseIngredients(json: String): List<Ingredient> {
    return try {
        val jsonArray = JSONArray(json)
        (0 until jsonArray.length()).map { i ->
            val obj = jsonArray.getJSONObject(i)
            Ingredient(
                name = obj.getString("name"),
                quantity = obj.getString("quantity"),
                unit = obj.optString("unit", "")
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

fun parseSteps(json: String): List<CookingStep> {
    return try {
        val jsonArray = JSONArray(json)
        (0 until jsonArray.length()).map { i ->
            val obj = jsonArray.getJSONObject(i)
            CookingStep(
                stepNumber = obj.getInt("stepNumber"),
                instruction = obj.getString("instruction"),
                duration = obj.optInt("duration", 0)
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}
