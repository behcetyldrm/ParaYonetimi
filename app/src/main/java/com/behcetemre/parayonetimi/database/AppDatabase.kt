package com.behcetemre.parayonetimi.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.behcetemre.parayonetimi.model.CategoryModel
import com.behcetemre.parayonetimi.model.SpendingModel
import com.behcetemre.parayonetimi.model.SubCategoryModel

@Database(
    entities = [CategoryModel::class, SpendingModel::class, SubCategoryModel::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun appDao(): AppDao
}