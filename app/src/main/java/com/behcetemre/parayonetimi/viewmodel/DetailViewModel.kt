package com.behcetemre.parayonetimi.viewmodel

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.behcetemre.parayonetimi.database.AppDao
import com.behcetemre.parayonetimi.model.CategoryModel
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class DetailViewModel @Inject constructor(private val dao: AppDao): ViewModel(){

    private val _category = MutableStateFlow<CategoryModel?>(null)
    val category : StateFlow<CategoryModel?> = _category.asStateFlow()

    /*private var _subCategory = MutableStateFlow<List<SubCategoryModel?>>(emptyList())
    val subCategory : StateFlow<List<SubCategoryModel?>> = _subCategory.asStateFlow()*/

    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    private val _selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))

    val selectedYear: StateFlow<Int> = _selectedYear
    val selectedMonth: StateFlow<Int> = _selectedMonth
    /**private val selectedDate = combine(_selectedYear, _selectedMonth){ year, month -> //ay içindeki tüm günler
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
    }*/

   /* @OptIn(ExperimentalCoroutinesApi::class)
    private val categorySpendings= selectedDate.flatMapLatest { (startDate, endDate) ->
        dao.getAmountByCategory(startDate, endDate)
    }

    fun getSubCategory(categoryId: Int) {
        viewModelScope.launch (Dispatchers.IO){
            _subCategory.value = dao.getCategorySubCategory(categoryId)
        }
    }*/

    fun getCategory(categoryId: Int) {
        viewModelScope.launch (Dispatchers.IO){
            _category.value = dao.getCategoryById(categoryId)
        }
    }

    fun updateDate(year: Int, month: Int){
        _selectedYear.value = year
        _selectedMonth.value = month
    }

}