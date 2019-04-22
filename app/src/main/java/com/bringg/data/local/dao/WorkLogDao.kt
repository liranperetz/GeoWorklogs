package com.bringg.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bringg.data.local.entity.WorkLog

@Dao
interface WorkLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(workLog: WorkLog)

    @Query("SELECT * FROM ${WorkLog.TABLE_NAME}")
    fun getAll(): LiveData<List<WorkLog>>

    @Query(
        "SELECT * FROM ${WorkLog.TABLE_NAME} " +
                "WHERE ${WorkLog.FIELD_DWELL_TIME} == 0  " +
                "AND ${WorkLog.FIELD_WORK_ADDRESS} == :workAddressId " +
                "ORDER BY ${WorkLog.FIELD_ID} DESC " +
                "LIMIT 1"
    )
    fun getLastOpened(workAddressId: Int): WorkLog?

}