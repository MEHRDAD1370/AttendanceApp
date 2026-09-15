package com.mr.attendance.domain

enum class PersonnelType(val title: String, val defaultOvertimeRate: Long?) {
    STADI("ستادی", null),
    SARPARAST("سرپرست", 1_760_000L),
    MASOOL("مسئول", 1_740_000L),
    PERSONNEL("پرسنل", 1_720_000L);

    companion object { fun from(value: String?) = entries.firstOrNull { it.name == value } ?: PERSONNEL }
}

enum class AttendanceStatus { PRESENT, LEAVE, OFF }
