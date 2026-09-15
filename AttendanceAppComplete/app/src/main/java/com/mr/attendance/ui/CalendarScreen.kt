package com.mr.attendance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mr.attendance.util.PersianDate
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(vm: MainViewModel, onBack: () -> Unit) {
    var jy by remember { mutableIntStateOf(PersianDate.today().year) }
    var jm by remember { mutableIntStateOf(PersianDate.today().month) }
    val holidays by vm.holidayYear.collectAsState()
    LaunchedEffect(jy) { vm.syncHolidayYear(jy) }
    LaunchedEffect(jy, jm) { vm.reloadMonth(YearMonth.from(PersianDate.toGregorian(jy, jm, 1))) }
    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onBack) { Text("بازگشت") }
            Text("تقویم شمسی", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { jm--; if(jm==0){jm=12;jy--} }) { Text("ماه قبل") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { jm++; if(jm==13){jm=1;jy++} }) { Text("›", style=MaterialTheme.typography.headlineMedium) }
            Text("${PersianDate.monthNames[jm-1]} $jy", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { jm--; if(jm==0){jm=12;jy--} }) { Text("‹", style=MaterialTheme.typography.headlineMedium) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PersianDate.weekNames.forEach { Text(it, modifier=Modifier.weight(1f), textAlign=androidx.compose.ui.text.style.TextAlign.Center, style=MaterialTheme.typography.labelSmall) }
        }
        Spacer(Modifier.height(6.dp))
        val first = PersianDate.weekIndexFromSaturday(PersianDate.dayOfWeek(jy,jm,1))
        val count = PersianDate.daysInMonth(jy,jm)
        val cells = buildList<Any?> { repeat(first) { add(null) }; for(d in 1..count) add(d) }
        LazyVerticalGrid(columns=GridCells.Fixed(7), modifier=Modifier.fillMaxWidth().weight(1f)) {
            items(cells.size) { idx ->
                val d = cells[idx] as Int?
                if(d == null) Box(Modifier.aspectRatio(1f).padding(2.dp))
                else {
                    val isFriday = PersianDate.dayOfWeek(jy,jm,d).value == 5
                    val key = "%04d/%02d/%02d".format(jy,jm,d)
                    val holiday = holidays.firstOrNull { it.date == key }
                    Box(Modifier.aspectRatio(1f).padding(2.dp).background(if(isFriday || holiday != null) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant).clickable { }) {
                        Column(Modifier.fillMaxSize(), horizontalAlignment=Alignment.CenterHorizontally, verticalArrangement=Arrangement.Center) { Text(d.toString()); if(holiday!=null) Text("تعطیل", style=MaterialTheme.typography.labelSmall) }
                    }
                }
            }
        }
    }
}
