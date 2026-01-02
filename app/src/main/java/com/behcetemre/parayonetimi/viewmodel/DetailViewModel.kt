package com.behcetemre.parayonetimi.viewmodel

import android.icu.util.Calendar
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.behcetemre.parayonetimi.database.AppDao
import com.behcetemre.parayonetimi.model.CategoryModel
import com.behcetemre.parayonetimi.model.SpendingModel
import com.behcetemre.parayonetimi.model.SubCategoryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList
import kotlin.collections.map

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DetailViewModel @Inject constructor(private val dao: AppDao): ViewModel(){

    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    private val _selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))

    val selectedYear: StateFlow<Int> = _selectedYear
    val selectedMonth: StateFlow<Int> = _selectedMonth
    private val selectedDate = combine(_selectedYear, _selectedMonth){ year, month -> //ay içindeki tüm günler
        val startDay = Calendar.getInstance().apply { //ayın ilk günü
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val endDay = Calendar.getInstance().apply { //ayın son günü
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH)) //ayın son günü
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        startDay.timeInMillis to endDay.timeInMillis //iki tarih aralığını paket yapar (pair)
    }

    private val categoryId = MutableStateFlow<Int?>(null)

    val category : StateFlow<CategoryModel?> = categoryId.flatMapLatest { categoryId ->
        if (categoryId != null) {
            dao.getCategoryById(categoryId)
        } else {
            flowOf(null)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val spendings: StateFlow<List<SpendingModel?>> = categoryId.flatMapLatest { categoryId ->
        if (categoryId != null) {
            selectedDate.flatMapLatest { (startDate, endDate) ->
                dao.getAmountByCategoryId(startDate, endDate, categoryId)
            }
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _subCategoryList : StateFlow<List<SubCategoryModel?>> = categoryId.flatMapLatest { categoryId ->
        if (categoryId != null) {
            dao.getCategorySubCategory(categoryId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val subCategoryList : StateFlow<List<SubCategoryWithSpend?>> = combine(
        flow = _subCategoryList,
        flow2 = spendings
    ){ subCategories, spendings ->
        subCategories.map { subCategory ->
            if (subCategory == null) {
                return@map null
            } else {
                val totalAmount = spendings.filter { it?.subCategory == subCategory.subCategoryId }.sumOf { it?.amount ?:0 }
                SubCategoryWithSpend(subCategory = subCategory, totalAmount = totalAmount)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun loadId(id: Int){
        viewModelScope.launch {
            categoryId.value = id
        }
    }

    fun updateCategory(categoryModel: CategoryModel){
        viewModelScope.launch (Dispatchers.IO){
            dao.updateCategory(categoryModel)
        }
    }

    fun updateSubCategory(subCategoryModel: SubCategoryModel){
        viewModelScope.launch (Dispatchers.IO){
            dao.updateSubCategory(subCategoryModel)
        }
    }

    fun deleteSpending(spending: SpendingModel) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteSpending(spending)
        }
    }


    fun deleteSubCategory(subCategory: SubCategoryModel) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteSubCategory(subCategory)
        }
    }

    fun deleteCategory(category: CategoryModel) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteCategory(category)
        }
    }

    fun updateDate(year: Int, month: Int){
        _selectedYear.value = year
        _selectedMonth.value = month
    }

}