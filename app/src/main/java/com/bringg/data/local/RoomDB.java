package com.bringg.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.bringg.data.local.dao.WorkAddressDao;
import com.bringg.data.local.dao.WorkLogDao;
import com.bringg.data.local.entity.WorkAddress;
import com.bringg.data.local.entity.WorkLog;

@Database(entities = {
        WorkAddress.class,
        WorkLog.class
}, version = 1)
public abstract class RoomDB extends RoomDatabase {

    private static volatile RoomDB INSTANCE;

    public static RoomDB get(Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RoomDB.class, "bringg.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract WorkAddressDao getWorkAddressDao();

    public abstract WorkLogDao getWorkLogDao();

}
