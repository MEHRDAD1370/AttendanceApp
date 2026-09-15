package com.mr.attendance.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mr.attendance.domain.*
import com.mr.attendance.util.PersianDate
import java.time.LocalDate
import java.time.YearMonth
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun RecordsScreen(vm: MainViewModel, onBack:()->Unit, onEdit:(AttendanceRecord)->Unit){
    val records by vm.records.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp)){ Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){TextButton(onClick=onBack){Text("بازگشت")};Text("سوابق ماه جاری",style=MaterialTheme.typography.titleLarge)}}
    LazyColumn{items(records, key={it.id}){r->Card(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=4.dp).clickable{onEdit(r)}){Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.SpaceBetween){Column{Text(PersianDate.format(r.date));Text(r.shift?.title ?: r.status.name)};Text("${r.entryTime ?: "-"} ← ${r.exitTime ?: "-"}")}}}}
}

@Composable
fun EditAttendanceScreen(vm: MainViewModel, record: AttendanceRecord, onBack:()->Unit){
    var entry by remember{mutableStateOf(record.entryTime?.format(DateTimeFormatter.ofPattern("HH:mm"))?:(""))}; var exit by remember{mutableStateOf(record.exitTime?.format(DateTimeFormatter.ofPattern("HH:mm"))?:(""))}; var shift by remember{mutableStateOf(record.shift?:Shift.A)}; var status by remember{mutableStateOf(record.status)}; var note by remember{mutableStateOf(record.note?:"")}
    val parse: (String)->LocalTime? = { s->runCatching{LocalTime.parse(s,DateTimeFormatter.ofPattern("HH:mm"))}.getOrNull() }
    Column(Modifier.fillMaxSize().padding(16.dp)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){TextButton(onClick=onBack){Text("بازگشت")};Text("ویرایش رکورد",style=MaterialTheme.typography.titleLarge)};Text("تاریخ: ${PersianDate.format(record.date)}");Spacer(Modifier.height(8.dp));Text("شیفت");Row{Shift.entries.forEach{FilterChip(selected=it==shift,onClick={shift=it},label={Text(it.code)},modifier=Modifier.padding(end=6.dp))}};Spacer(Modifier.height(8.dp));OutlinedTextField(entry,{entry=it},label={Text("ورود HH:mm")},modifier=Modifier.fillMaxWidth());OutlinedTextField(exit,{exit=it},label={Text("خروج HH:mm")},modifier=Modifier.fillMaxWidth());Spacer(Modifier.height(8.dp));Text("وضعیت");Row{AttendanceStatus.entries.forEach{FilterChip(selected=it==status,onClick={status=it},label={Text(when(it){AttendanceStatus.PRESENT->"حضور";AttendanceStatus.LEAVE->"مرخصی";AttendanceStatus.OFF->"آف"})},modifier=Modifier.padding(end=6.dp))}};OutlinedTextField(note,{note=it},label={Text("یادداشت")},modifier=Modifier.fillMaxWidth());Spacer(Modifier.height(12.dp));Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick={vm.saveRecord(record.copy(shift=shift,status=status,entryTime=parse(entry),exitTime=parse(exit),note=note.ifBlank{null}));onBack()},modifier=Modifier.weight(1f)){Text("ذخیره")};OutlinedButton(onClick={vm.deleteRecord(record);onBack()},modifier=Modifier.weight(1f)){Text("حذف")}}
    }
}
