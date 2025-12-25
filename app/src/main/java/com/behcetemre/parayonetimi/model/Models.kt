package com.behcetemre.parayonetimi.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity("category_model")
data class CategoryModel(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    val categoryName: String,
    val icon: String,
    val limit: Int
)

@Entity(
    tableName = "sub_category_model",
    foreignKeys = [
        ForeignKey(
            entity = CategoryModel::class,
            parentColumns = ["categoryId"],
            childColumns = ["category"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SubCategoryModel(
    @PrimaryKey(autoGenerate = true)
    val subCategoryId: Int = 0,
    val subCategoryName: String,
    val category: Int,
    val limit: Int
)

@Entity(
    tableName = "spending_model",
    foreignKeys = [
        ForeignKey(
            entity = CategoryModel::class,
            parentColumns = ["categoryId"],
            childColumns = ["category"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SubCategoryModel::class,
            parentColumns = ["subCategoryId"],
            childColumns = ["subCategory"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SpendingModel(
    @PrimaryKey(autoGenerate = true)
    val spendingId: Int = 0,
    val amount: Int,
    val createdDate: Long,
    val category: Int,
    val subCategory: Int? = null,
    val note: String
)