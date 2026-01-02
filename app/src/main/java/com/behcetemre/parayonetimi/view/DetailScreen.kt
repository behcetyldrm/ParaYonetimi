package com.behcetemre.parayonetimi.view

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.behcetemre.parayonetimi.model.SpendingModel
import com.behcetemre.parayonetimi.model.SubCategoryModel
import com.behcetemre.parayonetimi.viewmodel.DetailViewModel
import com.behcetemre.parayonetimi.viewmodel.HomeViewModel
import com.behcetemre.parayonetimi.viewmodel.SubCategoryWithSpend
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    categoryId: Int,
    navController: NavController
) {

    LaunchedEffect(categoryId) {
        viewModel.loadId(categoryId)
    }

    /*Karakter sınır takibi koy*/

    val year = viewModel.selectedYear.collectAsState()
    val month = viewModel.selectedMonth.collectAsState()
    var datePickerExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var subCategoryExpanded by remember { mutableStateOf(false) }
    var dialogExpanded by remember { mutableStateOf(false) }


    val selectedCategory by viewModel.category.collectAsState()
    val subCategoryList by viewModel.subCategoryList.collectAsState()
    val spendings by viewModel.spendings.collectAsState()
    val totalAmount = spendings.sumOf { it?.amount ?:0 }
    var selectedSubCategory by remember { mutableStateOf<SubCategoryModel?>(null) }

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
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedCategory!!.categoryName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black.copy(0.7f)
                    )

                    Row {
                        IconButton(
                            onClick = {categoryExpanded = true},
                            shape = CircleShape,
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(selectedCategory!!.bgColor1))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(Modifier.width(2.dp))
                        IconButton(
                            onClick = { dialogExpanded = true },
                            shape = CircleShape,
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(selectedCategory!!.bgColor1))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }

                TotalCard(
                    year = year.value,
                    month = month.value,
                    limit = selectedCategory!!.limit,
                    amount = totalAmount,
                    color1 = Color(selectedCategory!!.bgColor1),
                    color2 = Color(selectedCategory!!.bgColor2),
                    icon = selectedCategory!!.icon
                ) { datePickerExpanded = true }

                /*Alt Kategoriler*/

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
                                viewModel = viewModel,
                                textColor = subCategoryTextColor,
                                onSelected = {
                                    subCategoryExpanded = true
                                    selectedSubCategory = item.subCategory
                                }
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
                                    textColor = subCategoryTextColor,
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }

            if (dialogExpanded){
                Dialog(
                    onDismissRequest = { dialogExpanded = false }
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(12.dp)
                        ) {
                            Text(
                                text = "Silmek istediğinize emin misiniz?",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(text = "Bu işlem geri alınamaz ve bu kategoriye ait tüm harcamalar silinir!")
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = { dialogExpanded = false },
                                        colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent),
                                    ) {
                                        Text(text = "İptal", color = Color(0xFFD32F2F).copy(0.6f))
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    TextButton(
                                        onClick = {
                                            if (selectedCategory != null){
                                                viewModel.deleteCategory(category = selectedCategory!!)
                                                dialogExpanded = false
                                                navController.popBackStack()
                                            }
                                        },
                                        colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent),
                                    ) {
                                        Text(text = "Sil", color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold)
                                    }
                                }
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
    AnimatedVisibility(
        visible = subCategoryExpanded,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        UpdateSubCategory(
            subCategory = selectedSubCategory,
            detailViewModel = viewModel
        ) { subCategoryExpanded = false }
    }
    AnimatedVisibility(
        visible = categoryExpanded,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        AddCategoryCard(
            detailViewModel = viewModel,
            category = selectedCategory
        ) { categoryExpanded = false }
    }
}

