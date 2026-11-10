package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "icon_name")
    val iconName: String = "category",

    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#3F51B5",

    @ColumnInfo(name = "default_block_type")
    val defaultBlockType: BlockType = BlockType.NEEDS,

    @ColumnInfo(name = "is_system")
    val isSystem: Boolean = false
)
