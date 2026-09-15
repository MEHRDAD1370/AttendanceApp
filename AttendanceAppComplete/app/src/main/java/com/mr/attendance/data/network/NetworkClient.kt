package com.mr.attendance.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    private val http = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val holidayApi: HolidayApi = Retrofit.Builder()
        .baseUrl("https://persian-calendar-api.sajjadth.workers.dev/")
        .client(http)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(HolidayApi::class.java)
}
