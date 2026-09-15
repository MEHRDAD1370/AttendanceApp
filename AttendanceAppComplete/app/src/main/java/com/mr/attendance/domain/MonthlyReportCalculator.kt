package com.mr.attendance.domain

import java.time.YearMonth

object MonthlyReportCalculator {
    fun calculate(records: List<AttendanceRecord>, rate: Long, month: YearMonth): MonthlyReport {
        val inMonth = records.filter { YearMonth.from(it.date) == month }
        var result = MonthlyReport()
        var p=0L; var ot=0L; var sh=0L; var late=0L; var early=0L; var pay=0L
        var leave=0; var off=0; var ent=0; var ex=0; var a=0; var b=0; var c=0
        inMonth.forEach { r ->
            if (r.entryTime != null) ent++
            if (r.exitTime != null) ex++
            when (r.status) { AttendanceStatus.LEAVE -> leave++; AttendanceStatus.OFF -> off++; AttendanceStatus.PRESENT -> Unit }
            when(r.shift){ Shift.A -> a++; Shift.B -> b++; Shift.C -> c++; null -> Unit }
            val d = AttendanceCalculator.calculate(r, rate)
            p += d.presenceMinutes; ot += d.overtimeMinutes; sh += d.shortageMinutes; late += d.lateMinutes; early += d.earlyDepartureMinutes; pay += d.overtimePay
        }
        result = MonthlyReport(p,ot,sh,late,early,pay,leave,off,ent,ex,a,b,c,inMonth.count { it.status == AttendanceStatus.PRESENT })
        return result
    }
}
