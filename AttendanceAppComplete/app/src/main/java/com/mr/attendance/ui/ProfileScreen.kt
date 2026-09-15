package com.mr.attendance.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mr.attendance.domain.PersonnelType

@Composable
fun ProfileScreen(vm: MainViewModel, onBack: () -> Unit) {
    val p by vm.personnel.collectAsState(); var name by remember(p.name){mutableStateOf(p.name)}; var job by remember(p.jobPosition){mutableStateOf(p.jobPosition)}; var type by remember(p.personnelType){mutableStateOf(PersonnelType.from(p.personnelType))}; var rate by remember(p.stadiOvertimeRate){mutableStateOf(if(p.stadiOvertimeRate==0L) "" else p.stadiOvertimeRate.toString())}
    Column(Modifier.fillMaxSize().padding(16.dp)){
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){TextButton(onClick=onBack){Text("بازگشت")};Text("پروفایل",style=MaterialTheme.typography.titleLarge)}
        OutlinedTextField(name,{name=it},label={Text("نام و نام خانوادگی")},modifier=Modifier.fillMaxWidth());Spacer(Modifier.height(8.dp));OutlinedTextField(job,{job=it},label={Text("سمت شغلی")},modifier=Modifier.fillMaxWidth());Spacer(Modifier.height(10.dp));Text("نوع پرسنل");Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)){PersonnelType.entries.forEach{t->FilterChip(selected=t==type,onClick={type=t},label={Text(t.title)})}}
        if(type==PersonnelType.STADI){Spacer(Modifier.height(10.dp));OutlinedTextField(rate,{rate=it.filter(Char::isDigit)},label={Text("مبلغ ساعتی اضافه کار (ریال)")},modifier=Modifier.fillMaxWidth())}
        Spacer(Modifier.height(18.dp));Button(onClick={vm.savePersonnel(name,job,type.name,rate.toLongOrNull()?:0L)},modifier=Modifier.fillMaxWidth()){Text("ذخیره اطلاعات")}
    }
}
