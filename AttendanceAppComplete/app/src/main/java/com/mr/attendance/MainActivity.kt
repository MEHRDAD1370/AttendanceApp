package com.mr.attendance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.mr.attendance.data.db.AppDatabase
import com.mr.attendance.data.network.NetworkClient
import com.mr.attendance.data.repository.AttendanceRepository
import com.mr.attendance.data.repository.HolidayRepository
import com.mr.attendance.domain.AttendanceManager
import com.mr.attendance.ui.*

class AttendanceViewModelFactory(private val repo: AttendanceRepository, private val holidayRepo: HolidayRepository, private val manager: AttendanceManager): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST") override fun <T: androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T = MainViewModel(repo,holidayRepo,manager) as T
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
        val db = AppDatabase.get(this); val repo = AttendanceRepository(db.attendanceDao(),db.personnelDao()); val holidayRepo = HolidayRepository(db.holidayDao(),NetworkClient.holidayApi); val manager = AttendanceManager(repo)
        setContent {
            MaterialTheme {
                val vm: MainViewModel = viewModel(factory=AttendanceViewModelFactory(repo,holidayRepo,manager)); var screen by remember{mutableStateOf("main")}; var editRecord by remember{mutableStateOf<com.mr.attendance.domain.AttendanceRecord?>(null)}
                when(screen){
                    "main"->MainScreen(vm){screen=it}
                    "calendar"->CalendarScreen(vm){screen="main"}
                    "report"->ReportScreen(vm){screen="main"}
                    "records"->RecordsScreen(vm,{screen="main"}){editRecord=it;screen="edit"}
                    "edit"->editRecord?.let{EditAttendanceScreen(vm,it){editRecord=null;screen="records"}} ?: run{screen="records"}
                    "profile"->ProfileScreen(vm){screen="main"}
                    "about"->AboutScreen{screen="main"}
                }
            }
        }
    }
}
