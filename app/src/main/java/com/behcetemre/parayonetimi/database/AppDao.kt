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

    //belirtilen aralıkta ki harcamaları getir
    @Query("SELECT amount FROM spending_model WHERE createdDate BETWEEN :startDate AND :endDate")
    fun getAmountByDate(startDate: Long, endDate: Long) : Flow<List<Int>>

    /*Değişecek categoryId kaldır*/
    @Query("SELECT amount FROM spending_model WHERE category = :categoryId AND createdDate BETWEEN :startDate AND :endDate")
    fun getAmountByCategory(categoryId: Int, startDate: Long, endDate: Long) : Flow<List<Int>>

    //Category
    @Insert
    fun insertCategory(category: CategoryModel)
    @Update
    fun updateCategory(category: CategoryModel)
    @Delete
    fun deleteCategory(category: CategoryModel)

    //Spending
    @Insert
    fun insertSpending(spending: SpendingModel)
    @Update
    fun updateSpending(spending: SpendingModel)
    @Delete
    fun deleteSpending(spending: SpendingModel)
}