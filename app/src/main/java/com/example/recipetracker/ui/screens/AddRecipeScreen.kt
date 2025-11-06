package com.example.recipetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.recipetracker.data.model.Recipe
import com.example.recipetracker.viewmodel.RecipeViewModel
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    viewModel: RecipeViewModel,
    onSaveSuccess: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var cuisine by remember { mutableStateOf("") }
    var prepTime by remember { mutableStateOf("") }
    var cookTime by remember { mutableStateOf("") }
    var servings by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Medium") }
    var ingredientsText by remember { mutableStateOf("") }
    var stepsText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isGlutenFree by remember { mutableStateOf(false) }
    var isVegan by remember { mutableStateOf(false) }
    var isVegetarian by remember { mutableStateOf(false) }
    var isNutFree by remember { mutableStateOf(false) }
    var isDairyFree by remember { mutableStateOf(false) }
    var showDifficultyMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Recipe") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (name.isNotBlank() && ingredientsText.isNotBlank() && stepsText.isNotBlank()) {
                                val recipe = Recipe(
                                    name = name,
                                    description = description,
                                    cuisine = cuisine,
                                    prepTime = prepTime.toIntOrNull() ?: 0,
                                    cookTime = cookTime.toIntOrNull() ?: 0,
                                    servings = servings.toIntOrNull() ?: 1,
                                    difficulty = difficulty,
                                    ingredients = convertIngredientsToJson(ingredientsText),
                                    steps = convertStepsToJson(stepsText),
                                    notes = notes,
                                    isGlutenFree = isGlutenFree,
                                    isVegan = isVegan,
                                    isVegetarian = isVegetarian,
                                    isNutFree = isNutFree,
                                    isDairyFree = isDairyFree,
                                    isPersonal = true
                                )
                                viewModel.insertRecipe(recipe)
                                onSaveSuccess()
                            }
                        },
                        enabled = name.isNotBlank() && ingredientsText.isNotBlank() && stepsText.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Recipe Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }

            item {
                OutlinedTextField(
                    value = cuisine,
                    onValueChange = { cuisine = it },
                    label = { Text("Cuisine Type") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("e.g., Italian, Thai, Mexican") }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = prepTime,
                        onValueChange = { prepTime = it.filter { char -> char.isDigit() } },
                        label = { Text("Prep (min)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = cookTime,
                        onValueChange = { cookTime = it.filter { char -> char.isDigit() } },
                        label = { Text("Cook (min)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = servings,
                        onValueChange = { servings = it.filter { char -> char.isDigit() } },
                        label = { Text("Servings") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            item {
                Box {
                    OutlinedTextField(
                        value = difficulty,
                        onValueChange = { },
                        label = { Text("Difficulty") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDifficultyMenu = true }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select difficulty")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = showDifficultyMenu,
                        onDismissRequest = { showDifficultyMenu = false }
                    ) {
                        listOf("Easy", "Medium", "Hard").forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level) },
                                onClick = {
                                    difficulty = level
                                    showDifficultyMenu = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Dietary Information",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DietaryCheckbox("Gluten-Free", isGlutenFree) { isGlutenFree = it }
                    DietaryCheckbox("Vegan", isVegan) { isVegan = it }
                    DietaryCheckbox("Vegetarian", isVegetarian) { isVegetarian = it }
                    DietaryCheckbox("Nut-Free", isNutFree) { isNutFree = it }
                    DietaryCheckbox("Dairy-Free", isDairyFree) { isDairyFree = it }
                }
            }

            item {
                OutlinedTextField(
                    value = ingredientsText,
                    onValueChange = { ingredientsText = it },
                    label = { Text("Ingredients *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                    maxLines = 10,
                    placeholder = { Text("Enter each ingredient on a new line:\n1 cup flour\n2 eggs\n1/2 tsp salt") }
                )
            }

            item {
                OutlinedTextField(
                    value = stepsText,
                    onValueChange = { stepsText = it },
                    label = { Text("Instructions *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                    maxLines = 15,
                    placeholder = { Text("Enter each step on a new line:\n1. Preheat oven\n2. Mix ingredients\n3. Bake for 30 minutes") }
                )
            }

            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    placeholder = { Text("Any additional notes or tips") }
                )
            }
        }
    }
}

@Composable
fun DietaryCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}

fun convertIngredientsToJson(text: String): String {
    val lines = text.lines().filter { it.isNotBlank() }
    val jsonArray = JSONArray()
    
    lines.forEach { line ->
        val parts = line.trim().split(" ", limit = 3)
        val ingredient = JSONObject()
        
        when {
            parts.size >= 3 -> {
                ingredient.put("quantity", parts[0])
                ingredient.put("unit", parts[1])
                ingredient.put("name", parts[2])
            }
            parts.size == 2 -> {
                ingredient.put("quantity", parts[0])
                ingredient.put("unit", "")
                ingredient.put("name", parts[1])
            }
            else -> {
                ingredient.put("quantity", "")
                ingredient.put("unit", "")
                ingredient.put("name", line)
            }
        }
        jsonArray.put(ingredient)
    }
    
    return jsonArray.toString()
}

fun convertStepsToJson(text: String): String {
    val lines = text.lines().filter { it.isNotBlank() }
    val jsonArray = JSONArray()
    
    lines.forEachIndexed { index, line ->
        val step = JSONObject()
        val cleanedLine = line.trim().replaceFirst(Regex("^\\d+\\.?\\s*"), "")
        step.put("stepNumber", index + 1)
        step.put("instruction", cleanedLine)
        step.put("duration", 0)
        jsonArray.put(step)
    }
    
    return jsonArray.toString()
}
