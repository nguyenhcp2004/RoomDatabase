package com.example.roomdatabase.database;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * https://developer.android.com/training/data-storage/room/migrating-db-versions#java
 */
public class Migrations {
    
    /**
     * Migration từ version 1 lên version 2
     * Thêm cột customerType vào bảng Customer
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Customer ADD COLUMN customerType TEXT NOT NULL DEFAULT 'Regular'");
        }
    };
}
