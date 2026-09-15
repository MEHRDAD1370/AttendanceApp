package com.mr.attendance.domain

import java.time.LocalDate
import java.time.LocalTime

data class AttendanceRecord(
    val id: Long = 0,
    val date: LocalDate,
    val shift: Shift?,
    val entryTime: LocalTime? = null,
    val exitTime: LocalTime? = null,
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val isOfficialHoliday: Boolean = false,
    val holidayName: String? = null,
    val note: String? = null
)
