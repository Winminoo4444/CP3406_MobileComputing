package com.example.recipetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recipetracker.data.model.DietaryFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilter: DietaryFilter,
    onFilterChange: (DietaryFilter) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tempFilter by remember { mutableStateOf(currentFilter) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dietary Filters",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                }
            }

            FilterOption(
                label = "Gluten-Free",
                checked = tempFilter.isGlutenFree,
                onCheckedChange = { tempFilter = tempFilter.copy(isGlutenFree = it) }
            )

            FilterOption(
                label = "Vegan",
                checked = tempFilter.isVegan,
                onCheckedChange = { tempFilter = tempFilter.copy(isVegan = it) }
            )

            FilterOption(
                label = "Vegetarian",
                checked = tempFilter.isVegetarian,
                onCheckedChange = { tempFilter = tempFilter.copy(isVegetarian = it) }
            )

            FilterOption(
                label = "Nut-Free",
                checked = tempFilter.isNutFree,
                onCheckedChange = { tempFilter = tempFilter.copy(isNutFree = it) }
            )

            FilterOption(
                label = "Dairy-Free",
                checked = tempFilter.isDairyFree,
                onCheckedChange = { tempFilter = tempFilter.copy(isDairyFree = it) }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        tempFilter = DietaryFilter()
                        onFilterChange(tempFilter)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear All")
                }

                Button(
                    onClick = {
                        onFilterChange(tempFilter)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }
        }
    }
}

@Composable
fun FilterOption(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
