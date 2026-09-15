package com.mr.attendance.domain

import com.mr.attendance.data.repository.AttendanceRepository
import com.mr.attendance.util.PersianDate
import java.time.LocalDate
import java.time.LocalTime

class AttendanceManager(private val repo: AttendanceRepository) {
    suspend fun registerEntry(date: LocalDate, shift: Shift): Result<Unit> = runCatching {
        val old = repo.today(date)
        check(old?.entryTime == null) { "ورود این روز قبلاً ثبت شده است." }
        val holiday = date.dayOfWeek.value == 5
        val record = (old ?: AttendanceRecord(date = date, shift = shift, status = AttendanceStatus.PRESENT, isOfficialHoliday = holiday)).copy(shift = shift, entryTime = LocalTime.now(), status = AttendanceStatus.PRESENT)
        repo.save(record)
    }
    suspend fun registerExit(date: LocalDate): Result<Unit> = runCatching {
        val old = repo.today(date) ?: error("ابتدا ورود را ثبت کنید.")
        check(old.entryTime != null) { "ابتدا ورود را ثبت کنید." }
        check(old.exitTime == null) { "خروج این روز قبلاً ثبت شده است." }
        repo.save(old.copy(exitTime = LocalTime.now()))
    }
}
