package com.example.roomdatabase.database;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * File chứa tất cả các migration cho Room Database
 * Mỗi migration được định nghĩa với version bắt đầu và kết thúc
 */
public class Migrations {
    
    /**
     * Migration từ version 1 lên version 2
     * Thêm cột mới vào bảng Customer
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Thêm cột customerType vào bảng Customer
            database.execSQL("ALTER TABLE Customer ADD COLUMN customerType TEXT NOT NULL DEFAULT 'Regular'");
            
            // Thêm cột createdAt vào bảng Customer
            database.execSQL("ALTER TABLE Customer ADD COLUMN createdAt TEXT");
            
            // Cập nhật giá trị mặc định cho các bản ghi cũ
            database.execSQL("UPDATE Customer SET customerType = 'Regular' WHERE customerType IS NULL");
        }
    };
    
    /**
     * Migration từ version 2 lên version 3
     * Thêm bảng mới và cập nhật cấu trúc
     */
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Tạo bảng mới cho lịch sử đơn hàng
            database.execSQL("CREATE TABLE IF NOT EXISTS OrderHistory (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "orderId INTEGER NOT NULL, " +
                    "status TEXT NOT NULL, " +
                    "changedAt TEXT NOT NULL, " +
                    "changedBy TEXT, " +
                    "FOREIGN KEY(orderId) REFERENCES `Order`(id) ON DELETE CASCADE" +
                    ")");
            
            // Thêm cột discount vào bảng Order
            database.execSQL("ALTER TABLE `Order` ADD COLUMN discount REAL DEFAULT 0.0");
            
            // Thêm cột notes vào bảng Cake
            database.execSQL("ALTER TABLE Cake ADD COLUMN notes TEXT");
        }
    };
    
    /**
     * Migration từ version 3 lên version 4
     * Thêm chỉ mục và tối ưu hiệu suất
     */
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Tạo chỉ mục cho tìm kiếm nhanh
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Customer_email ON Customer(email)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Customer_type ON Customer(customerType)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Order_customerId ON `Order`(customerId)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Order_status ON `Order`(status)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Cake_category ON Cake(category)");
            
            // Thêm cột isActive vào bảng Customer (soft delete)
            database.execSQL("ALTER TABLE Customer ADD COLUMN isActive INTEGER NOT NULL DEFAULT 1");
        }
    };
    
    /**
     * Migration từ version 4 lên version 5
     * Thay đổi kiểu dữ liệu và thêm ràng buộc
     */
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Tạo bảng tạm để thay đổi kiểu dữ liệu
            database.execSQL("CREATE TABLE Customer_new (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT NOT NULL UNIQUE, " +
                    "phone TEXT NOT NULL, " +
                    "address TEXT NOT NULL, " +
                    "customerType TEXT NOT NULL DEFAULT 'Regular', " +
                    "createdAt TEXT, " +
                    "isActive INTEGER NOT NULL DEFAULT 1, " +
                    "lastOrderDate TEXT, " +
                    "totalOrders INTEGER DEFAULT 0" +
                    ")");
            
            // Copy dữ liệu từ bảng cũ sang bảng mới
            database.execSQL("INSERT INTO Customer_new (id, name, email, phone, address, customerType, createdAt, isActive) " +
                    "SELECT id, name, email, phone, address, customerType, createdAt, isActive FROM Customer");
            
            // Xóa bảng cũ và đổi tên bảng mới
            database.execSQL("DROP TABLE Customer");
            database.execSQL("ALTER TABLE Customer_new RENAME TO Customer");
            
            // Tạo lại chỉ mục
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Customer_email ON Customer(email)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Customer_type ON Customer(customerType)");
        }
    };
    
    /**
     * Migration phức tạp: Tách bảng Order thành Order và OrderItem
     * Đây là ví dụ về migration phức tạp khi thay đổi cấu trúc quan hệ
     */
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Tạo bảng Order mới với cấu trúc đơn giản hơn
            database.execSQL("CREATE TABLE Order_new (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "customerId INTEGER NOT NULL, " +
                    "orderDate TEXT NOT NULL, " +
                    "deliveryDate TEXT, " +
                    "status TEXT NOT NULL, " +
                    "totalPrice REAL NOT NULL, " +
                    "discount REAL DEFAULT 0.0, " +
                    "deliveryAddress TEXT, " +
                    "specialInstructions TEXT, " +
                    "createdAt TEXT, " +
                    "FOREIGN KEY(customerId) REFERENCES Customer(id) ON DELETE CASCADE" +
                    ")");
            
            // Tạo bảng OrderItem để lưu chi tiết từng sản phẩm trong đơn hàng
            database.execSQL("CREATE TABLE OrderItem (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "orderId INTEGER NOT NULL, " +
                    "cakeId INTEGER NOT NULL, " +
                    "quantity INTEGER NOT NULL, " +
                    "unitPrice REAL NOT NULL, " +
                    "totalPrice REAL NOT NULL, " +
                    "FOREIGN KEY(orderId) REFERENCES Order_new(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(cakeId) REFERENCES Cake(id) ON DELETE CASCADE" +
                    ")");
            
            // Copy dữ liệu từ bảng Order cũ
            database.execSQL("INSERT INTO Order_new (id, customerId, orderDate, deliveryDate, status, totalPrice, discount, deliveryAddress, specialInstructions) " +
                    "SELECT id, customerId, orderDate, deliveryDate, status, totalPrice, discount, deliveryAddress, specialInstructions FROM `Order`");
            
            // Tạo OrderItem từ dữ liệu cũ (giả sử mỗi order chỉ có 1 cake)
            database.execSQL("INSERT INTO OrderItem (orderId, cakeId, quantity, unitPrice, totalPrice) " +
                    "SELECT id, cakeId, quantity, (totalPrice/quantity), totalPrice FROM `Order`");
            
            // Xóa bảng cũ và đổi tên
            database.execSQL("DROP TABLE `Order`");
            database.execSQL("ALTER TABLE Order_new RENAME TO `Order`");
            
            // Tạo lại chỉ mục
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Order_customerId ON `Order`(customerId)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Order_status ON `Order`(status)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_OrderItem_orderId ON OrderItem(orderId)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_OrderItem_cakeId ON OrderItem(cakeId)");
        }
    };
}
