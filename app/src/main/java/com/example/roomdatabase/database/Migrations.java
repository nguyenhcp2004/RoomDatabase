package com.example.roomdatabase.database;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrations {
    
    /**
     * Migration từ version 1 lên version 2
     * Thêm cột customerType vào bảng customers
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Entity Customer dùng tableName = "customers"
            database.execSQL("ALTER TABLE customers ADD COLUMN customerType TEXT NOT NULL DEFAULT 'Regular'");
        }
    };

    /**
     * Migration từ version 2 lên version 3
     * Thêm cột bio (nullable) vào bảng customers
     */
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE customers ADD COLUMN bio TEXT");
        }
    };
}
