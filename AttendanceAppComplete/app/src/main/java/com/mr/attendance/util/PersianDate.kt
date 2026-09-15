package com.mr.attendance.util

import java.time.LocalDate
import java.time.DayOfWeek

/** Lightweight Jalali/Gregorian conversion; no external calendar library required. */
data class JalaliDate(val year: Int, val month: Int, val day: Int) {
    fun monthName(): String = PersianDate.monthNames[month - 1]
    override fun toString(): String = "%04d/%02d/%02d".format(year, month, day)
}

object PersianDate {
    val monthNames = listOf("فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند")
    val weekNames = listOf("شنبه","یکشنبه","دوشنبه","سه‌شنبه","چهارشنبه","پنجشنبه","جمعه")

    fun today(): JalaliDate = fromGregorian(LocalDate.now())
    fun format(date: LocalDate): String = fromGregorian(date).toString()
    fun toGregorian(jy: Int, jm: Int, jd: Int): LocalDate {
        var y = jy + 1595
        var days = -355668 + 365 * y + (y / 33) * 8 + ((y % 33 + 3) / 4) + jd + if (jm < 7) (jm - 1) * 31 else (jm - 1) * 30 + 6
        var gy = 400 * (days / 146097)
        days %= 146097
        if (days > 36524) {
            gy += 100 * (--days / 36524)
            days %= 36524
            if (days >= 365) days++
        }
        gy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            gy += (days - 1) / 365
            days = (days - 1) % 365
        }
        val gd = days + 1
        val leap = gy % 4 == 0 && (gy % 100 != 0 || gy % 400 == 0)
        val md = intArrayOf(31, if (leap) 29 else 28,31,30,31,30,31,31,30,31,30,31)
        var gm = 1
        var d = gd
        while (d > md[gm - 1]) { d -= md[gm - 1]; gm++ }
        return LocalDate.of(gy, gm, d)
    }

    fun fromGregorian(date: LocalDate): JalaliDate {
        var gy = date.year - 1600
        val gm = date.monthValue - 1
        val gd = date.dayOfMonth - 1
        val gDayNo = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400
        val gDaysInMonth = intArrayOf(31,28,31,30,31,30,31,31,30,31,30,31)
        var gDay = gDayNo
        for (i in 0 until gm) gDay += gDaysInMonth[i]
        gDay += gd
        var jDay = gDay - 80
        var jy = 979 + 33 * (jDay / 12053)
        jDay %= 12053
        jy += 4 * (jDay / 1461)
        jDay %= 1461
        if (jDay > 365) { jy += (jDay - 1) / 365; jDay = (jDay - 1) % 365 }
        val jm: Int
        val jd: Int
        if (jDay < 186) { jm = 1 + jDay / 31; jd = 1 + jDay % 31 }
        else { jm = 7 + (jDay - 186) / 30; jd = 1 + (jDay - 186) % 30 }
        return JalaliDate(jy, jm, jd)
    }

    fun daysInMonth(year: Int, month: Int): Int = when {
        month <= 6 -> 31
        month <= 11 -> 30
        isLeapYear(year) -> 30
        else -> 29
    }

    fun isLeapYear(year: Int): Boolean = runCatching { fromGregorian(toGregorian(year,12,30)) == JalaliDate(year,12,30) }.getOrDefault(false)
    fun dayOfWeek(year: Int, month: Int, day: Int): DayOfWeek = toGregorian(year,month,day).dayOfWeek
    fun weekIndexFromSaturday(dayOfWeek: DayOfWeek): Int = (dayOfWeek.value % 7) // Sunday=0 ... Saturday=6
}
