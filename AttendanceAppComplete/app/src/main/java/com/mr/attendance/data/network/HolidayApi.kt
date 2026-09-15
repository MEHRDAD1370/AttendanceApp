package com.mr.attendance.data.network

import com.google.gson.JsonElement
import retrofit2.http.GET
import retrofit2.http.Query

interface HolidayApi {
    @GET("/")
    suspend fun getCalendar(@Query("year") year: Int): JsonElement
}
