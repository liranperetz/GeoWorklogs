package com.bringg.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.bringg.data.local.RoomDB
import com.bringg.data.local.entity.WorkLog
import com.bringg.extensions.getContext

class WorkLogVM(application: Application) : AndroidViewModel(application) {

    private val db: RoomDB by lazy {
        RoomDB.get(getContext())
    }

    private val dao = db.workLogDao

    fun getDateSummary(from: Long, to: Long): LiveData<List<WorkLog>> = dao.getDateSummary(from, to)

}