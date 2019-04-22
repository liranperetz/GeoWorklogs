package com.bringg.vm

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.bringg.data.local.RoomDB
import com.bringg.data.local.dao.WorkAddressDao
import com.bringg.data.local.entity.WorkAddress
import com.bringg.extensions.getContext
import kotlinx.coroutines.runBlocking

class WorkAddressVM(application: Application) : AndroidViewModel(application) {

    private val db: RoomDB by lazy {
        RoomDB.get(getContext())
    }

    private val workAddressDao: WorkAddressDao by lazy {
        db.workAddressDao
    }

    /**
     * Get work address live data object
     * @return live data of work addresses list
     */
    fun getWorkAddress(): LiveData<List<WorkAddress>> = workAddressDao.getWorkAddresses()

    /**
     * Save work address in local db
     *
     * @param workAddress
     */
    fun save(workAddress: WorkAddress) = runBlocking {
        workAddressDao.insert(workAddress)
    }

}