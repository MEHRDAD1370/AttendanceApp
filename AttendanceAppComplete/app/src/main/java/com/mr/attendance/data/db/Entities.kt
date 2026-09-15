package com.mr.attendance.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personnel")
data class PersonnelEntity(
    @PrimaryKey val id: Long = 1,
    val name: String = "",
    val jobPosition: String = "",
    val personnelType: String = "PERSONNEL",
    val stadiOvertimeRate: Long = 0
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val shift: String?,
    val entryTime: String?,
    val exitTime: String?,
    val status: String,
    val isOfficialHoliday: Boolean,
    val holidayName: String?,
    val note: String?
)

@Entity(tableName = "holidays", primaryKeys = ["date"])
data class HolidayEntity(
    val date: String,
    val title: String,
    val isHoliday: Boolean,
    val updatedAt: Long
)