@Composable
fun SubCategoryCard(
    item: SubCategoryWithSpend,
    viewModel: DetailViewModel,
    bgColor: Color,
    textColor: Color,
    onSelected: () -> Unit
) {
    val subCategory = item.subCategory
    val totalAmount = item.totalAmount
    var expanded by remember { mutableStateOf(false) }
    var dialogExpanded by remember { mutableStateOf(false) }


    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier
            .width(200.dp)
            .padding(horizontal = 12.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        expanded = true
                    }
                )
            }
    ) {
        Column (modifier = Modifier.padding(12.dp)){

            Text(
                text = subCategory.subCategoryName,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(Modifier.height(8.dp))

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
            Spacer(Modifier.height(12.dp))

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
        if (subCategory.subCategoryName != "Diğer"){
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                shape = RoundedCornerShape(12.dp),
                containerColor = Color.White,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Düzenle") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        onSelected()
                        expanded = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Sil") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        dialogExpanded = true
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(textColor = textColor, leadingIconColor = textColor)
                )
            }
        }
    }

    if (dialogExpanded){
        Dialog(
            onDismissRequest = { dialogExpanded = false }
        ) {
            Box(Modifier.background(color = Color.White, shape = RoundedCornerShape(12.dp))){
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(
                        text = "Silmek istediğinize emin misiniz?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Bu işlem geri alınamaz ve bu kategoriye ait tüm harcamalar silinir!"
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { dialogExpanded = false },
                            colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent),
                        ) {
                            Text(text = "İptal", color = textColor.copy(0.6f))
                        }
                        Spacer(Modifier.width(6.dp))
                        TextButton(
                            onClick = {
                                viewModel.deleteSubCategory(subCategory)
                                dialogExpanded = false
                            },
                            colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent),
                        ) {
                            Text(text = "Sil", color = textColor, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SpendingCard(
    spending: SpendingModel,
    viewModel: DetailViewModel,
    subCategoryName: String,
    textColor: Color
) {
    val formatter = remember (spending.createdDate){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH.mm")
                .withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(spending.createdDate))
        } else {
            SimpleDateFormat("dd/MM/yyyy HH.mm", Locale("tr", "TR"))
                .format(Date(spending.createdDate))
        }
    }

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        expanded = true
                    }
                )
            },
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
                    color = Color.Black.copy(0.6f),
                    fontSize = 14.sp
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = Color.White,
            modifier = Modifier.padding(top = 2.dp)
        ) {
            DropdownMenuItem(
                text = { Text("Sil") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                },
                onClick = {
                    viewModel.deleteSpending(spending)
                },
                colors = MenuDefaults.itemColors(textColor = textColor, leadingIconColor = textColor)
            )
        }
    }
}

@Composable
fun UpdateSubCategory(
    subCategory: SubCategoryModel?,
    detailViewModel: DetailViewModel,
    onDissmiss: () -> Unit
) {
    var name by remember { mutableStateOf(subCategory?.subCategoryName) }
    var limit by remember { mutableStateOf(subCategory?.limit?.toString()) }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.5f)),
        contentAlignment = Alignment.Center
    ){
        Card(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            if (limit != null && name != null){
                                if (limit!!.isNotBlank() && name!!.isNotBlank()){
                                    val newSubCategory = subCategory?.copy(
                                        subCategoryName = name!!.trim(),
                                        limit = limit!!.trim().toInt()
                                    )
                                    detailViewModel.updateSubCategory(newSubCategory!!)
                                    onDissmiss()
                                }
                            } else {
                                Log.e("UpdateSubCategory", "Limit veya isim boş olamaz")
                            }
                        },
                        shape = CircleShape,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xff101828)),
                        modifier = Modifier.size(25.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            tint = Color.White,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(6.dp))

                    IconButton(
                        onClick = { onDissmiss() },
                        shape = CircleShape,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xff101828)),
                        modifier = Modifier.size(25.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            tint = Color.White,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = "İsim",
                    fontSize = 14.sp,
                    color = Color.Black.copy(0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(Modifier.height(4.dp))
                SpecialTextField(
                    value = name ?: "",
                    placeHolder = "Örn: Mutfak, fatura"
                ) { if (it.length <= 18) name = it }

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Limit(TL)",
                    fontSize = 14.sp,
                    color = Color.Black.copy(0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(Modifier.height(4.dp))
                SpecialTextField(
                    value = limit ?: "",
                    placeHolder = "Örn: 1000",
                    keyboardType = KeyboardType.Number
                ) { if (it.length <= 8) limit = it }
            }
        }
    }
}