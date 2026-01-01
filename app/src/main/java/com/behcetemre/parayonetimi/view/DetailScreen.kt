package com.behcetemre.parayonetimi.view

import android.icu.util.Calendar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.behcetemre.parayonetimi.model.SpendingModel
import com.behcetemre.parayonetimi.viewmodel.DetailViewModel
import com.behcetemre.parayonetimi.viewmodel.HomeViewModel
import com.behcetemre.parayonetimi.viewmodel.SubCategoryWithSpend
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    categoryId: Int
) {

    LaunchedEffect(categoryId) {
        viewModel.loadId(categoryId)
    }

    val year = viewModel.selectedYear.collectAsState()
    val month = viewModel.selectedMonth.collectAsState()
    var datePickerExpanded by remember { mutableStateOf(false) }

    val selectedCategory by viewModel.category.collectAsState()
    val subCategoryList by viewModel.subCategoryList.collectAsState()
    val spendings by viewModel.spendings.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (selectedCategory != null){
            val subCategoryBgColor = Color(selectedCategory!!.iconColor)
            val subCategoryTextColor = Color(selectedCategory!!.bgColor2)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedCategory!!.categoryName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black.copy(0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp),
                    textAlign = TextAlign.Start
                )
                TotalCard(
                    year = year.value,
                    month = month.value,
                    limit = selectedCategory!!.limit,
                    amount = 5000, /*veritabanından alınacak*/
                    color1 = Color(selectedCategory!!.bgColor1),
                    color2 = Color(selectedCategory!!.bgColor2),
                    icon = selectedCategory!!.icon
                ) { datePickerExpanded = true }

                /*Alt Kategoriler*/
                /*
                detaylı görüntüleme için bottom sheet ekle
                */
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Alt Kategoriler",
                    fontSize = 14.sp,
                    color = Color.Black.copy(0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Start,
                )
                Spacer(Modifier.height(8.dp))

                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(subCategoryList){ item ->
                        if (item != null){
                            SubCategoryCard(
                                item = item,
                                bgColor = subCategoryBgColor,
                                textColor = subCategoryTextColor
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                /*Harcamalar*/

                Text(
                    text = "Harcamalar",
                    fontSize = 14.sp,
                    color = Color.Black.copy(0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Start,
                )
                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(spendings){ item ->
                        if (item != null){
                            val subCategory = subCategoryList.find { it?.subCategory?.subCategoryId == item.subCategory }
                            if (subCategory != null){
                                val subName = subCategory.subCategory.subCategoryName
                                SpendingCard(
                                    spending = item,
                                    subCategoryName = subName,
                                    textColor = subCategoryTextColor
                                )
                            }
                        }
                    }
                }
            }

        }else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Kategori bulunamadı", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    AnimatedVisibility(
        visible = datePickerExpanded,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        DatePickerCard(year = year.value, month = month.value, onDissmiss = { datePickerExpanded = false }) { year, month ->
            viewModel.updateDate(year = year, month = month)
        }
    }
}

@Composable
fun SubCategoryCard(item: SubCategoryWithSpend, bgColor: Color, textColor: Color) {
    val subCategory = item.subCategory
    val totalAmount = item.totalAmount

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier
            .width(200.dp)
            .padding(horizontal = 12.dp)
    ) {
        Column (modifier = Modifier.padding(12.dp)){
            Text(
                text = subCategory.subCategoryName,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black.copy(0.7f)
            )
            Spacer(Modifier.height(4.dp))

            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Text(
                    text = "Limit: ",
                    fontSize = 14.sp,
                    color = Color.Black.copy(0.5f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${subCategory.limit}₺",
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(8.dp))

            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Text(
                    text = "Harcama: ",
                    fontSize = 14.sp,
                    color = Color.Black.copy(0.5f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "$totalAmount₺",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun SpendingCard(
    spending: SpendingModel,
    subCategoryName: String,
    textColor: Color
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH.mm")
        .withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(spending.createdDate))

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = subCategoryName,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    fontSize = 18.sp
                )
                Text(
                    text = "${spending.amount}₺",
                    color = textColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }
            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = spending.note,
                    color = Color.Black.copy(0.6f)
                )
                Text(
                    text = formatter,
                    color = Color.Black.copy(0.6f)
                )
            }
        }
    }
}