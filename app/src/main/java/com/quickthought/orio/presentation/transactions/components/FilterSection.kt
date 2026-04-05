package com.quickthought.orio.presentation.transactions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quickthought.orio.domain.model.TransactionFilterState
import com.quickthought.orio.domain.model.TransactionTypeFilter
import com.quickthought.orio.domain.model.transactionCategories
import java.util.Locale

@Composable
fun FilterSection(state: TransactionFilterState, onFilterChange: (TransactionFilterState) -> Unit) {
    Column(modifier = Modifier
        .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { onFilterChange(state.copy(searchQuery = it)) },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            placeholder = { Text("Search notes...") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Sort Dropdown
            var showSortMenu by remember { mutableStateOf(false) }

            Box {
                FilterChip(
                    selected = true,
                    onClick = { showSortMenu = true },
                    label = { Text("Sort By") },
                    leadingIcon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Sort,
                            null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                )

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    com.quickthought.orio.domain.model.TransactionSort.entries.forEach { sortOption ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    sortOption.name.replace("_", " ").lowercase().replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(
                                            Locale.ROOT
                                        ) else it.toString()
                                    }
                                )
                            },
                            onClick = {
                                onFilterChange(state.copy(sortBy = sortOption))
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            // Type Filter: Cycles through All -> Expense -> Income
            val nextType = when (state.typeFilter) {
                TransactionTypeFilter.ALL -> TransactionTypeFilter.EXPENSE
                TransactionTypeFilter.EXPENSE -> TransactionTypeFilter.INCOME
                TransactionTypeFilter.INCOME -> TransactionTypeFilter.ALL
            }
            FilterChip(
                selected = state.typeFilter != TransactionTypeFilter.ALL,
                onClick = { onFilterChange(state.copy(typeFilter = nextType)) },
                label = { Text(state.typeFilter.name) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(transactionCategories) { category ->
                FilterChip(
                    selected = state.selectedCategory == category.id,
                    onClick = {
                        onFilterChange(
                            state.copy(
                                selectedCategory = if (state.selectedCategory == category.id) null else category.id
                            )
                        )
                    },
                    label = { Text(category.name) },
                    leadingIcon = {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }

    }
}