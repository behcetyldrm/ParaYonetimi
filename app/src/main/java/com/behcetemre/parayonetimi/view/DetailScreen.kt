package com.behcetemre.parayonetimi.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
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
import com.behcetemre.parayonetimi.viewmodel.DetailViewModel
import com.behcetemre.parayonetimi.viewmodel.HomeViewModel

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    categoryId: Int
) {

    val selectedCategory by viewModel.category.collectAsState()

    val year = viewModel.selectedYear.collectAsState()
    val month = viewModel.selectedMonth.collectAsState()
    var datePickerExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(categoryId) {
        viewModel.getCategory(categoryId)
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (selectedCategory != null){

            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedCategory!!.categoryName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black.copy(0.7f),
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp),
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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    textAlign = TextAlign.Start,
                )
                Spacer(Modifier.height(8.dp))

                LazyRow() { }
            }

        }else {
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
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