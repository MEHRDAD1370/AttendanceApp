package com.mr.attendance.util

object TimeFormatter {
    fun minutes(value: Long): String = "%02d:%02d".format(value / 60, value % 60)
    fun money(value: Long): String = "%,d ریال".format(value)
}
