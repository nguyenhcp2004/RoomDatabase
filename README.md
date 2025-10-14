## RoomDatabase (Android • Java • Room 2.6.1)

### Giới thiệu

Dự án mẫu quản lý Cửa hàng Bánh sử dụng Room Database (Entity/DAO/Database) và UI đơn giản với `TabLayout` + `RecyclerView`. Ứng dụng cho phép CRUD cho 3 bảng: Khách hàng (`Customer`), Bánh (`Cake`), Đơn hàng (`Order`).

---

### Dành cho người mới: Hiểu nhanh flow dự án trong 60 giây

- Màn hình chính: `MainActivity` với 3 tab; mỗi tab là một danh sách (`RecyclerView`).
- Bấm Thêm/Sửa/Xóa → `MainActivity` mở dialog nhập liệu → gọi hàm CRUD nền (`AsyncTask`).
- CRUD gọi xuống DAO (`@Dao` + `@Query/@Insert/@Update/@Delete`).
- DAO do Room generate, dựa trên `@Entity` và `@Database` trong `AppDatabase`.
- `AppDatabase.getInstance(context)` cung cấp singleton DB chứa 3 DAO: `CustomerDao`, `CakeDao`, `OrderDao`.

Chỉ cần nắm 5 dòng trên, bạn đã có bức tranh tổng thể.

### Tính năng chính

- Quản lý khách hàng, bánh, đơn hàng với CRUD đầy đủ.
- Giao diện 3 tab: Khách Hàng, Bánh, Đơn Hàng.
- Dữ liệu mẫu được nạp tự động khi mở app lần đầu (nếu bảng trống).

---

### Yêu cầu hệ thống

- Android Studio Koala / Jellyfish trở lên (tương thích AGP 8.7.2)
- Android SDK: compileSdk 34, targetSdk 34, minSdk 24
- JDK 11
- Gradle Wrapper đi kèm dự án

---

### Công nghệ & thư viện

- Room: 2.6.1 (annotationProcessor, Java)
- AndroidX: AppCompat, Activity, ConstraintLayout, RecyclerView, Material

---

### Cấu trúc dự án

```
RoomDatabase/
├─ app/
│  ├─ src/main/
│  │  ├─ java/com/example/roomdatabase/
│  │  │  ├─ MainActivity.java                // Điều hướng tabs, CRUD qua dialogs
│  │  │  ├─ database/AppDatabase.java        // RoomDatabase singleton
│  │  │  ├─ dao/CustomerDao.java             // DAO Khách hàng
│  │  │  ├─ dao/CakeDao.java                 // DAO Bánh
│  │  │  ├─ dao/OrderDao.java                // DAO Đơn hàng
│  │  │  ├─ entity/Customer.java             // @Entity customers
│  │  │  ├─ entity/Cake.java                 // @Entity cakes
│  │  │  └─ entity/Order.java                // @Entity orders
│  │  ├─ res/layout/                         // activity_main.xml, dialog_*.xml, item_*.xml
│  │  └─ AndroidManifest.xml
│  ├─ build.gradle.kts
├─ build.gradle.kts
├─ settings.gradle.kts
└─ gradle/libs.versions.toml
```

#### Vai trò chi tiết từng thư mục/tệp (đi theo luồng làm việc)

- `app/src/main/java/com/example/roomdatabase/MainActivity.java`

  - Điều phối UI: tạo `TabLayout`, `RecyclerView`, xử lý nút `Thêm`/`Làm mới`.
  - Khởi tạo DB: `AppDatabase.getInstance(this)` → lấy `customerDao/cakeDao/orderDao`.
  - Thực thi CRUD qua `AsyncTask` rồi cập nhật Adapter để render danh sách.

- `app/src/main/java/com/example/roomdatabase/database/AppDatabase.java`

  - `@Database(entities=..., version=2, exportSchema=false)`; extends `RoomDatabase`.
  - Cấp phát singleton bằng `Room.databaseBuilder(...).fallbackToDestructiveMigration().build()`.
  - Expose `customerDao()`, `cakeDao()`, `orderDao()`.

