package com.behcetemre.parayonetimi.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.behcetemre.parayonetimi.model.CategoryModel
import com.behcetemre.parayonetimi.model.SpendingModel
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    //Get
    @Query("SELECT * FROM category_model")
    fun getAllCategory() : Flow<List<CategoryModel>>

    @Query("SELECT amount FROM spending_model WHERE createdDate = :selectedDate")
    fun getAmountByDate(selectedDate: Long) : Flow<List<Int>>

    @Query("SELECT amount FROM spending_model WHERE category = :categoryId")
    fun getAmountByCategory(categoryId: Int) : Flow<List<Int>>

    //Category
    @Insert
    fun insertCategory(category: CategoryModel)
    @Update
    fun updateCategory(category: CategoryModel)
    @Delete
    fun deleteCategory(category: CategoryModel)

    //Spending
}