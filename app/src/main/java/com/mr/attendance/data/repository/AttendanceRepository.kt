package com.mr.attendance.data.repository

import com.mr.attendance.data.db.*
import com.mr.attendance.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.YearMonth

private val DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE
private val TIME_FMT = DateTimeFormatter.ofPattern("HH:mm")

class AttendanceRepository(private val attendanceDao: AttendanceDao, private val personnelDao: PersonnelDao) {
    suspend fun today(date: LocalDate) = withContext(Dispatchers.IO) { attendanceDao.getByDate(date.toString())?.toDomain() }
    suspend fun month(month: YearMonth): List<AttendanceRecord> = withContext(Dispatchers.IO) { attendanceDao.getMonth(month.toString()).map { it.toDomain() } }
    suspend fun all(): List<AttendanceRecord> = withContext(Dispatchers.IO) { attendanceDao.getAll().map { it.toDomain() } }
    suspend fun save(record: AttendanceRecord) = withContext(Dispatchers.IO) {
        val e = record.toEntity()
        if (record.id == 0L) attendanceDao.insert(e) else attendanceDao.update(e)
    }
    suspend fun delete(record: AttendanceRecord) = withContext(Dispatchers.IO) { attendanceDao.delete(record.toEntity()) }
    suspend fun personnel(): PersonnelEntity? = withContext(Dispatchers.IO) { personnelDao.get() }
    suspend fun savePersonnel(personnel: PersonnelEntity) = withContext(Dispatchers.IO) { personnelDao.save(personnel) }

    private fun AttendanceEntity.toDomain() = AttendanceRecord(id, LocalDate.parse(date, DATE_FMT), Shift.fromCode(shift), entryTime?.let { LocalTime.parse(it, TIME_FMT) }, exitTime?.let { LocalTime.parse(it, TIME_FMT) }, runCatching { AttendanceStatus.valueOf(status) }.getOrDefault(AttendanceStatus.PRESENT), isOfficialHoliday, holidayName, note)
    private fun AttendanceRecord.toEntity() = AttendanceEntity(id, date.toString(), shift?.code, entryTime?.format(TIME_FMT), exitTime?.format(TIME_FMT), status.name, isOfficialHoliday, holidayName, note)
}
