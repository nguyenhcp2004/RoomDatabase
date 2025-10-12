package com.example.roomdatabase.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.roomdatabase.dao.UserDao;
import com.example.roomdatabase.dao.ProductDao;
import com.example.roomdatabase.dao.OrderDao;
import com.example.roomdatabase.entity.User;
import com.example.roomdatabase.entity.Product;
import com.example.roomdatabase.entity.Order;

@Database(
    entities = {User.class, Product.class, Order.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    private static AppDatabase INSTANCE;
    
    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                "room_database"
            ).build();
        }
        return INSTANCE;
    }
    
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
