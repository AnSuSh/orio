package com.quickthought.orio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryData(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val colorHex: String
)
