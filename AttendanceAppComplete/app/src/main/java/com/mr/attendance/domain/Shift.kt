package com.mr.attendance.domain

enum class Shift(val code: String, val title: String, val startHour: Int, val startMinute: Int, val endHour: Int, val endMinute: Int) {
    A("A", "شیفت صبح", 7, 0, 15, 30),
    B("B", "شیفت عصر", 15, 0, 23, 30),
    C("C", "شیفت شب", 23, 0, 7, 30);

    companion object {
        fun fromCode(code: String?): Shift? = entries.firstOrNull { it.code.equals(code, true) }
    }
}
