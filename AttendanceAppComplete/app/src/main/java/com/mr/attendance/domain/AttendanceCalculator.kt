package com.mr.attendance.domain

import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

object AttendanceCalculator {
    fun calculate(record: AttendanceRecord, overtimeRatePerHour: Long): DailyCalculation {
        if (record.status != AttendanceStatus.PRESENT || record.entryTime == null || record.exitTime == null) {
            return DailyCalculation(status = record.status)
        }
        val shift = record.shift ?: return DailyCalculation(status = AttendanceStatus.PRESENT)
        val entry = record.entryTime
        val exit = record.exitTime
        val holiday = isFriday(record.date) || record.isOfficialHoliday
        val presence = duration(entry, exit)
        val late = if (holiday) 0 else lateMinutes(shift, entry)
        val early = if (holiday) 0 else earlyMinutes(shift, exit)
        val shortage = if (holiday) 0 else late + early
        val overtime = if (holiday) presence else calculateExcelLikeOvertime(shift, entry, exit)
        return DailyCalculation(
            status = AttendanceStatus.PRESENT,
            presenceMinutes = presence,
            lateMinutes = late,
            earlyDepartureMinutes = early,
            shortageMinutes = shortage,
            overtimeMinutes = overtime,
            overtimePay = (overtime * overtimeRatePerHour) / 60L
        )
    }

    private fun isFriday(date: LocalDate) = date.dayOfWeek.value == 5

    private fun duration(entry: LocalTime, exit: LocalTime): Long {
        var m = ChronoUnit.MINUTES.between(entry, exit)
        if (m < 0) m += 24 * 60
        return m
    }

    private fun mins(t: LocalTime) = t.hour * 60L + t.minute

    private fun lateMinutes(shift: Shift, entry: LocalTime): Long = (mins(entry) - mins(LocalTime.of(shift.startHour, shift.startMinute))).coerceAtLeast(0)

    private fun earlyMinutes(shift: Shift, exit: LocalTime): Long {
        val end = shift.endHour * 60 + shift.endMinute
        var x = exit.hour * 60 + exit.minute
        if (shift == Shift.C && x < 12 * 60) x += 24 * 60
        val e = if (shift == Shift.C) end + 24 * 60 else end
        return (e - x).coerceAtLeast(0)
    }

    // Matches the Excel workbook logic discovered in Shift Indicator.xlsm.
    private fun calculateExcelLikeOvertime(shift: Shift, entry: LocalTime, exit: LocalTime): Long {
        val en = mins(entry)
        val ex0 = mins(exit)
        val ex = if (shift == Shift.C && ex0 < 12 * 60) ex0 + 24 * 60 else ex0
        return when (shift) {
            Shift.A -> (mins(LocalTime.of(7, 0)) - en).coerceAtLeast(0) + (ex0 - mins(LocalTime.of(15, 30))).coerceAtLeast(0)
            Shift.B -> {
                val early = (mins(LocalTime.of(15, 0)) - en).coerceAtLeast(0)
                val late = (ex0 - mins(LocalTime.of(23, 30))).coerceAtLeast(0)
                val special = if (ex0 < 23 * 60 + 30 && ex0 < 4 * 60) 30L else 0L
                early + late + special
            }
            Shift.C -> {
                val start = mins(LocalTime.of(23, 0))
                val end = 24 * 60 + mins(LocalTime.of(7, 30))
                (start - en).coerceAtLeast(0) + (ex - end).coerceAtLeast(0)
            }
        }
    }
}
