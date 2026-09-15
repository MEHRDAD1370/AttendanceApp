package com.mr.attendance.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mr.attendance.data.db.PersonnelEntity
import com.mr.attendance.data.repository.AttendanceRepository
import com.mr.attendance.data.repository.HolidayRepository
import com.mr.attendance.domain.*
import com.mr.attendance.util.PersianDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class MainViewModel(private val repo: AttendanceRepository, private val holidays: HolidayRepository, private val manager: AttendanceManager): ViewModel() {
    private val _today = MutableStateFlow<AttendanceRecord?>(null)
    val today: StateFlow<AttendanceRecord?> = _today.asStateFlow()
    private val _selectedShift = MutableStateFlow(Shift.A)
    val selectedShift: StateFlow<Shift> = _selectedShift.asStateFlow()
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()
    private val _personnel = MutableStateFlow(PersonnelEntity())
    val personnel: StateFlow<PersonnelEntity> = _personnel.asStateFlow()
    private val _records = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val records: StateFlow<List<AttendanceRecord>> = _records.asStateFlow()
    private val _holidayYear = MutableStateFlow<List<com.mr.attendance.data.db.HolidayEntity>>(emptyList())
    val holidayYear: StateFlow<List<com.mr.attendance.data.db.HolidayEntity>> = _holidayYear.asStateFlow()

    init { refresh() }
    fun selectShift(s: Shift) { _selectedShift.value = s }
    fun clearMessage() { _message.value = null }

    fun refresh(date: LocalDate = LocalDate.now()) { viewModelScope.launch {
        _personnel.value = repo.personnel() ?: PersonnelEntity()
        _today.value = repo.today(date)
        _records.value = repo.month(YearMonth.from(date))
        syncHolidays(PersianDate.fromGregorian(date).year)
    }}
    fun syncHolidayYear(year: Int) { viewModelScope.launch { holidays.syncYear(year); _holidayYear.value = holidays.localYear(year) } }
    private fun syncHolidays(year: Int) { viewModelScope.launch {
        holidays.syncYear(year)
        _holidayYear.value = holidays.localYear(year)
    }}
    fun registerEntry() { viewModelScope.launch { manager.registerEntry(LocalDate.now(), _selectedShift.value).onSuccess { refresh() }.onFailure { _message.value = it.message } } }
    fun registerExit() { viewModelScope.launch { manager.registerExit(LocalDate.now()).onSuccess { refresh() }.onFailure { _message.value = it.message } } }
    fun reloadMonth(month: YearMonth) { viewModelScope.launch { _records.value = repo.month(month) } }
    fun savePersonnel(name: String, job: String, type: String, rate: Long) { viewModelScope.launch { repo.savePersonnel(PersonnelEntity(1,name,job,type,rate)); _personnel.value = PersonnelEntity(1,name,job,type,rate); _message.value = "اطلاعات ذخیره شد." } }
    fun saveRecord(record: AttendanceRecord) { viewModelScope.launch { repo.save(record); _message.value = "رکورد ذخیره شد."; refresh(record.date) } }
    fun deleteRecord(record: AttendanceRecord) { viewModelScope.launch { repo.delete(record); _message.value = "رکورد حذف شد."; refresh(record.date) } }
}
