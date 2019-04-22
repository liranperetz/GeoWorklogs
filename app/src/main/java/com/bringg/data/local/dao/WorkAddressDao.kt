package com.bringg.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bringg.data.local.entity.WorkAddress

@Dao
interface WorkAddressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(workAddress: WorkAddress)

    @Query("SELECT * FROM ${WorkAddress.TABLE_NAME} ORDER BY ${WorkAddress.FIELD_ID} DESC")
    fun getWorkAddresses(): LiveData<List<WorkAddress>>

}