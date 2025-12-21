package com.behcetemre.parayonetimi.viewmodel

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.behcetemre.parayonetimi.database.AppDao
import com.behcetemre.parayonetimi.model.CategoryModel
import com.behcetemre.parayonetimi.model.SpendingModel
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
import javax.inject.Inject

data class CategoryWithSpend(
    val category: CategoryModel,
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
        categorySpendings
    ){ categories, spendings ->
        categories.map { category ->
            val totalAmount = spendings.filter { it.category == category.categoryId }.sumOf { it.amount }
            CategoryWithSpend(category, totalAmount)
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


    //flatMapLatest -> sadece seçili işlemi yapar diğer işlemleri durdurur
    val spendings : StateFlow<Int?> = selectedDate.flatMapLatest { (startDate, endDate) ->
        dao.getAmountByDate(startDate = startDate, endDate = endDate)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    /*fun getCategoryId(id: Int) : StateFlow<Int?>{
        val categorySpendings : StateFlow<Int?> = selectedDate.flatMapLatest { (startDate, endDate) ->
            dao.getAmountByCategory(categoryId = id, startDate = startDate, endDate = endDate)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
        return categorySpendings
    }*/




    fun addCategory(categoryModel: CategoryModel) {
        viewModelScope.launch (Dispatchers.IO){
            dao.insertCategory(categoryModel)
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