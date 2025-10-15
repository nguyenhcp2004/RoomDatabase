package com.example.roomdatabase;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.app.DatePickerDialog;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    
    // ExecutorService để thực hiện các thao tác database
    private ExecutorService executorService;
    
    // LiveData observers
    private LiveData<List<Customer>> customersLiveData;
    private LiveData<List<Cake>> cakesLiveData;
    private LiveData<List<Order>> ordersLiveData;

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
        if (btnRefresh != null) {
            btnRefresh.setVisibility(View.GONE);
        }
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
                return;
            }
            
            // Khởi tạo ExecutorService
            executorService = Executors.newFixedThreadPool(4);
            
            // Khởi tạo LiveData
            customersLiveData = customerDao.getAllCustomers();
            cakesLiveData = cakeDao.getAllCakes();
            ordersLiveData = orderDao.getAllOrders();
            
            // Thiết lập observers
            setupObservers();
            
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi tạo database: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    
    private void setupObservers() {
        // Observer cho customers
        customersLiveData.observe(this, new Observer<List<Customer>>() {
            @Override
            public void onChanged(List<Customer> customerList) {
                if (customerList != null) {
                    customers.clear();
                    customers.addAll(customerList);
                    if (customerAdapter != null) {
                        customerAdapter.updateCustomers(customers);
                    }
                }
            }
        });
        
        // Observer cho cakes
        cakesLiveData.observe(this, new Observer<List<Cake>>() {
            @Override
            public void onChanged(List<Cake> cakeList) {
                if (cakeList != null) {
                    cakes.clear();
                    cakes.addAll(cakeList);
                    if (cakeAdapter != null) {
                        cakeAdapter.updateCakes(cakes);
                    }
                }
            }
        });
        
        // Observer cho orders
        ordersLiveData.observe(this, new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orderList) {
                if (orderList != null) {
                    orders.clear();
                    orders.addAll(orderList);
                    if (orderAdapter != null) {
                        orderAdapter.updateOrders(orders);
                    }
                }
            }
        });
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
        
        // Nút refresh đã bị ẩn và vô hiệu hóa
    }
    
    private void loadData() {
        // Với LiveData, dữ liệu sẽ tự động cập nhật thông qua observers
        // Không cần gọi các phương thức load riêng lẻ nữa
        Toast.makeText(this, "Dữ liệu đã được cập nhật tự động", Toast.LENGTH_SHORT).show();
    }
    
    // Customer CRUD Operations
    
    private void showCustomerDialog(Customer customer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_user, null);
        builder.setView(view);
        
        EditText etName = view.findViewById(R.id.etCustomerName);
        EditText etEmail = view.findViewById(R.id.etCustomerEmail);
        EditText etPhone = view.findViewById(R.id.etCustomerPhone);
        EditText etAddress = view.findViewById(R.id.etCustomerAddress);
        Spinner spType = view.findViewById(R.id.spCustomerType);
        Button btnSave = view.findViewById(R.id.btnSaveCustomer);
        Button btnCancel = view.findViewById(R.id.btnCancelCustomer);
        
        if (customer != null) {
            etName.setText(customer.getName());
            etEmail.setText(customer.getEmail());
            etPhone.setText(customer.getPhone());
            etAddress.setText(customer.getAddress());
            // preset selection theo customerType
            String[] types = {"VIP", "Regular", "New"};
            int sel = 1; // default Regular
            for (int i = 0; i < types.length; i++) if (types[i].equalsIgnoreCase(customer.getCustomerType())) { sel = i; break; }
            spType.setSelection(sel);
        }
        
        AlertDialog dialog = builder.create();
        
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String type = spType.getSelectedItem() != null ? spType.getSelectedItem().toString() : "";
            
            // Validation cơ bản
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || type.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validation tên (ít nhất 2 ký tự)
            if (name.length() < 2) {
                Toast.makeText(this, "Tên khách hàng phải có ít nhất 2 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validation email format
            if (!isValidEmail(email)) {
                Toast.makeText(this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validation số điện thoại (chỉ số, 10-11 chữ số)
            if (!isValidPhone(phone)) {
                Toast.makeText(this, "Số điện thoại phải có 10-11 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validation địa chỉ (ít nhất 10 ký tự)
            if (address.length() < 10) {
                Toast.makeText(this, "Địa chỉ phải có ít nhất 10 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validation loại khách hàng
            if (!isValidCustomerType(type)) {
                Toast.makeText(this, "Loại khách hàng không hợp lệ (VIP, Regular, New)", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Kiểm tra email trùng lặp (chỉ khi thêm mới)
            if (customer == null) {
                checkEmailExistsAndInsert(email, name, phone, address, type, dialog);
            } else {
                // Khi cập nhật, chỉ kiểm tra email trùng nếu email thay đổi
                if (!customer.getEmail().equals(email)) {
                    checkEmailExistsAndUpdate(customer, email, name, phone, address, type, dialog);
                } else {
                    customer.setName(name);
                    customer.setEmail(email);
                    customer.setPhone(phone);
                    customer.setAddress(address);
                    customer.setCustomerType(type);
                    updateCustomer(customer);
                    dialog.dismiss();
                }
            }
        });
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    // Validation methods
    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }
    
    private boolean isValidPhone(String phone) {
        // Chỉ chứa số và có độ dài 10-11 chữ số
        return phone.matches("^[0-9]{10,11}$");
    }
    
    private boolean isValidCustomerType(String type) {
        String[] validTypes = {"VIP", "Regular", "New"};
        for (String validType : validTypes) {
            if (validType.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
    
    // Kiểm tra email trùng lặp khi thêm mới
    private void checkEmailExistsAndInsert(String email, String name, String phone, String address, String type, AlertDialog dialog) {
        executorService.execute(() -> {
            try {
                Customer existingCustomer = customerDao.getCustomerByEmail(email).getValue();
                runOnUiThread(() -> {
                    if (existingCustomer != null) {
                        Toast.makeText(MainActivity.this, "❌ Email này đã tồn tại trong hệ thống!", Toast.LENGTH_SHORT).show();
                    } else {
                        Customer newCustomer = new Customer(name, email, phone, address, type);
                        insertCustomer(newCustomer);
                        dialog.dismiss();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "❌ Lỗi kiểm tra email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    // Kiểm tra email trùng lặp khi cập nhật
    private void checkEmailExistsAndUpdate(Customer customer, String email, String name, String phone, String address, String type, AlertDialog dialog) {
        executorService.execute(() -> {
            try {
                Customer existingCustomer = customerDao.getCustomerByEmail(email).getValue();
                runOnUiThread(() -> {
                    if (existingCustomer != null && existingCustomer.getId() != customer.getId()) {
                        Toast.makeText(MainActivity.this, "❌ Email này đã tồn tại trong hệ thống!", Toast.LENGTH_SHORT).show();
                    } else {
                        customer.setName(name);
                        customer.setEmail(email);
                        customer.setPhone(phone);
                        customer.setAddress(address);
                        customer.setCustomerType(type);
                        updateCustomer(customer);
                        dialog.dismiss();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "❌ Lỗi kiểm tra email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void insertCustomer(Customer customer) {
        executorService.execute(() -> {
            try {
                customerDao.insertCustomer(customer);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "✅ Thêm khách hàng thành công!", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "❌ Lỗi thêm khách hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void updateCustomer(Customer customer) {
        executorService.execute(() -> {
            try {
                customerDao.updateCustomer(customer);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "✅ Cập nhật khách hàng thành công!", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "❌ Lỗi cập nhật khách hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void deleteCustomer(Customer customer) {
        new AlertDialog.Builder(this)
                .setTitle("🗑️ Xóa Khách Hàng")
                .setMessage("Bạn có chắc chắn muốn xóa khách hàng này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    executorService.execute(() -> {
                        try {
                            customerDao.deleteCustomer(customer);
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "✅ Xóa khách hàng thành công!", Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "❌ Lỗi xóa khách hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Không", null)
                .show();
    }
    
    // Cake CRUD Operations
    
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
        executorService.execute(() -> {
            try {
                cakeDao.insertCake(cake);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "✅ Thêm bánh thành công!", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "❌ Lỗi thêm bánh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void updateCake(Cake cake) {
        executorService.execute(() -> {
            try {
                cakeDao.updateCake(cake);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "✅ Cập nhật bánh thành công!", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "❌ Lỗi cập nhật bánh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void deleteCake(Cake cake) {
        new AlertDialog.Builder(this)
                .setTitle("🗑️ Xóa Bánh")
                .setMessage("Bạn có chắc chắn muốn xóa bánh này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    executorService.execute(() -> {
                        try {
                            cakeDao.deleteCake(cake);
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "✅ Xóa bánh thành công!", Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "❌ Lỗi xóa bánh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Không", null)
                .show();
    }
    
    // Order CRUD Operations
    
    private void showOrderDialog(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_order, null);
        builder.setView(view);
        
        Spinner spCustomer = view.findViewById(R.id.spOrderCustomer);
        Spinner spCake = view.findViewById(R.id.spOrderCake);
        EditText etQuantity = view.findViewById(R.id.etOrderQuantity);
        EditText etTotal = view.findViewById(R.id.etOrderTotal);
        EditText etDate = view.findViewById(R.id.etOrderDate);
        EditText etDeliveryDate = view.findViewById(R.id.etOrderDeliveryDate);
        EditText etStatus = view.findViewById(R.id.etOrderStatus);
        EditText etInstructions = view.findViewById(R.id.etOrderSpecialInstructions);
        EditText etAddress = view.findViewById(R.id.etOrderDeliveryAddress);
        Button btnSave = view.findViewById(R.id.btnSaveOrder);
        Button btnCancel = view.findViewById(R.id.btnCancelOrder);
        
        // Chuẩn bị dữ liệu spinner khách hàng
        List<Customer> localCustomers = new ArrayList<>(customers != null ? customers : new ArrayList<>());
        List<String> customerNames = new ArrayList<>();
        for (Customer c : localCustomers) {
            customerNames.add(c.getName());
        }
        ArrayAdapter<String> customerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customerNames);
        customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCustomer.setAdapter(customerAdapter);

        // Chuẩn bị dữ liệu spinner bánh
        List<Cake> localCakes = new ArrayList<>(cakes != null ? cakes : new ArrayList<>());
        List<String> cakeNames = new ArrayList<>();
        for (Cake c : localCakes) {
            cakeNames.add(c.getName());
        }
        ArrayAdapter<String> cakeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cakeNames);
        cakeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCake.setAdapter(cakeAdapter);

        if (order != null) {
            // preset selection theo customerId của order
            if (localCustomers != null && !localCustomers.isEmpty()) {
                int idx = 0;
                for (int i = 0; i < localCustomers.size(); i++) {
                    if (localCustomers.get(i).getId() == order.getCustomerId()) { idx = i; break; }
                }
                spCustomer.setSelection(idx);
            }
            if (localCakes != null && !localCakes.isEmpty()) {
                int idxCake = 0;
                for (int i = 0; i < localCakes.size(); i++) {
                    if (localCakes.get(i).getId() == order.getCakeId()) { idxCake = i; break; }
                }
                spCake.setSelection(idxCake);
            }
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

        // Date pickers cho ngày đặt và ngày giao
        View.OnClickListener dateClickListener = v -> {
            final Calendar cal = Calendar.getInstance();
            int y = cal.get(Calendar.YEAR), m = cal.get(Calendar.MONTH), d = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dp = new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                String mm = String.format(Locale.getDefault(), "%02d", month + 1);
                String dd = String.format(Locale.getDefault(), "%02d", dayOfMonth);
                ((EditText) v).setText(year + "-" + mm + "-" + dd);
            }, y, m, d);
            dp.show();
        };
        etDate.setOnClickListener(dateClickListener);
        etDeliveryDate.setOnClickListener(dateClickListener);
        
        AlertDialog dialog = builder.create();
        
        btnSave.setOnClickListener(v -> {
            // Lấy customerId từ spinner
            int selectedPos = spCustomer.getSelectedItemPosition();
            String customerIdStr = (selectedPos >= 0 && selectedPos < localCustomers.size()) ? String.valueOf(localCustomers.get(selectedPos).getId()) : "";
            // Lấy cakeId từ spinner
            int selectedCakePos = spCake.getSelectedItemPosition();
            String cakeIdStr = (selectedCakePos >= 0 && selectedCakePos < localCakes.size()) ? String.valueOf(localCakes.get(selectedCakePos).getId()) : "";
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
        executorService.execute(() -> {
            try {
                orderDao.insertOrder(order);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "✅ Thêm đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "❌ Lỗi thêm đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void updateOrder(Order order) {
        executorService.execute(() -> {
            try {
                orderDao.updateOrder(order);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "✅ Cập nhật đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "❌ Lỗi cập nhật đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void deleteOrder(Order order) {
        new AlertDialog.Builder(this)
                .setTitle("🗑️ Xóa Đơn Hàng")
                .setMessage("Bạn có chắc chắn muốn xóa đơn hàng này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    executorService.execute(() -> {
                        try {
                            orderDao.deleteOrder(order);
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "✅ Xóa đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "❌ Lỗi xóa đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
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
        
        executorService.execute(() -> {
            try {
                // Kiểm tra và thêm sample customers
                List<Customer> existingCustomers = customerDao.getAllCustomers().getValue();
                if (existingCustomers == null || existingCustomers.isEmpty()) {
                    customerDao.insertCustomers(
                        new Customer("Nguyễn Văn An", "an.nguyen@email.com", "0123456789", "123 Đường ABC, Quận 1, TP.HCM", "VIP"),
                        new Customer("Trần Thị Bình", "binh.tran@email.com", "0987654321", "456 Đường XYZ, Quận 2, TP.HCM", "Regular"),
                        new Customer("Lê Văn Cường", "cuong.le@email.com", "0369852147", "789 Đường DEF, Quận 3, TP.HCM", "New")
                    );
                }
                
                // Kiểm tra và thêm sample cakes
                List<Cake> existingCakes = cakeDao.getAllCakes().getValue();
                if (existingCakes == null || existingCakes.isEmpty()) {
                    cakeDao.insertCakes(
                        new Cake("Bánh Sinh Nhật Chocolate", "Bánh sinh nhật chocolate thơm ngon với kem tươi", 250000, 10, "Birthday", "Medium", "Chocolate"),
                        new Cake("Bánh Cưới Vanilla", "Bánh cưới vanilla sang trọng cho ngày đặc biệt", 500000, 5, "Wedding", "Large", "Vanilla"),
                        new Cake("Bánh Kỷ Niệm Strawberry", "Bánh kỷ niệm vị dâu tây ngọt ngào", 180000, 15, "Anniversary", "Small", "Strawberry"),
                        new Cake("Bánh Tiramisu", "Bánh tiramisu Ý truyền thống", 320000, 8, "Custom", "Medium", "Mixed"),
                        new Cake("Bánh Red Velvet", "Bánh red velvet với cream cheese", 280000, 12, "Birthday", "Medium", "Mixed")
                    );
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "❌ Lỗi thêm dữ liệu mẫu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
                e.printStackTrace();
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}