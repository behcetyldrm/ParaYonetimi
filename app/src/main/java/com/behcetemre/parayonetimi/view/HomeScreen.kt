@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.behcetemre.parayonetimi.view

import android.annotation.SuppressLint
import android.widget.Space
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.behcetemre.parayonetimi.R
import com.behcetemre.parayonetimi.model.CategoryModel
import com.behcetemre.parayonetimi.model.SpendingModel
import com.behcetemre.parayonetimi.model.SubCategoryModel
import com.behcetemre.parayonetimi.viewmodel.HomeViewModel
import com.behcetemre.parayonetimi.viewmodel.SubCategoryWithSpend
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class CategoryColor(
    val bgColor1: Color,
    val bgColor2: Color,
    val iconColor: Color
)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {

    val year = viewModel.selectedYear.collectAsState()
    val month = viewModel.selectedMonth.collectAsState()
    val categoryList = viewModel.categoryList.collectAsState()
    val categoryUiList = viewModel.categoryUiList.collectAsState()
    val spendings = viewModel.spendings.collectAsState().value ?: 0
    val limit = categoryList.value.sumOf { it.limit }

    var categoryExpanded by remember { mutableStateOf(false) }
    var spendingExpanded by remember { mutableStateOf(false) }
    var datePickerExpanded by remember { mutableStateOf(false) }

    val colorList = listOf(
        CategoryColor(Color(0xff60a5fa), Color(0xff2563eb), Color(0xffeff6ff)),
        CategoryColor(Color(0xffc084fc), Color(0xff9333ea), Color(0xfffaf5ff)),
        CategoryColor(Color(0xff2dd4bf), Color(0xff0d9488), Color(0xfff0fdfa)),
        CategoryColor(Color(0xfffb923c), Color(0xffea580c), Color(0xfffff7ed)),
        CategoryColor(Color(0xff22d3ee), Color(0xff0891b2), Color(0xffecfeff)),
        CategoryColor(Color(0xfff87171), Color(0xffdc2626), Color(0xfffef2f2))
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = { FloatingButton(
            onAddCategoryClick = { categoryExpanded = true },
            onAddSpendingClick = { spendingExpanded = true }
        ) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TotalCard(year = year.value, month = month.value, limit = limit, amount = spendings){
                datePickerExpanded = true
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Kategoriler",
                fontSize = 14.sp,
                color = Color.Black.copy(0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.height(4.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(categoryUiList.value){ item ->
                    val index = categoryList.value.indexOf(item.category) % colorList.size
                    val color = colorList[index]
                    val subCategories = item.subCategory

                    ShowCategoryCard(
                        categoryModel = item.category,
                        bgColor1 = color.bgColor1,
                        bgColor2 = color.bgColor2,
                        iconColor = color.iconColor,
                        spends = item.totalAmount,
                        subCategoryList = subCategories,
                        navController = navController
                    )
                }
            }
        }
    }

    AnimatedVisibility(
        visible = categoryExpanded,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        AddCategoryCard(viewModel = viewModel) { categoryExpanded = false }
    }

    AnimatedVisibility(
        visible = spendingExpanded,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        AddSpendingCard(viewModel = viewModel) { spendingExpanded = false }
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FloatingButton(
    onAddCategoryClick: () -> Unit,
    onAddSpendingClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }


    FloatingActionButtonMenu(
        expanded = expanded,
        button = { ToggleFloatingActionButton(
            checked = expanded,
            containerColor = { if (expanded) Color(0xFF304777) else Color(0xff101828) },
            onCheckedChange = { expanded = it }
        ) {
            Icon(
                imageVector = if (expanded) Icons.Filled.Close else Icons.Filled.Menu,
                contentDescription = "menu",
                tint = Color.White
            )
        }
        }
    ) {
        FloatingActionButtonMenuItem(
            onClick = {
                expanded = false
                onAddCategoryClick()
            },
            text = { Text("Kategori Ekle", color = Color.White) },
            containerColor = Color(0xff101828),
            icon = { Icon(imageVector = Icons.Default.Category, contentDescription = null, tint = Color.White) }
        )

        FloatingActionButtonMenuItem(
            onClick = {
                expanded = false
                onAddSpendingClick()
            },
            text = { Text("Harcama Ekle", color = Color.White) },
            containerColor = Color(0xff101828),
            icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White) }
        )
    }
}

@Composable
fun AddCategoryCard(
    viewModel: HomeViewModel,
    onDissmiss: () -> Unit
) {

    var categoryName by remember { mutableStateOf("") }
    var categoryLimit by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("shop_icon") }

    val context = LocalContext.current

    val iconList = listOf(
        "shop_icon" to R.drawable.shop_icon,
        "money_icon" to R.drawable.money_icon,
        "home_icon" to R.drawable.home_icon,
        "food_icon" to R.drawable.food_icon,
        "tech_icon" to R.drawable.tech_icon,
        "pet_icon" to R.drawable.pet_icon,
        "game_icon" to R.drawable.game_icon,
        "exercise_icon" to R.drawable.exercise_icon,
        "car_icon" to R.drawable.car_icon,
        "belge_icon" to R.drawable.belge_icon,
        "book_icon" to R.drawable.book_icon,
        "coffee_icon" to R.drawable.coffee_icon
    )

   Box(
       modifier = Modifier
           .fillMaxSize()
           .background(Color.Black.copy(0.5f)),
       contentAlignment = Alignment.Center
   ){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "İsim",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(0.7f)
                    )

                    Row{
                        IconButton(
                            onClick = {
                                if (categoryName.isNotBlank() && categoryLimit.isNotBlank() && categoryLimit.toInt() > 0) {
                                    val category = CategoryModel(
                                        categoryName = categoryName,
                                        limit = categoryLimit.toInt(),
                                        icon = icon
                                    )
                                    viewModel.addCategory(category) { id ->
                                        val subCategory = SubCategoryModel(
                                            subCategoryName = "Diğer",
                                            category = id,
                                            limit = categoryLimit.toInt()
                                        )
                                        viewModel.addSubCategory(subCategory)
                                        onDissmiss()
                                    }

                                } else {
                                    Toast.makeText(context, "Lütfen boş alanları doldurunuz", Toast.LENGTH_SHORT).show()
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
                }
                Spacer(Modifier.height(8.dp))
                SpecialTextField(
                    value = categoryName,
                    placeHolder = "Örn: alışveriş, vergi, yemek..."
                ) { categoryName = it }

                Spacer(Modifier.height(12.dp))

                Text(
                    "Limit(TL)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(0.7f)
                )
                Spacer(Modifier.height(8.dp))
                SpecialTextField(
                    value = categoryLimit,
                    placeHolder = "1000"
                ) { categoryLimit = it }

                Spacer(Modifier.height(8.dp))

                Text(
                    "İkon",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(0.7f)
                )
                Spacer(Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(iconList){ (iconName, iconRes) ->
                        Surface(
                            modifier = Modifier.clickable (
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ){
                                icon = iconName
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (icon == iconName) Color(0xff101828) else Color(0xFFF0F0F0),
                            contentColor = if (icon == iconName) Color.White else Color.Black
                        ) {
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun AddSpendingCard(viewModel: HomeViewModel, onDissmiss: () -> Unit) {

    val calendar = Calendar.getInstance().timeInMillis

    val categoryList = viewModel.categoryList.collectAsState().value

    var categoryName by remember { mutableStateOf("Seçiniz") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableLongStateOf(calendar) }
    var note by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.5f)),
        contentAlignment = Alignment.Center
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ){
                    IconButton(
                        onClick = {
                            if (categoryName != "Seçiniz" && amount.isNotBlank()){
                                val spending = SpendingModel(
                                    amount = amount.toInt(),
                                    createdDate = date,
                                    category = categoryList.find { it.categoryName == categoryName }!!.categoryId,
                                    note = note
                                )
                                viewModel.addSpending(spending)
                            }
                            onDissmiss()
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
                /*Category Picker*/
                SplitButtonLayout(
                    leadingButton = {
                        SplitButtonDefaults.LeadingButton(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff0d9488))
                        ) {
                            Text(categoryName, color = Color.White)
                        }
                    },
                    trailingButton = {
                        var checked by remember { mutableStateOf(false) }
                        val rotation by animateFloatAsState(targetValue = if (checked) 180f else 0f)

                        SplitButtonDefaults.TrailingButton(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff0d9488))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.graphicsLayer { rotationZ = rotation },
                                tint = Color.White
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        DropdownMenu(
                            expanded = checked,
                            onDismissRequest = { checked = false }
                        ) {
                            categoryList.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.categoryName) },
                                    onClick = {
                                        categoryName = category.categoryName
                                        checked = false
                                    },
                                    modifier = Modifier.heightIn(max = 80.dp)
                                )
                            }
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))

                /*Amount*/
                Text(
                    text = "Harcama Tutarı(TL)",
                    fontSize = 14.sp,
                    color = Color.Black.copy(0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(Modifier.height(4.dp))
                SpecialTextField(
                    value = amount,
                    placeHolder = "1000",
                    keyboardType = KeyboardType.Number
                ) { if(amount.length <= 8) amount = it }

                Spacer(Modifier.height(16.dp))

                /*Date Picker*/
                Text(
                    text = "Harcama Tarihi",
                    fontSize = 14.sp,
                    color = Color.Black.copy(0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(Modifier.height(4.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ){ showDatePicker = true },
                    color = Color(0xffE1E2EC),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
                    Text(
                        text = dateFormatter.format(date),
                        modifier = Modifier.padding(12.dp),
                        color = Color.Black
                    )
                }

                /*Not*/
                Text(
                    text = "Not",
                    fontSize = 14.sp,
                    color = Color.Black.copy(0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(Modifier.height(4.dp))
                SpecialTextField(
                    value = note,
                    placeHolder = "Not yazın...",
                    singleLine = false
                ) {
                    if (note.length <= 30){
                        note = it
                    }
                }
            }
        }

        if (showDatePicker){
            DatePickerSheet(
                selectedDate = date,
                onSelected = { date = it },
                onDissmiss = { showDatePicker = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSheet(
    selectedDate: Long,
    onSelected: (Long) -> Unit,
    onDissmiss: () -> Unit
) {
    //skipPartiallyExpanded -> tam olarak açılmasını sağlar.
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope() //animasyonlu kapatma işlemi için

    var selectedDate by remember { mutableStateOf(selectedDate) }
    val dateState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)

    ModalBottomSheet(
        onDismissRequest = onDissmiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DatePicker(
                state = dateState,
                title = {
                    Text(
                        text = "Tarih Seçiniz",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp)
                    )
                },
                headline = {
                    DatePickerDefaults.DatePickerHeadline(
                        selectedDateMillis = dateState.selectedDateMillis,
                        displayMode = dateState.displayMode,
                        dateFormatter = DatePickerDefaults.dateFormatter(),
                        modifier = Modifier.padding(start = 24.dp)
                    )
                },
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = Color(0xff101828),
                    todayContentColor = Color(0xff101828),
                    todayDateBorderColor = Color(0xff101828)
                )
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible){
                                onDissmiss()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(text = "İptal", color = Color(0xff101828).copy(0.7f))
                }
                Spacer(Modifier.width(4.dp))
                TextButton(
                    onClick = {
                        val date = dateState.selectedDateMillis
                        if (date != null) {
                            selectedDate = date
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onSelected(selectedDate)
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(text = "Tamam", color = Color(0xff101828))
                }
            }
        }

    }
}

@Composable
fun ShowCategoryCard(
    categoryModel: CategoryModel,
    subCategoryList: List<SubCategoryWithSpend>,
    spends: Int,
    bgColor1: Color,
    bgColor2: Color,
    iconColor: Color,
    navController: NavController
) {

    var expanded by remember { mutableStateOf(false) }
    val rotation = animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    val percent = if (categoryModel.limit > 0){
        ((spends.toFloat() * 100) / categoryModel.limit.toFloat()).toInt()
    } else {
        0
    }
    val subCategoryCount = subCategoryList.size
    val iconRes = when(categoryModel.icon){
        "shop_icon" -> R.drawable.shop_icon
        "money_icon" -> R.drawable.money_icon
        "home_icon" -> R.drawable.home_icon
        "food_icon" -> R.drawable.food_icon
        "belge_icon" -> R.drawable.belge_icon
        "book_icon" -> R.drawable.book_icon
        "coffee_icon" -> R.drawable.coffee_icon
        "car_icon" -> R.drawable.car_icon
        "exercise_icon" -> R.drawable.exercise_icon
        "game_icon" -> R.drawable.game_icon
        "pet_icon" -> R.drawable.pet_icon
        "tech_icon" -> R.drawable.tech_icon
        else -> R.drawable.shop_icon
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically){
                    Surface(
                        modifier = Modifier.background(
                            brush = Brush.horizontalGradient(colors = listOf(bgColor1, bgColor2)),
                            shape = RoundedCornerShape(12.dp)
                        ),
                        color = Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))

                    Column{
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = categoryModel.categoryName,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .background(color = Color(0xffE1E2EC), shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = subCategoryCount.toString(),
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                        modifier = Modifier.fillMaxSize(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.background(
                                        color = iconColor,
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                ) {
                                    Text(
                                        text = "%${percent}",
                                        color = bgColor1,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                IconButton(
                                    onClick = { expanded = !expanded },
                                    shape = CircleShape,
                                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xffE1E2EC)),
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier.graphicsLayer { rotationZ = rotation.value }
                                    )
                                }
                            }
                        }

                        Row {
                            Text(text = "Limit:", fontSize = 12.sp, color = Color.Black.copy(0.7f))
                            Spacer(Modifier.width(2.dp))
                            Text(text = "${categoryModel.limit} ₺")

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = "Harcanan:",
                                color = Color.Black.copy(0.7f)
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(text = "$spends ₺", fontWeight = FontWeight.SemiBold, color = bgColor2)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            MoneyProgressBar(percent = percent, color1 = bgColor1, color2 = bgColor2)

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    subCategoryList.forEach { subCategoryWithAmount ->
                        val subCategory = subCategoryWithAmount.subCategory
                        val amount = subCategoryWithAmount.totalAmount

                        val subPercent = if (categoryModel.limit > 0){
                            ((amount.toFloat() * 100) / subCategory.limit.toFloat()).toInt()
                        } else {
                            0
                        }

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = subCategory.subCategoryName,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )

                                Box(
                                    modifier = Modifier.background(
                                        color = iconColor,
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                ) {
                                    Text(
                                        text = "%${subPercent}",
                                        color = bgColor1,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Row {
                                Text(text = "Limit:", fontSize = 12.sp, color = Color.Black.copy(0.7f))
                                Spacer(Modifier.width(2.dp))
                                Text(text = "${subCategory.limit} ₺")

                                Spacer(Modifier.width(8.dp))

                                Text(
                                    text = "Harcanan:",
                                    color = Color.Black.copy(0.7f)
                                )
                                Spacer(Modifier.width(2.dp))
                                Text(text = "$amount ₺", fontWeight = FontWeight.SemiBold, color = bgColor2)
                            }
                            Spacer(Modifier.height(8.dp))
                            MoneyProgressBar(percent = subPercent, color1 = bgColor1, color2 = bgColor2)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpecialTextField(
    value: String,
    placeHolder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        placeholder = { Text(placeHolder) },
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else 3,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xffE1E2EC),
            focusedContainerColor = Color(0xffE1E2EC),
            focusedIndicatorColor = Color(0xff101828),
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun DatePickerCard(
    year: Int,
    month: Int,
    onDissmiss: () -> Unit,
    onValueChange: (selectedYear: Int, selectedMonth: Int) -> Unit
) {
    var selectedYear by remember { mutableStateOf(year) }
    var selectedMonth by remember { mutableStateOf(month) }


    val years = listOf(2025, 2026, 2027, 2028, 2029, 2030, 2031, 2032, 2033, 2034, 2035)
    val months = listOf(
        "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Yıl Seç", fontSize = 14.sp, color = Color.Black.copy(0.7f))
                Spacer(Modifier.height(4.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(years){ year ->
                        Surface(
                            modifier = Modifier.clickable (
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ){ selectedYear = year },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedYear == year) Color(0xff101828) else Color(0xFFF0F0F0),
                            contentColor = if (selectedYear == year) Color.White else Color.Black
                        ) {
                            Text(
                                text = year.toString(),
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text("Ay Seç", fontSize = 14.sp, color = Color.Black.copy(0.7f))
                Spacer(Modifier.height(4.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(months){ month ->
                        Surface(
                            modifier = Modifier.clickable (
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ){ selectedMonth = months.indexOf(month) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedMonth == months.indexOf(month)) Color(0xff101828) else Color(0xFFF0F0F0),
                            contentColor = if (selectedMonth == months.indexOf(month)) Color.White else Color.Black
                        ) {
                            Text(
                                text = month,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDissmiss() }) {
                        Text("İptal", color = Color(0xff101828).copy(0.7f))
                    }
                    Spacer(Modifier.width(6.dp))
                    TextButton(
                        onClick = {
                            onDissmiss()
                            onValueChange(selectedYear, selectedMonth)
                          }
                    ) {
                        Text("Tamam", color = Color(0xff101828))
                    }
                }
            }
        }
    }

}
@Composable
fun TotalCard(
    year: Int,
    month: Int,
    limit: Int,
    amount: Int,
    onDissmiss: () -> Unit
) {
    val months = listOf(
        "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
    )
    val percent = if (limit == 0) 0 else ((amount.toFloat() / limit.toFloat()) * 100f).toInt()

    /*var checked by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (checked) 180f else 0f,
        label = "rotationAnimation"
    )*/

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(brush = Brush.horizontalGradient(
                colors = listOf(Color(0xff1e293b), Color(0xff0f172a),Color(0xff111827))),
                shape = RoundedCornerShape(16.dp)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Surface(
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onDissmiss() },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF334155)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${months[month]} $year", color = Color.White)
                                Spacer(Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Text(
                        text = "Toplam",
                        color = Color.White,
                        fontSize = 14.sp,
                    )
                }

                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xff334155)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Harcanan",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "$amount ₺",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xff334155)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Limit",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "$limit ₺",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }


                }

                Spacer(Modifier.height(18.dp))

                MoneyProgressBar(percent = percent, color1 = Color.White, color2 = Color.White, bgColor = Color.White.copy(0.2f))
                Text(
                    text = "%${percent}",
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}


@Composable
fun MoneyProgressBar(
    color1: Color,
    color2: Color,
    bgColor: Color = Color(0xffE1E2EC),
    percent: Int
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = (percent / 100f).coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(32.dp))
                    .background(brush = Brush.horizontalGradient(colors = listOf(color1, color2)), shape = RoundedCornerShape(32.dp))
                //coerceIn -> min-max aralığını belirtir
            )
        }
    }
}