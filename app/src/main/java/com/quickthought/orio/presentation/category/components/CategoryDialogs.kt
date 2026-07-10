package com.quickthought.orio.presentation.category.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quickthought.orio.domain.model.Category
import com.quickthought.orio.domain.model.availableIcons
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryDialog(
    category: Category? = null,
    onDismiss: () -> Unit,
    onSave: (Category) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var selectedIconName by remember { mutableStateOf(category?.let { cat -> availableIcons.entries.find { it.value == cat.icon }?.key } ?: "Category") }
    var selectedColor by remember { mutableStateOf(category?.color ?: Color(0xFF9E9E9E)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category == null) "Add Category" else "Edit Category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Select Icon", style = MaterialTheme.typography.titleSmall)
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(48.dp),
                    modifier = Modifier.height(150.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableIcons.toList()) { (iconName, icon) ->
                        val isSelected = selectedIconName == iconName
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { selectedIconName = iconName }
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = iconName,
                                modifier = Modifier.padding(12.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Text("Select Color", style = MaterialTheme.typography.titleSmall)
                val colors = listOf(
                    Color(0xFFFF9800), Color(0xFFFFC107), Color(0xFF4CAF50),
                    Color(0xFF2196F3), Color(0xFFE91E63), Color(0xFF673AB7),
                    Color(0xFF8BC34A), Color(0xFF9E9E9E), Color(0xFF00BCD4),
                    Color(0xFF009688), Color(0xFFFF5722), Color(0xFF795548)
                )
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(36.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(colors) { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(color, CircleShape)
                                .clickable { selectedColor = color }
                                .padding(if (selectedColor == color) 4.dp else 0.dp)
                        ) {
                            if (selectedColor == color) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            Category(
                                id = category?.id ?: name.lowercase(Locale.ROOT).replace(" ", "_"),
                                name = name,
                                icon = availableIcons[selectedIconName]!!,
                                color = selectedColor
                            )
                        )
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
