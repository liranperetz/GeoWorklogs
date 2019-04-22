package com.bringg.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = WorkLog.TABLE_NAME, foreignKeys = [
        ForeignKey(
            entity = WorkAddress::class,
            parentColumns = [WorkAddress.FIELD_ID],
            childColumns = [WorkLog.FIELD_WORK_ADDRESS]
        )
    ]
)
data class WorkLog(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = FIELD_ID)
    val id: Int = 0,
    val arrivedAt: Long = 0,
    var leavedAt: Long = 0,
    var dwellTime: Long = 0,
    @ColumnInfo(name = FIELD_WORK_ADDRESS)
    val workAddress: Int
) {

    override fun equals(other: Any?): Boolean {
        return other != null &&
                other is WorkLog &&
                id == other.id &&
                arrivedAt == other.arrivedAt &&
                leavedAt == other.leavedAt &&
                dwellTime == other.dwellTime &&
                workAddress == other.workAddress
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return "id: $id, arrivedAt: $arrivedAt, leavedAt: $leavedAt, dwellTime: $dwellTime, workAddress: $workAddress"
    }

    companion object {
        const val TABLE_NAME = "WorkLog"
        const val FIELD_ID = "_id"
        const val FIELD_WORK_ADDRESS = "WorkAddress"
        const val FIELD_DWELL_TIME = "dwellTime"
    }
}