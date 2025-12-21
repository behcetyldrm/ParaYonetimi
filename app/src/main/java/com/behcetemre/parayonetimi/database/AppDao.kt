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
    @Query("SELECT SUM(amount) FROM spending_model WHERE createdDate BETWEEN :startDate AND :endDate")
    fun getAmountByDate(startDate: Long, endDate: Long) : Flow<Int?>

    @Query("SELECT * FROM spending_model WHERE createdDate BETWEEN :startDate AND :endDate")
    fun getAmountByCategory(startDate: Long, endDate: Long) : Flow<List<SpendingModel>>
    /*@Query("SELECT SUM(amount) FROM spending_model WHERE category = :categoryId AND createdDate BETWEEN :startDate AND :endDate")
    fun getAmountByCategory(categoryId: Int, startDate: Long, endDate: Long) : Flow<Int?>*/

    //Category
    @Insert
    suspend fun insertCategory(category: CategoryModel)
    @Update
    suspend fun updateCategory(category: CategoryModel)
    @Delete
    suspend fun deleteCategory(category: CategoryModel)

    //Spending
    @Insert
    suspend fun insertSpending(spending: SpendingModel)
    @Update
    suspend fun updateSpending(spending: SpendingModel)
    @Delete
    suspend fun deleteSpending(spending: SpendingModel)
}