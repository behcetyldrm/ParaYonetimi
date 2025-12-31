package com.behcetemre.parayonetimi.viewmodel

import android.icu.util.Calendar
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class SubCategoryWithSpend(
    val subCategory: SubCategoryModel,
    val totalAmount: Int
)
data class CategoryWithSpend(
    val category: CategoryModel,
    val subCategory: List<SubCategoryWithSpend>,
    val totalAmount: Int
)
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(private val dao: AppDao) : ViewModel() {
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

    private val categorySpendings= selectedDate.flatMapLatest { (startDate, endDate) ->
        dao.getAmountByCategory(startDate, endDate)
    }

    val categoryUiList : StateFlow<List<CategoryWithSpend>> = combine(
        dao.getAllCategory(),
        dao.getAllSubCategory(),
        categorySpendings
    ){ categories, subCategories, spendings ->
        categories.map { category ->
            val totalAmount = spendings.filter { it.category == category.categoryId }.sumOf { it.amount }
            val subCategory = subCategories.filter { it.category == category.categoryId }
            val subCategoryWithAmount = subCategory.map { subCategory ->
                val spending = spendings.filter { it.subCategory == subCategory.subCategoryId }.sumOf { it.amount }
                SubCategoryWithSpend(subCategory, spending)
            }

            CategoryWithSpend(category, subCategoryWithAmount, totalAmount)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val categoryList : StateFlow<List<CategoryModel>> = dao.getAllCategory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val subCategoryList : StateFlow<List<SubCategoryModel>> = dao.getAllSubCategory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    //flatMapLatest -> sadece seçili işlemi yapar diğer işlemleri durdurur
    val spendings : StateFlow<Int?> = selectedDate.flatMapLatest { (startDate, endDate) ->
        dao.getAmountByDate(startDate = startDate, endDate = endDate)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun addCategory(categoryModel: CategoryModel, getCategoryId: (Int) -> Unit) {
        viewModelScope.launch (Dispatchers.IO){
            val categoryId = dao.insertCategory(categoryModel)
            withContext(Dispatchers.Main){
                getCategoryId(categoryId.toInt())
            }
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


    fun addSubCategory(subCategoryModel: SubCategoryModel) {
        viewModelScope.launch (Dispatchers.IO){
            dao.insertSubCategory(subCategoryModel)
        }
    }

    fun addSpending(spendingModel: SpendingModel){
        viewModelScope.launch (Dispatchers.IO){
            dao.insertSpending(spendingModel)
        }
    }
    fun updateDate(year: Int, month: Int){
        _selectedYear.value = year
        _selectedMonth.value = month
    }
}