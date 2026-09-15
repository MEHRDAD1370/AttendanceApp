package com.mr.attendance.domain

data class MonthlyReport(
    val totalPresenceMinutes: Long = 0,
    val totalOvertimeMinutes: Long = 0,
    val totalShortageMinutes: Long = 0,
    val totalLateMinutes: Long = 0,
    val totalEarlyDepartureMinutes: Long = 0,
    val totalOvertimePay: Long = 0,
    val leaveCount: Int = 0,
    val offCount: Int = 0,
    val entryCount: Int = 0,
    val exitCount: Int = 0,
    val shiftACount: Int = 0,
    val shiftBCount: Int = 0,
    val shiftCCount: Int = 0,
    val workingDays: Int = 0
)
