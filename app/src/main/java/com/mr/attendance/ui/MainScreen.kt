package com.mr.attendance.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mr.attendance.domain.Shift
import com.mr.attendance.util.PersianDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun MainScreen(vm: MainViewModel, go: (String) -> Unit) {
    val p by vm.personnel.collectAsState(); val today by vm.today.collectAsState(); val shift by vm.selectedShift.collectAsState(); val msg by vm.message.collectAsState()
    LaunchedEffect(Unit) { while(true) { kotlinx.coroutines.delay(15_000); vm.refresh() } }
    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment=Alignment.CenterHorizontally) {
        Text("حضور و غیاب", style=MaterialTheme.typography.headlineMedium)
        Text(if(p.name.isBlank()) "نام شخص ثبت نشده" else p.name, style=MaterialTheme.typography.titleMedium)
        Text(PersianDate.format(java.time.LocalDate.now()), style=MaterialTheme.typography.bodyLarge)
        Text(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), style=MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) {
            Text("شیفت امروز", style=MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)) { Shift.entries.forEach { s -> FilterChip(selected=s==shift, onClick={vm.selectShift(s)}, label={Text(s.title)}) } }
        }}
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            Button(onClick={vm.registerEntry()}, modifier=Modifier.weight(1f)) { Text("ثبت ورود") }
            Button(onClick={vm.registerExit()}, modifier=Modifier.weight(1f)) { Text("ثبت خروج") }
        }
        Spacer(Modifier.height(12.dp))
        val status = when { today == null -> "برای امروز رکوردی ثبت نشده"; today!!.entryTime != null && today!!.exitTime == null -> "ورود ثبت شده: ${today!!.entryTime}"; else -> "ورود: ${today!!.entryTime ?: "-"} | خروج: ${today!!.exitTime ?: "-"}" }
        Text(status, textAlign=TextAlign.Center)
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)) { OutlinedButton(onClick={go("calendar")},Modifier.weight(1f)){Text("تقویم")}; OutlinedButton(onClick={go("report")},Modifier.weight(1f)){Text("گزارش ماه")}; OutlinedButton(onClick={go("records")},Modifier.weight(1f)){Text("سوابق")}; OutlinedButton(onClick={go("profile")},Modifier.weight(1f)){Text("پروفایل")} }
        Spacer(Modifier.height(8.dp)); TextButton(onClick={go("about")}) { Text("درباره برنامه") }
    }
    if(msg!=null) AlertDialog(onDismissRequest={vm.clearMessage()}, confirmButton={Button(onClick={vm.clearMessage()}){Text("باشه")}}, title={Text("پیام")}, text={Text(msg!!)})
}
