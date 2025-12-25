package com.behcetemre.parayonetimi.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.behcetemre.parayonetimi.model.CategoryModel
import com.behcetemre.parayonetimi.model.SpendingModel
import com.behcetemre.parayonetimi.model.SubCategoryModel
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

    @Query("SELECT * FROM sub_category_model")
    fun getAllSubCategory() : Flow<List<SubCategoryModel>>

    //Category
    @Insert
    suspend fun insertCategory(category: CategoryModel) : Long
    @Update
    suspend fun updateCategory(category: CategoryModel)
    @Delete
    suspend fun deleteCategory(category: CategoryModel)

    //sub category
    @Insert
    suspend fun insertSubCategory(subCategory: SubCategoryModel)
    @Update
    suspend fun updateSubCategory(subCategory: SubCategoryModel)
    @Delete
    suspend fun deleteSubCategory(subCategory: SubCategoryModel)


    //Spending
    @Insert
    suspend fun insertSpending(spending: SpendingModel)
    @Update
    suspend fun updateSpending(spending: SpendingModel)
    @Delete
    suspend fun deleteSpending(spending: SpendingModel)
}