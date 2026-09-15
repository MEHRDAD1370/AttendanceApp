package com.mr.attendance.data.repository

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mr.attendance.data.db.HolidayDao
import com.mr.attendance.data.db.HolidayEntity
import com.mr.attendance.data.network.HolidayApi
import com.mr.attendance.util.PersianDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HolidayRepository(private val dao: HolidayDao, private val api: HolidayApi) {
    suspend fun syncYear(year: Int): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val json = api.getCalendar(year)
            val items = parse(json, year).distinctBy { it.date + "|" + it.title }
            if (items.isNotEmpty()) dao.upsertAll(items)
            items.size
        }
    }

    suspend fun localYear(year: Int): List<HolidayEntity> = withContext(Dispatchers.IO) { dao.forYear("$year/") }
    suspend fun localDate(date: String): HolidayEntity? = withContext(Dispatchers.IO) { dao.find(date) }

    private fun parse(root: JsonElement, year: Int): List<HolidayEntity> {
        val out = mutableListOf<HolidayEntity>()
        fun walk(el: JsonElement) {
            if (el.isJsonArray) { el.asJsonArray.forEach(::walk); return }
            if (!el.isJsonObject) return
            val o = el.asJsonObject
            extract(o, year)?.let { out += it }
            o.entrySet().forEach { (_, v) -> if (v.isJsonObject || v.isJsonArray) walk(v) }
        }
        walk(root)
        return out.filter { it.isHoliday }
    }

    private fun extract(o: JsonObject, defaultYear: Int): HolidayEntity? {
        var y = int(o,"year") ?: intObject(o,"jalali_date","year") ?: defaultYear
        val m = int(o,"month") ?: intObject(o,"jalali_date","month")
        val d = int(o,"day") ?: intObject(o,"jalali_date","day")
        val dayId = str(o,"day_id")?.takeIf { it.length >= 8 && it.all(Char::isDigit) }
        if (m == null || d == null) {
            if (dayId != null) { y = dayId.substring(0,4).toIntOrNull() ?: y }
            else return null
        }
        val mm = m ?: dayId!!.substring(4,6).toInt()
        val dd = d ?: dayId!!.substring(6,8).toInt()
        if (mm !in 1..12 || dd !in 1..31) return null
        val isHoliday = bool(o,"is_holiday") ?: bool(o,"holiday") ?: boolObject(o,"events","is_holiday") ?: false
        if (!isHoliday) return null
        val title = str(o,"event") ?: str(o,"title") ?: str(o,"description") ?: str(o,"name") ?: "تعطیل رسمی"
        return HolidayEntity("%04d/%02d/%02d".format(y,mm,dd), title, true, System.currentTimeMillis())
    }

    private fun str(o: JsonObject, key: String) = o.get(key)?.takeIf { it.isJsonPrimitive }?.asString
    private fun int(o: JsonObject, key: String) = o.get(key)?.takeIf { it.isJsonPrimitive }?.asString?.toIntOrNull()
    private fun bool(o: JsonObject, key: String) = o.get(key)?.takeIf { it.isJsonPrimitive }?.let { runCatching { it.asBoolean }.getOrNull() }
    private fun intObject(o: JsonObject, parent: String, key: String) = o.get(parent)?.takeIf { it.isJsonObject }?.asJsonObject?.get(key)?.asInt
    private fun boolObject(o: JsonObject, parent: String, key: String) = o.get(parent)?.takeIf { it.isJsonObject }?.asJsonObject?.get(key)?.asBoolean
}
