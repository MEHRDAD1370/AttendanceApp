package com.mr.attendance.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mr.attendance.domain.*
import com.mr.attendance.util.TimeFormatter
import com.mr.attendance.util.PersianDate
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun ReportScreen(vm: MainViewModel, onBack: () -> Unit) {
    var month by remember { mutableStateOf(YearMonth.from(LocalDate.now())) }
    val records by vm.records.collectAsState(); val p by vm.personnel.collectAsState()
    LaunchedEffect(month) { vm.reloadMonth(month) }
    val type = PersonnelType.from(p.personnelType); val rate = if(type == PersonnelType.STADI) p.stadiOvertimeRate else type.defaultOvertimeRate ?: 0L
    val report = MonthlyReportCalculator.calculate(records, rate, month)
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){TextButton(onClick=onBack){Text("بازگشت")};Text("گزارش ماهانه",style=MaterialTheme.typography.titleLarge);TextButton(onClick={month=month.plusMonths(1)}){Text("ماه بعد")}}
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){TextButton(onClick={month=month.minusMonths(1)}){Text("ماه قبل")};Text("${PersianDate.monthNames[month.monthValue-1]} ${PersianDate.fromGregorian(month.atDay(1)).year}",style=MaterialTheme.typography.titleMedium)}
        Spacer(Modifier.height(10.dp))
        ReportItem("جمع کل حضور",TimeFormatter.minutes(report.totalPresenceMinutes)); ReportItem("جمع اضافه کار",TimeFormatter.minutes(report.totalOvertimeMinutes)); ReportItem("جمع کسر کار",TimeFormatter.minutes(report.totalShortageMinutes)); ReportItem("مجموع تأخیر",TimeFormatter.minutes(report.totalLateMinutes)); ReportItem("مجموع تعجیل در خروج",TimeFormatter.minutes(report.totalEarlyDepartureMinutes)); ReportItem("دریافتی اضافه کار",TimeFormatter.money(report.totalOvertimePay)); ReportItem("مرخصی / آف","${report.leaveCount} / ${report.offCount}"); ReportItem("تعداد ورود / خروج","${report.entryCount} / ${report.exitCount}"); ReportItem("شیفت A / B / C","${report.shiftACount} / ${report.shiftBCount} / ${report.shiftCCount}")
    }
}
@Composable private fun ReportItem(title:String,value:String){Card(Modifier.fillMaxWidth().padding(vertical=3.dp)){Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.SpaceBetween){Text(title);Text(value,style=MaterialTheme.typography.titleMedium)}}}
