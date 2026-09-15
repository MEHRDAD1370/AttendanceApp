package com.mr.attendance.domain

data class DailyCalculation(
    val status: AttendanceStatus,
    val presenceMinutes: Long = 0,
    val lateMinutes: Long = 0,
    val earlyDepartureMinutes: Long = 0,
    val shortageMinutes: Long = 0,
    val overtimeMinutes: Long = 0,
    val overtimePay: Long = 0
)
