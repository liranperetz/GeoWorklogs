package com.bringg.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = WorkAddress.TABLE_NAME
)
data class WorkAddress(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = FIELD_ID)
    val id: Int = 0,
    val lat: Double,
    val lon: Double,
    val typedAddress: String
) {

    override fun toString(): String = "id = $id, Typed address = $typedAddress, lat = $lat, lon = $lon"

    companion object {
        const val TABLE_NAME = "WorkAddress"
        const val FIELD_ID = "_id"
    }
}