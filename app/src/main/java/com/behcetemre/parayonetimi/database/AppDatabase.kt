package com.behcetemre.parayonetimi.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.behcetemre.parayonetimi.model.CategoryModel
import com.behcetemre.parayonetimi.model.SpendingModel

@Database(entities = [CategoryModel::class, SpendingModel::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun appDao(): AppDao
}