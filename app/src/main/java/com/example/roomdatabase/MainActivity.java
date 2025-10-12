package com.example.roomdatabase;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.roomdatabase.adapter.CustomerAdapter;
import com.example.roomdatabase.adapter.CakeAdapter;
import com.example.roomdatabase.adapter.OrderAdapter;
import com.example.roomdatabase.database.AppDatabase;
import com.example.roomdatabase.dao.CustomerDao;
import com.example.roomdatabase.dao.CakeDao;
import com.example.roomdatabase.dao.OrderDao;
import com.example.roomdatabase.entity.Customer;
import com.example.roomdatabase.entity.Cake;
import com.example.roomdatabase.entity.Order;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private Button btnAdd, btnRefresh;
    
    private AppDatabase database;
    private CustomerDao customerDao;
    private CakeDao cakeDao;
    private OrderDao orderDao;
    
    private CustomerAdapter customerAdapter;
    private CakeAdapter cakeAdapter;
    private OrderAdapter orderAdapter;
    
    private List<Customer> customers = new ArrayList<>();
    private List<Cake> cakes = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    
    private int currentTab = 0; // 0: Customers, 1: Cakes, 2: Orders

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        try {
            initViews();
            initDatabase();
            setupTabs();
            setupRecyclerView();
            setupButtons();
            
            // Load data after a short delay to ensure everything is initialized
            recyclerView.postDelayed(() -> {
                loadData();
                addSampleData();
            }, 100);
            
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi tạo ứng dụng: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    
    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        btnRefresh = findViewById(R.id.btnRefresh);
    }
    
    private void initDatabase() {
        try {
            database = AppDatabase.getInstance(this);
            if (database == null) {
                Toast.makeText(this, "Không thể khởi tạo database", Toast.LENGTH_SHORT).show();
                return;
            }
            
            customerDao = database.customerDao();
            cakeDao = database.cakeDao();
            orderDao = database.orderDao();
            
            if (customerDao == null || cakeDao == null || orderDao == null) {
                Toast.makeText(this, "Không thể khởi tạo DAO", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi tạo database: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    
    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("👥 Khách Hàng"));
        tabLayout.addTab(tabLayout.newTab().setText("🍰 Bánh"));
        tabLayout.addTab(tabLayout.newTab().setText("📋 Đơn Hàng"));
        
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                setupRecyclerView();
                loadData();
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    
    private void setupRecyclerView() {
        if (recyclerView == null) {
            Toast.makeText(this, "RecyclerView không được tìm thấy", Toast.LENGTH_SHORT).show();
            return;
        }
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        switch (currentTab) {
            case 0: // Customers
                if (customers == null) customers = new ArrayList<>();
                customerAdapter = new CustomerAdapter(customers);
                customerAdapter.setOnCustomerClickListener(new CustomerAdapter.OnCustomerClickListener() {
                    @Override
                    public void onEditClick(Customer customer) {
                        showCustomerDialog(customer);
                    }
                    
                    @Override
                    public void onDeleteClick(Customer customer) {
                        deleteCustomer(customer);
                    }
                });
                recyclerView.setAdapter(customerAdapter);
                break;
                
            case 1: // Cakes
                if (cakes == null) cakes = new ArrayList<>();
                cakeAdapter = new CakeAdapter(cakes);
                cakeAdapter.setOnCakeClickListener(new CakeAdapter.OnCakeClickListener() {
                    @Override
                    public void onEditClick(Cake cake) {
                        showCakeDialog(cake);
                    }
                    
                    @Override
                    public void onDeleteClick(Cake cake) {
                        deleteCake(cake);
                    }
                });
                recyclerView.setAdapter(cakeAdapter);
                break;
                
            case 2: // Orders
                if (orders == null) orders = new ArrayList<>();
                orderAdapter = new OrderAdapter(orders);
                orderAdapter.setOnOrderClickListener(new OrderAdapter.OnOrderClickListener() {
                    @Override
                    public void onEditClick(Order order) {
                        showOrderDialog(order);
                    }
                    
                    @Override
                    public void onDeleteClick(Order order) {
                        deleteOrder(order);
                    }
                });
                recyclerView.setAdapter(orderAdapter);
                break;
        }
    }
    
    private void setupButtons() {
        btnAdd.setOnClickListener(v -> {
            switch (currentTab) {
                case 0:
                    showCustomerDialog(null);
                    break;
                case 1:
                    showCakeDialog(null);
                    break;
                case 2:
                    showOrderDialog(null);
                    break;
            }
        });
        
        btnRefresh.setOnClickListener(v -> loadData());
    }
    
    private void loadData() {
        if (customerDao == null || cakeDao == null || orderDao == null) {
            Toast.makeText(this, "DAO chưa được khởi tạo", Toast.LENGTH_SHORT).show();
            return;
        }
        
        switch (currentTab) {
            case 0:
                loadCustomers();
                break;
            case 1:
                loadCakes();
                break;
            case 2:
                loadOrders();
                break;
        }
    }
    
    // Customer CRUD Operations
    private void loadCustomers() {
        new AsyncTask<Void, Void, List<Customer>>() {
            @Override
            protected List<Customer> doInBackground(Void... voids) {
                return customerDao.getAllCustomers();
            }
            
            @Override
            protected void onPostExecute(List<Customer> customerList) {
                customers.clear();
                customers.addAll(customerList);
                if (customerAdapter != null) {
                    customerAdapter.updateCustomers(customers);
                }
            }
        }.execute();
    }
    
    private void showCustomerDialog(Customer customer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_user, null);
        builder.setView(view);
        
        EditText etName = view.findViewById(R.id.etCustomerName);
        EditText etEmail = view.findViewById(R.id.etCustomerEmail);
        EditText etPhone = view.findViewById(R.id.etCustomerPhone);
        EditText etAddress = view.findViewById(R.id.etCustomerAddress);
        EditText etType = view.findViewById(R.id.etCustomerType);
        Button btnSave = view.findViewById(R.id.btnSaveCustomer);
        Button btnCancel = view.findViewById(R.id.btnCancelCustomer);
        
        if (customer != null) {
            etName.setText(customer.getName());
            etEmail.setText(customer.getEmail());
            etPhone.setText(customer.getPhone());
            etAddress.setText(customer.getAddress());
            etType.setText(customer.getCustomerType());
        }
        
        AlertDialog dialog = builder.create();
        
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String type = etType.getText().toString().trim();
            
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || type.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (customer == null) {
                Customer newCustomer = new Customer(name, email, phone, address, type);
                insertCustomer(newCustomer);
            } else {
                customer.setName(name);
                customer.setEmail(email);
                customer.setPhone(phone);
                customer.setAddress(address);
                customer.setCustomerType(type);
                updateCustomer(customer);
            }
            
            dialog.dismiss();
        });
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private void insertCustomer(Customer customer) {
        new AsyncTask<Customer, Void, Void>() {
            @Override
            protected Void doInBackground(Customer... customers) {
                customerDao.insertCustomer(customers[0]);
                return null;
            }
            
            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.this, "✅ Thêm khách hàng thành công!", Toast.LENGTH_SHORT).show();
                loadCustomers();
            }
        }.execute(customer);
    }
    
    private void updateCustomer(Customer customer) {
        new AsyncTask<Customer, Void, Void>() {
            @Override
            protected Void doInBackground(Customer... customers) {
                customerDao.updateCustomer(customers[0]);
                return null;
            }
            
            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.this, "✅ Cập nhật khách hàng thành công!", Toast.LENGTH_SHORT).show();
                loadCustomers();
            }
        }.execute(customer);
    }
    
    private void deleteCustomer(Customer customer) {
        new AlertDialog.Builder(this)
                .setTitle("🗑️ Xóa Khách Hàng")
                .setMessage("Bạn có chắc chắn muốn xóa khách hàng này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    new AsyncTask<Customer, Void, Void>() {
                        @Override
                        protected Void doInBackground(Customer... customers) {
                            customerDao.deleteCustomer(customers[0]);
                            return null;
                        }
                        
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            Toast.makeText(MainActivity.this, "✅ Xóa khách hàng thành công!", Toast.LENGTH_SHORT).show();
                            loadCustomers();
                        }
                    }.execute(customer);
                })
                .setNegativeButton("Không", null)
                .show();
    }
    
    // Cake CRUD Operations
    private void loadCakes() {
        new AsyncTask<Void, Void, List<Cake>>() {
            @Override
            protected List<Cake> doInBackground(Void... voids) {
                return cakeDao.getAllCakes();
            }
            
            @Override
            protected void onPostExecute(List<Cake> cakeList) {
                cakes.clear();
                cakes.addAll(cakeList);
                if (cakeAdapter != null) {
                    cakeAdapter.updateCakes(cakes);
                }
            }
        }.execute();
    }
    
    private void showCakeDialog(Cake cake) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_product, null);
        builder.setView(view);
        
        EditText etName = view.findViewById(R.id.etCakeName);
        EditText etDescription = view.findViewById(R.id.etCakeDescription);
        EditText etPrice = view.findViewById(R.id.etCakePrice);
        EditText etStock = view.findViewById(R.id.etCakeStock);
        EditText etCategory = view.findViewById(R.id.etCakeCategory);
        EditText etSize = view.findViewById(R.id.etCakeSize);
        EditText etFlavor = view.findViewById(R.id.etCakeFlavor);
        Button btnSave = view.findViewById(R.id.btnSaveCake);
        Button btnCancel = view.findViewById(R.id.btnCancelCake);
        
        if (cake != null) {
            etName.setText(cake.getName());
            etDescription.setText(cake.getDescription());
            etPrice.setText(String.valueOf(cake.getPrice()));
            etStock.setText(String.valueOf(cake.getStock()));
            etCategory.setText(cake.getCategory());
            etSize.setText(cake.getSize());
            etFlavor.setText(cake.getFlavor());
        }
        
        AlertDialog dialog = builder.create();
        
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String stockStr = etStock.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String size = etSize.getText().toString().trim();
            String flavor = etFlavor.getText().toString().trim();
            
            if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || category.isEmpty() || size.isEmpty() || flavor.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                double price = Double.parseDouble(priceStr);
                int stock = Integer.parseInt(stockStr);
                
                if (cake == null) {
                    Cake newCake = new Cake(name, description, price, stock, category, size, flavor);
                    insertCake(newCake);
                } else {
                    cake.setName(name);
                    cake.setDescription(description);
                    cake.setPrice(price);
                    cake.setStock(stock);
                    cake.setCategory(category);
                    cake.setSize(size);
                    cake.setFlavor(flavor);
                    updateCake(cake);
                }
                
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập số hợp lệ cho giá và số lượng", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private void insertCake(Cake cake) {
        new AsyncTask<Cake, Void, Void>() {
            @Override
            protected Void doInBackground(Cake... cakes) {
                cakeDao.insertCake(cakes[0]);
                return null;
            }
            
            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.this, "✅ Thêm bánh thành công!", Toast.LENGTH_SHORT).show();
                loadCakes();
            }
        }.execute(cake);
    }
    
    private void updateCake(Cake cake) {
        new AsyncTask<Cake, Void, Void>() {
            @Override
            protected Void doInBackground(Cake... cakes) {
                cakeDao.updateCake(cakes[0]);
                return null;
            }
            
            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.this, "✅ Cập nhật bánh thành công!", Toast.LENGTH_SHORT).show();
                loadCakes();
            }
        }.execute(cake);
    }
    
    private void deleteCake(Cake cake) {
        new AlertDialog.Builder(this)
                .setTitle("🗑️ Xóa Bánh")
                .setMessage("Bạn có chắc chắn muốn xóa bánh này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    new AsyncTask<Cake, Void, Void>() {
                        @Override
                        protected Void doInBackground(Cake... cakes) {
                            cakeDao.deleteCake(cakes[0]);
                            return null;
                        }
                        
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            Toast.makeText(MainActivity.this, "✅ Xóa bánh thành công!", Toast.LENGTH_SHORT).show();
                            loadCakes();
                        }
                    }.execute(cake);
                })
                .setNegativeButton("Không", null)
                .show();
    }
    
    // Order CRUD Operations
    private void loadOrders() {
        new AsyncTask<Void, Void, List<Order>>() {
            @Override
            protected List<Order> doInBackground(Void... voids) {
                return orderDao.getAllOrders();
            }
            
            @Override
            protected void onPostExecute(List<Order> orderList) {
                orders.clear();
                orders.addAll(orderList);
                if (orderAdapter != null) {
                    orderAdapter.updateOrders(orders);
                }
            }
        }.execute();
    }
    
    private void showOrderDialog(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_order, null);
        builder.setView(view);
        
        EditText etCustomerId = view.findViewById(R.id.etOrderCustomerId);
        EditText etCakeId = view.findViewById(R.id.etOrderCakeId);
        EditText etQuantity = view.findViewById(R.id.etOrderQuantity);
        EditText etTotal = view.findViewById(R.id.etOrderTotal);
        EditText etDate = view.findViewById(R.id.etOrderDate);
        EditText etDeliveryDate = view.findViewById(R.id.etOrderDeliveryDate);
        EditText etStatus = view.findViewById(R.id.etOrderStatus);
        EditText etInstructions = view.findViewById(R.id.etOrderSpecialInstructions);
        EditText etAddress = view.findViewById(R.id.etOrderDeliveryAddress);
        Button btnSave = view.findViewById(R.id.btnSaveOrder);
        Button btnCancel = view.findViewById(R.id.btnCancelOrder);
        
        if (order != null) {
            etCustomerId.setText(String.valueOf(order.getCustomerId()));
            etCakeId.setText(String.valueOf(order.getCakeId()));
            etQuantity.setText(String.valueOf(order.getQuantity()));
            etTotal.setText(String.valueOf(order.getTotalPrice()));
            etDate.setText(order.getOrderDate());
            etDeliveryDate.setText(order.getDeliveryDate());
            etStatus.setText(order.getStatus());
            etInstructions.setText(order.getSpecialInstructions());
            etAddress.setText(order.getDeliveryAddress());
        } else {
            // Set default values for new orders
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            etDate.setText(currentDate);
            etStatus.setText("Pending");
        }
        
        AlertDialog dialog = builder.create();
        
        btnSave.setOnClickListener(v -> {
            String customerIdStr = etCustomerId.getText().toString().trim();
            String cakeIdStr = etCakeId.getText().toString().trim();
            String quantityStr = etQuantity.getText().toString().trim();
            String totalStr = etTotal.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String deliveryDate = etDeliveryDate.getText().toString().trim();
            String status = etStatus.getText().toString().trim();
            String instructions = etInstructions.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            
            if (customerIdStr.isEmpty() || cakeIdStr.isEmpty() || quantityStr.isEmpty() || totalStr.isEmpty() || date.isEmpty() || status.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                int customerId = Integer.parseInt(customerIdStr);
                int cakeId = Integer.parseInt(cakeIdStr);
                int quantity = Integer.parseInt(quantityStr);
                double total = Double.parseDouble(totalStr);
                
                if (order == null) {
                    Order newOrder = new Order(customerId, cakeId, quantity, total, date, deliveryDate, status, instructions, address);
                    insertOrder(newOrder);
                } else {
                    order.setCustomerId(customerId);
                    order.setCakeId(cakeId);
                    order.setQuantity(quantity);
                    order.setTotalPrice(total);
                    order.setOrderDate(date);
                    order.setDeliveryDate(deliveryDate);
                    order.setStatus(status);
                    order.setSpecialInstructions(instructions);
                    order.setDeliveryAddress(address);
                    updateOrder(order);
                }
                
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private void insertOrder(Order order) {
        new AsyncTask<Order, Void, Void>() {
            @Override
            protected Void doInBackground(Order... orders) {
                orderDao.insertOrder(orders[0]);
                return null;
            }
            
            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.this, "✅ Thêm đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                loadOrders();
            }
        }.execute(order);
    }
    
    private void updateOrder(Order order) {
        new AsyncTask<Order, Void, Void>() {
            @Override
            protected Void doInBackground(Order... orders) {
                orderDao.updateOrder(orders[0]);
                return null;
            }
            
            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.this, "✅ Cập nhật đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                loadOrders();
            }
        }.execute(order);
    }
    
    private void deleteOrder(Order order) {
        new AlertDialog.Builder(this)
                .setTitle("🗑️ Xóa Đơn Hàng")
                .setMessage("Bạn có chắc chắn muốn xóa đơn hàng này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    new AsyncTask<Order, Void, Void>() {
                        @Override
                        protected Void doInBackground(Order... orders) {
                            orderDao.deleteOrder(orders[0]);
                            return null;
                        }
                        
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            Toast.makeText(MainActivity.this, "✅ Xóa đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                            loadOrders();
                        }
                    }.execute(order);
                })
                .setNegativeButton("Không", null)
                .show();
    }
    
    // Add sample data for demonstration
    private void addSampleData() {
        if (customerDao == null || cakeDao == null) {
            Toast.makeText(this, "DAO chưa được khởi tạo", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    // Add sample customers
                    if (customerDao.getAllCustomers().isEmpty()) {
                        customerDao.insertCustomers(
                            new Customer("Nguyễn Văn An", "an.nguyen@email.com", "0123456789", "123 Đường ABC, Quận 1, TP.HCM", "VIP"),
                            new Customer("Trần Thị Bình", "binh.tran@email.com", "0987654321", "456 Đường XYZ, Quận 2, TP.HCM", "Regular"),
                            new Customer("Lê Văn Cường", "cuong.le@email.com", "0369852147", "789 Đường DEF, Quận 3, TP.HCM", "New")
                        );
                    }
                    
                    // Add sample cakes
                    if (cakeDao.getAllCakes().isEmpty()) {
                        cakeDao.insertCakes(
                            new Cake("Bánh Sinh Nhật Chocolate", "Bánh sinh nhật chocolate thơm ngon với kem tươi", 250000, 10, "Birthday", "Medium", "Chocolate"),
                            new Cake("Bánh Cưới Vanilla", "Bánh cưới vanilla sang trọng cho ngày đặc biệt", 500000, 5, "Wedding", "Large", "Vanilla"),
                            new Cake("Bánh Kỷ Niệm Strawberry", "Bánh kỷ niệm vị dâu tây ngọt ngào", 180000, 15, "Anniversary", "Small", "Strawberry"),
                            new Cake("Bánh Tiramisu", "Bánh tiramisu Ý truyền thống", 320000, 8, "Custom", "Medium", "Mixed"),
                            new Cake("Bánh Red Velvet", "Bánh red velvet với cream cheese", 280000, 12, "Birthday", "Medium", "Mixed")
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            
            @Override
            protected void onPostExecute(Void aVoid) {
                loadData();
            }
        }.execute();
    }
}