- `app/src/main/java/com/example/roomdatabase/dao/*.java`

  - Khai báo DAO với `@Dao`; dùng `@Query` cho đọc/lọc; `@Insert/@Update/@Delete` cho ghi.
  - Không viết SQL phức tạp trong Activity; mọi truy vấn nằm ở DAO.

- `app/src/main/java/com/example/roomdatabase/entity/*.java`

  - `@Entity(tableName=...)` mô tả bảng; `@PrimaryKey` mô tả khóa; field = cột.
  - Getter/Setter phục vụ Room và Adapter.

- `app/src/main/res/layout/`

  - `activity_main.xml`: khung UI chính.
  - `dialog_*.xml`: form nhập liệu cho từng nghiệp vụ.
  - `item_*.xml`: layout một hàng trong danh sách.

- `AndroidManifest.xml`

  - Khai báo `MainActivity` là launcher.

- `build.gradle.kts`, `gradle/libs.versions.toml`
  - Quản lý phiên bản plugin, thư viện, và options biên dịch.

---

### Cài đặt và chạy nhanh (Android Studio)

1. Mở Android Studio → Open → trỏ tới thư mục gốc dự án.
2. Chờ Gradle đồng bộ dependencies.
3. Chọn cấu hình chạy `app` và thiết bị/emulator Android (API 24+).
4. Bấm Run ▶ để build và cài đặt.

Gợi ý: Lần đầu build có thể hơi lâu do tải dependencies. Nếu gặp lỗi JDK, đảm bảo Project JDK trỏ về JDK 11.

---

### Build/run bằng dòng lệnh

- Windows PowerShell/CMD (tại thư mục gốc dự án):

```
./gradlew :app:assembleDebug
```

File APK sau build: `app/build/outputs/apk/debug/app-debug.apk`

Có thể cài qua adb:

