package com.mr.attendance.data.db

import androidx.room.*

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE date = :date LIMIT 1") suspend fun getByDate(date: String): AttendanceEntity?
    @Query("SELECT * FROM attendance WHERE date LIKE :prefix || '%' ORDER BY date ASC") suspend fun getMonth(prefix: String): List<AttendanceEntity>
    @Query("SELECT * FROM attendance ORDER BY date DESC") suspend fun getAll(): List<AttendanceEntity>
    @Query("SELECT * FROM attendance WHERE date = :date AND entryTime IS NOT NULL AND exitTime IS NULL LIMIT 1") suspend fun getOpen(date: String): AttendanceEntity?
    @Insert suspend fun insert(entity: AttendanceEntity): Long
    @Update suspend fun update(entity: AttendanceEntity)
    @Delete suspend fun delete(entity: AttendanceEntity)
    @Query("DELETE FROM attendance") suspend fun deleteAll()
}

@Dao
interface PersonnelDao {
    @Query("SELECT * FROM personnel WHERE id = 1 LIMIT 1") suspend fun get(): PersonnelEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun save(entity: PersonnelEntity)
}

@Dao
interface HolidayDao {
    @Query("SELECT * FROM holidays WHERE date LIKE :prefix || '%' ORDER BY date") suspend fun forYear(prefix: String): List<HolidayEntity>
    @Query("SELECT * FROM holidays WHERE date = :date LIMIT 1") suspend fun find(date: String): HolidayEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertAll(items: List<HolidayEntity>)
    @Query("DELETE FROM holidays WHERE updatedAt < :cutoff") suspend fun deleteOld(cutoff: Long)
}
