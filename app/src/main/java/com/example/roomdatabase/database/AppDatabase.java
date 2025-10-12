package com.example.roomdatabase.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.roomdatabase.dao.CustomerDao;
import com.example.roomdatabase.dao.CakeDao;
import com.example.roomdatabase.dao.OrderDao;
import com.example.roomdatabase.entity.Customer;
import com.example.roomdatabase.entity.Cake;
import com.example.roomdatabase.entity.Order;

@Database(
    entities = {Customer.class, Cake.class, Order.class},
    version = 2,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    private static AppDatabase INSTANCE;
    
    public abstract CustomerDao customerDao();
    public abstract CakeDao cakeDao();
    public abstract OrderDao orderDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                "room_database"
            )
            .fallbackToDestructiveMigration()
            .build();
        }
        return INSTANCE;
    }
    
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