```
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

### Cấu hình Gradle chính

- `compileSdk = 34`, `targetSdk = 34`, `minSdk = 24`
- Java 11: `sourceCompatibility = JavaVersion.VERSION_11`
- Room 2.6.1 với `annotationProcessor("androidx.room:room-compiler:2.6.1")`

Trích yếu `app/build.gradle.kts` (đã rút gọn):

```gradle
android {
    compileSdk = 34
    defaultConfig { minSdk = 24; targetSdk = 34 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.13.0")
}
```

---

### Kiến trúc Room trong dự án

1. Database: `AppDatabase`

   - `version = 2`, `exportSchema = false`
   - Singleton qua `getInstance(context)`
   - Tên DB: `room_database`
   - `fallbackToDestructiveMigration()` (xem lưu ý Migration bên dưới)

2. Entities:

   - `Customer`: id, name, email, phone, address, customerType
   - `Cake`: id, name, description, price, stock, category, size, flavor, imageUrl
   - `Order`: id, customerId, cakeId, quantity, totalPrice, orderDate, deliveryDate, status, specialInstructions, deliveryAddress

3. DAO APIs (rút gọn):

   - `CustomerDao`: `getAllCustomers()`, `getCustomerById()`, `getCustomersByName()`, `insertCustomer(s)`, `updateCustomer()`, `deleteCustomer()`...
   - `CakeDao`: `getAllCakes()`, `getCakesByCategory()`, `getCakesByPriceRange()`, `insertCake(s)`, `updateCake()`, `deleteCake()`...
   - `OrderDao`: `getAllOrders()`, `getOrdersByCustomerId()`, `getOrdersByStatus()`, `insertOrder(s)`, `updateOrder()`, `deleteOrder()`...

4. Khởi tạo DB & sử dụng trong UI

- `MainActivity` gọi `AppDatabase.getInstance(this)` lấy `CustomerDao`, `CakeDao`, `OrderDao`.
- CRUD thực hiện trong `AsyncTask` và cập nhật `RecyclerView` qua các Adapter tương ứng.
- Dữ liệu mẫu được thêm trong `addSampleData()` nếu bảng trống.

---

### Chạy DB & CRUD nhanh (hands-on)

Mục tiêu: Hiểu cách chạy DB và thực hiện CRUD ngay trong vài phút.

1. Khởi tạo DB và DAO (đã có sẵn trong `MainActivity.initDatabase()`)

```java
database = AppDatabase.getInstance(this);
customerDao = database.customerDao();
cakeDao = database.cakeDao();
orderDao = database.orderDao();
```

2. Đọc dữ liệu danh sách (ví dụ Bánh)

```java
new AsyncTask<Void, Void, List<Cake>>() {
    protected List<Cake> doInBackground(Void... v) { return cakeDao.getAllCakes(); }
    protected void onPostExecute(List<Cake> data) {
        cakes.clear(); cakes.addAll(data);
        cakeAdapter.updateCakes(cakes);
    }
}.execute();
```

3. Thêm mới (Create)

```java
Cake newCake = new Cake(name, desc, price, stock, category, size, flavor);
new AsyncTask<Cake, Void, Void>() {
    protected Void doInBackground(Cake... cs) { cakeDao.insertCake(cs[0]); return null; }
    protected void onPostExecute(Void ig) { loadCakes(); }
}.execute(newCake);
```

4. Cập nhật (Update)

```java
cake.setPrice(newPrice);
new AsyncTask<Cake, Void, Void>() {
    protected Void doInBackground(Cake... cs) { cakeDao.updateCake(cs[0]); return null; }
    protected void onPostExecute(Void ig) { loadCakes(); }
}.execute(cake);
```

5. Xóa (Delete)

```java
new AsyncTask<Cake, Void, Void>() {
    protected Void doInBackground(Cake... cs) { cakeDao.deleteCake(cs[0]); return null; }
    protected void onPostExecute(Void ig) { loadCakes(); }
}.execute(cake);
```

6. Đọc theo điều kiện (ví dụ lọc theo danh mục)

```java
new AsyncTask<Void, Void, List<Cake>>() {
    protected List<Cake> doInBackground(Void... v) { return cakeDao.getCakesByCategory("Birthday"); }
    protected void onPostExecute(List<Cake> data) { cakeAdapter.updateCakes(data); }
}.execute();
```

Lặp lại tương tự cho `CustomerDao` và `OrderDao` với các phương thức đã có.

Tips:

- Luôn gọi thao tác DB trong background (`AsyncTask`, Executor, ViewModel + LiveData/Coroutines nếu nâng cấp).
- Sau CRUD, nhớ refresh UI (gọi `load*()` hoặc `adapter.update*()` phù hợp).

---

#### Ví dụ CRUD với Customer (dựa trên `CustomerDao`)

1. Đọc tất cả khách hàng

```java
new AsyncTask<Void, Void, List<Customer>>() {
    protected List<Customer> doInBackground(Void... v) { return customerDao.getAllCustomers(); }
    protected void onPostExecute(List<Customer> data) {
        customers.clear(); customers.addAll(data);
        customerAdapter.updateCustomers(customers);
    }
}.execute();
```

2. Thêm khách hàng

```java
Customer c = new Customer(name, email, phone, address, type);
new AsyncTask<Customer, Void, Void>() {
    protected Void doInBackground(Customer... cs) { customerDao.insertCustomer(cs[0]); return null; }
    protected void onPostExecute(Void ig) { loadCustomers(); }
}.execute(c);
```

3. Cập nhật khách hàng

```java
customer.setAddress(newAddress);
new AsyncTask<Customer, Void, Void>() {
    protected Void doInBackground(Customer... cs) { customerDao.updateCustomer(cs[0]); return null; }
    protected void onPostExecute(Void ig) { loadCustomers(); }
}.execute(customer);
```

4. Xóa khách hàng

```java
new AsyncTask<Customer, Void, Void>() {
    protected Void doInBackground(Customer... cs) { customerDao.deleteCustomer(cs[0]); return null; }
    protected void onPostExecute(Void ig) { loadCustomers(); }
}.execute(customer);
```

5. Tìm theo điều kiện (ví dụ tên gần đúng)

```java
new AsyncTask<Void, Void, List<Customer>>() {
    protected List<Customer> doInBackground(Void... v) { return customerDao.getCustomersByName("%An%"); }
    protected void onPostExecute(List<Customer> data) { customerAdapter.updateCustomers(data); }
}.execute();
```

---

#### Ví dụ CRUD với Order (dựa trên `OrderDao`)

1. Đọc tất cả đơn hàng

```java
new AsyncTask<Void, Void, List<Order>>() {
    protected List<Order> doInBackground(Void... v) { return orderDao.getAllOrders(); }
    protected void onPostExecute(List<Order> data) {
        orders.clear(); orders.addAll(data);
        orderAdapter.updateOrders(orders);
    }
}.execute();
```

2. Thêm đơn hàng

```java
Order o = new Order(customerId, cakeId, quantity, total, orderDate, deliveryDate, status, instructions, address);
new AsyncTask<Order, Void, Void>() {
    protected Void doInBackground(Order... os) { orderDao.insertOrder(os[0]); return null; }
    protected void onPostExecute(Void ig) { loadOrders(); }
}.execute(o);
```

3. Cập nhật đơn hàng

```java
order.setStatus("Completed");
new AsyncTask<Order, Void, Void>() {
    protected Void doInBackground(Order... os) { orderDao.updateOrder(os[0]); return null; }
    protected void onPostExecute(Void ig) { loadOrders(); }
}.execute(order);
```

4. Xóa đơn hàng

```java
new AsyncTask<Order, Void, Void>() {
    protected Void doInBackground(Order... os) { orderDao.deleteOrder(os[0]); return null; }
    protected void onPostExecute(Void ig) { loadOrders(); }
}.execute(order);
```

5. Lọc theo trạng thái

```java
new AsyncTask<Void, Void, List<Order>>() {
    protected List<Order> doInBackground(Void... v) { return orderDao.getOrdersByStatus("Pending"); }
    protected void onPostExecute(List<Order> data) { orderAdapter.updateOrders(data); }
}.execute();
```

---

### Flow chi tiết: từ UI → DB qua một hành động ví dụ

Ví dụ “Thêm Bánh”:

1. Người dùng nhấn `Thêm` (tab Bánh) → `showCakeDialog(null)`.
2. Nhập form và Lưu → tạo `new Cake(...)`.
3. Gọi `insertCake(newCake)` → `AsyncTask#doInBackground` gọi `cakeDao.insertCake(newCake)`.
4. DAO thực thi lệnh `@Insert` vào bảng `cakes`.
5. `onPostExecute` gọi `loadCakes()` → `cakeDao.getAllCakes()` → cập nhật `cakeAdapter` → UI hiển thị item mới.

Sửa/Xóa tương tự: mở dialog với dữ liệu hiện có → cập nhật model → `update/delete` → reload danh sách.

---

### Hướng dẫn thực hành: tạo bảng mới từ A → Z (ví dụ `Supplier`)

1. Tạo Entity

```java
@Entity(tableName = "suppliers")
public class Supplier {
    @PrimaryKey(autoGenerate = true) private int id;
    private String name; private String phone; private String address;
    // getters/setters...
}
```

2. Tạo DAO

```java
@Dao
public interface SupplierDao {
    @Query("SELECT * FROM suppliers") List<Supplier> getAllSuppliers();
    @Insert void insertSupplier(Supplier s);
    @Update void updateSupplier(Supplier s);
    @Delete void deleteSupplier(Supplier s);
}
```

3. Khai báo vào `AppDatabase`

```java
@Database(entities = {Customer.class, Cake.class, Order.class, Supplier.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SupplierDao supplierDao();
}
```

4. Thêm Migration để giữ dữ liệu (khuyến nghị)

```java
// static final Migration MIGRATION_2_3 = new Migration(2, 3) {
//   @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
//     db.execSQL("CREATE TABLE IF NOT EXISTS suppliers (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT, phone TEXT, address TEXT)");
//   }
// };
// Room.databaseBuilder(...).addMigrations(MIGRATION_2_3).build();
```

5. Thêm UI

- Thêm tab “Nhà Cung Cấp”, tạo `SupplierAdapter`, `item_supplier.xml`, `dialog_supplier.xml`.
- Thêm các hàm `loadSuppliers()/insert/update/delete` trong `MainActivity` theo mẫu sẵn có.

6. Kiểm thử

- Chạy app → thêm/sửa/xóa nhà cung cấp → danh sách cập nhật đúng.

Bạn có thể lặp lại 6 bước cho mọi bảng mới.

---

### Walkthrough: Đọc mã theo trình tự

1. `MainActivity`: `onCreate()` → `initViews()` → `initDatabase()` → `setupTabs()` → `setupRecyclerView()` → `setupButtons()` → `loadData()`.
2. `AppDatabase`: cách khai báo entities/version và singleton.
3. `entity/*.java`: schema bảng và kiểu dữ liệu.
4. `dao/*.java`: truy vấn sẵn có để tái sử dụng.
5. Quay lại `MainActivity`: theo dõi `load*`, `show*Dialog`, `insert/update/delete*` để nắm trọn luồng UI ↔ DAO.

---

### Quy ước & gợi ý code trong dự án

- Tên bảng số nhiều: `customers`, `cakes`, `orders`.
- Dùng `AsyncTask` để không chặn UI thread (gợi ý nâng cấp: ViewModel + LiveData/Flow).
- Adapter chịu trách nhiệm cập nhật danh sách và notify thay đổi.
- Thay đổi schema: luôn tăng `version` và thêm Migration để bảo toàn dữ liệu.

---

### Luồng màn hình & UX

- Tab "Khách Hàng": danh sách khách hàng; thêm/sửa/xóa qua dialog `dialog_user`.
- Tab "Bánh": danh sách bánh; thêm/sửa/xóa qua dialog `dialog_product`.
- Tab "Đơn Hàng": danh sách đơn hàng; thêm/sửa/xóa qua dialog `dialog_order`.
- Nút "Thêm" mở dialog theo tab hiện tại. Nút "Làm mới" reload dữ liệu.

---

### Tuỳ biến và mở rộng

1. Thêm trường mới vào Entity:

   - Cập nhật lớp `@Entity` tương ứng (vd: `Cake.java`).
   - Tăng `version` trong `AppDatabase` (vd: từ 2 → 3).
   - Thay vì `fallbackToDestructiveMigration()`, bạn nên thêm `.addMigrations(MIGRATION_2_3)` để giữ dữ liệu.

2. Viết Migration (gợi ý):

```java
// Ví dụ khai báo trong AppDatabase.java
// static final Migration MIGRATION_2_3 = new Migration(2, 3) {
//     @Override
//     public void migrate(@NonNull SupportSQLiteDatabase db) {
//         db.execSQL("ALTER TABLE cakes ADD COLUMN imageUrl TEXT");
//     }
// };
// ... Room.databaseBuilder(...).addMigrations(MIGRATION_2_3).build();
```

3. Thêm API lọc/tìm kiếm mới:

   - Bổ sung `@Query` trong DAO tương ứng.
   - Gọi DAO từ Activity/ViewModel và cập nhật Adapter.

4. Chuyển sang luồng bất đồng bộ hiện đại:
   - `AsyncTask` đã lỗi thời. Gợi ý dùng `Executor`, `LiveData`/`ViewModel`, hoặc `Coroutines` (nếu chuyển sang Kotlin).

---

### Kiểm thử nhanh chức năng CRUD

1. Chạy app, chuyển qua từng tab để thấy danh sách.
2. Nhấn "Thêm" để tạo mới; điền đầy đủ trường; Lưu.
3. Nhấn vào icon sửa/xóa trên mỗi item để cập nhật/xóa.
4. Nhấn "Làm mới" để reload dữ liệu.

---

### Xử lý sự cố thường gặp

- Lỗi JDK/compileOptions: đảm bảo JDK 11, và `sourceCompatibility/targetCompatibility` là Java 11.
- Không thấy dữ liệu: kiểm tra `addSampleData()` chỉ thêm khi bảng trống; thử bấm "Làm mới".
- Mất dữ liệu sau khi đổi schema: do `fallbackToDestructiveMigration()`. Hãy thêm Migration thực thụ để giữ dữ liệu.
- Lỗi build dependencies: đảm bảo mạng ổn định hoặc thử `./gradlew --refresh-dependencies`.

---

### Lệnh Gradle hữu ích

```
./gradlew clean
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

---

### Gợi ý nâng cấp

- Tách logic DB sang Repository + ViewModel.
- Sử dụng `Flow`/`LiveData` để quan sát dữ liệu.
- Chuyển `annotationProcessor` → KAPT (nếu chuyển sang Kotlin) hoặc Room Rx/Flow.
- Thêm Paging cho danh sách dài.

---

### Bản quyền

Mã nguồn phục vụ mục đích học tập. Bạn có thể tự do fork và mở rộng.
