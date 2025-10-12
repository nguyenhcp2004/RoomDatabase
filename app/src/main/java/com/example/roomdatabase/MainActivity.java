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

import com.example.roomdatabase.adapter.UserAdapter;
import com.example.roomdatabase.adapter.ProductAdapter;
import com.example.roomdatabase.adapter.OrderAdapter;
import com.example.roomdatabase.database.AppDatabase;
import com.example.roomdatabase.dao.UserDao;
import com.example.roomdatabase.dao.ProductDao;
import com.example.roomdatabase.dao.OrderDao;
import com.example.roomdatabase.entity.User;
import com.example.roomdatabase.entity.Product;
import com.example.roomdatabase.entity.Order;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private Button btnAdd, btnRefresh;
    
    private AppDatabase database;
    private UserDao userDao;
    private ProductDao productDao;
    private OrderDao orderDao;
    
    private UserAdapter userAdapter;
    private ProductAdapter productAdapter;
    private OrderAdapter orderAdapter;
    
    private List<User> users = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    
    private int currentTab = 0; // 0: Users, 1: Products, 2: Orders

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
        
        initViews();
        initDatabase();
        setupTabs();
        setupRecyclerView();
        setupButtons();
        loadData();
    }
    
    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        btnRefresh = findViewById(R.id.btnRefresh);
    }
    
    private void initDatabase() {
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        productDao = database.productDao();
        orderDao = database.orderDao();
    }
    
    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Users"));
        tabLayout.addTab(tabLayout.newTab().setText("Products"));
        tabLayout.addTab(tabLayout.newTab().setText("Orders"));
        
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        switch (currentTab) {
            case 0: // Users
                userAdapter = new UserAdapter(users);
                userAdapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
                    @Override
                    public void onEditClick(User user) {
                        showUserDialog(user);
                    }
                    
                    @Override
                    public void onDeleteClick(User user) {
                        deleteUser(user);
                    }
                });
                recyclerView.setAdapter(userAdapter);
                break;
                
            case 1: // Products
                productAdapter = new ProductAdapter(products);
                productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
                    @Override
                    public void onEditClick(Product product) {
                        showProductDialog(product);
                    }
                    
                    @Override
                    public void onDeleteClick(Product product) {
                        deleteProduct(product);
                    }
                });
                recyclerView.setAdapter(productAdapter);
                break;
                
            case 2: // Orders
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
                    showUserDialog(null);
                    break;
                case 1:
                    showProductDialog(null);
                    break;
                case 2:
                    showOrderDialog(null);
                    break;
            }
        });
        
        btnRefresh.setOnClickListener(v -> loadData());
    }
    
    private void loadData() {
        switch (currentTab) {
            case 0:
                loadUsers();
                break;
            case 1:
                loadProducts();
                break;
            case 2:
                loadOrders();
                break;
        }
    }
    
    // User CRUD Operations
    private void loadUsers() {
        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... voids) {
                return userDao.getAllUsers();
            }
            
            @Override
            protected void onPostExecute(List<User> userList) {
                users.clear();
                users.addAll(userList);
                if (userAdapter != null) {
                    userAdapter.updateUsers(users);
                }
            }
        }.execute();
    }
    
    private void showUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_user, null);
        builder.setView(view);
        
        EditText etName = view.findViewById(R.id.etUserName);
        EditText etEmail = view.findViewById(R.id.etUserEmail);
        EditText etPhone = view.findViewById(R.id.etUserPhone);
        EditText etAddress = view.findViewById(R.id.etUserAddress);
        Button btnSave = view.findViewById(R.id.btnSaveUser);
        Button btnCancel = view.findViewById(R.id.btnCancelUser);
        
        if (user != null) {
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
            etAddress.setText(user.getAddress());
        }
        
        AlertDialog dialog = builder.create();
        
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (user == null) {
                User newUser = new User(name, email, phone, address);
                insertUser(newUser);
            } else {
                user.setName(name);
                user.setEmail(email);
                user.setPhone(phone);
                user.setAddress(address);
                updateUser(user);
            }
            
            dialog.dismiss();
        });
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private void insertUser(User user) {
        new AsyncTask<User, Void, Void>() {
            @Override
            protected Void doInBackground(User... users) {
                userDao.insertUser(users[0]);
                return null;
            }
            
            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                loadUsers();
            }
        }.execute(user);
    }
    
    private void updateUser(User user) {
        new AsyncTask<User, Void, Void>() {
            @Override
            protected Void doInBackground(User... users) {
                userDao.updateUser(users[0]);
                return null;
            }
            
            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                loadUsers();
            }
        }.execute(user);
    }
    
    private void deleteUser(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    new AsyncTask<User, Void, Void>() {
                        @Override
                        protected Void doInBackground(User... users) {
                            userDao.deleteUser(users[0]);
                            return null;
                        }
                        
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            Toast.makeText(MainActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                            loadUsers();
                        }
                    }.execute(user);
                })
                .setNegativeButton("No", null)
                .show();
    }
    
    // Product CRUD Operations
    private void loadProducts() {
        new AsyncTask<Void, Void, List<Product>>() {
            @Override
            protected List<Product> doInBackground(Void... voids) {
                return productDao.getAllProducts();
            }
            
            @Override
            protected void onPostExecute(List<Product> productList) {
                products.clear();
                products.addAll(productList);
                if (productAdapter != null) {
                    productAdapter.updateProducts(products);
                }
            }
        }.execute();
    }
    
    private void showProductDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_product, null);
        builder.setView(view);
        
        EditText etName = view.findViewById(R.id.etProductName);
        EditText etDescription = view.findViewById(R.id.etProductDescription);
        EditText etPrice = view.findViewById(R.id.etProductPrice);
        EditText etStock = view.findViewById(R.id.etProductStock);
        EditText etCategory = view.findViewById(R.id.etProductCategory);
        Button btnSave = view.findViewById(R.id.btnSaveProduct);
        Button btnCancel = view.findViewById(R.id.btnCancelProduct);
        
        if (product != null) {
            etName.setText(product.getName());
            etDescription.setText(product.getDescription());
            etPrice.setText(String.valueOf(product.getPrice()));
            etStock.setText(String.valueOf(product.getStock()));
            etCategory.setText(product.getCategory());
        }
        
        AlertDialog dialog = builder.create();
        
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String stockStr = etStock.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            
            if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                double price = Double.parseDouble(priceStr);
                int stock = Integer.parseInt(stockStr);
                
                if (product == null) {
                    Product newProduct = new Product(name, description, price, stock, category);
                    insertProduct(newProduct);
                } else {
                    product.setName(name);
                    product.setDescription(description);
                    product.setPrice(price);
                    product.setStock(stock);
                    product.setCategory(category);
                    updateProduct(product);
                }
                
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numbers for price and stock", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private void insertProduct(Product product) {
        new AsyncTask<Product, Void, Void>() {
            @Override
            protected Void doInBackground(Product... products) {
                productDao.insertProduct(products[0]);
                return null;
            }
            
            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                loadProducts();
            }
        }.execute(product);
    }
    
    private void updateProduct(Product product) {
        new AsyncTask<Product, Void, Void>() {
            @Override
            protected Void doInBackground(Product... products) {
                productDao.updateProduct(products[0]);
                return null;
            }
            
            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                loadProducts();
            }
        }.execute(product);
    }
    
    private void deleteProduct(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    new AsyncTask<Product, Void, Void>() {
                        @Override
                        protected Void doInBackground(Product... products) {
                            productDao.deleteProduct(products[0]);
                            return null;
                        }
                        
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            Toast.makeText(MainActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                            loadProducts();
                        }
                    }.execute(product);
                })
                .setNegativeButton("No", null)
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
        
        EditText etUserId = view.findViewById(R.id.etOrderUserId);
        EditText etProductId = view.findViewById(R.id.etOrderProductId);
        EditText etQuantity = view.findViewById(R.id.etOrderQuantity);
        EditText etTotal = view.findViewById(R.id.etOrderTotal);
        EditText etDate = view.findViewById(R.id.etOrderDate);
        EditText etStatus = view.findViewById(R.id.etOrderStatus);
        Button btnSave = view.findViewById(R.id.btnSaveOrder);
        Button btnCancel = view.findViewById(R.id.btnCancelOrder);
        
        if (order != null) {
            etUserId.setText(String.valueOf(order.getUserId()));
            etProductId.setText(String.valueOf(order.getProductId()));
            etQuantity.setText(String.valueOf(order.getQuantity()));
            etTotal.setText(String.valueOf(order.getTotalPrice()));
            etDate.setText(order.getOrderDate());
            etStatus.setText(order.getStatus());
        } else {
            // Set default date for new orders
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            etDate.setText(currentDate);
            etStatus.setText("Pending");
        }
        
        AlertDialog dialog = builder.create();
        
        btnSave.setOnClickListener(v -> {
            String userIdStr = etUserId.getText().toString().trim();
            String productIdStr = etProductId.getText().toString().trim();
            String quantityStr = etQuantity.getText().toString().trim();
            String totalStr = etTotal.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String status = etStatus.getText().toString().trim();
            
            if (userIdStr.isEmpty() || productIdStr.isEmpty() || quantityStr.isEmpty() || totalStr.isEmpty() || date.isEmpty() || status.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                int userId = Integer.parseInt(userIdStr);
                int productId = Integer.parseInt(productIdStr);
                int quantity = Integer.parseInt(quantityStr);
                double total = Double.parseDouble(totalStr);
                
                if (order == null) {
                    Order newOrder = new Order(userId, productId, quantity, total, date, status);
                    insertOrder(newOrder);
                } else {
                    order.setUserId(userId);
                    order.setProductId(productId);
                    order.setQuantity(quantity);
                    order.setTotalPrice(total);
                    order.setOrderDate(date);
                    order.setStatus(status);
                    updateOrder(order);
                }
                
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "Order added successfully", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "Order updated successfully", Toast.LENGTH_SHORT).show();
                loadOrders();
            }
        }.execute(order);
    }
    
    private void deleteOrder(Order order) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Order")
                .setMessage("Are you sure you want to delete this order?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    new AsyncTask<Order, Void, Void>() {
                        @Override
                        protected Void doInBackground(Order... orders) {
                            orderDao.deleteOrder(orders[0]);
                            return null;
                        }
                        
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            Toast.makeText(MainActivity.this, "Order deleted successfully", Toast.LENGTH_SHORT).show();
                            loadOrders();
                        }
                    }.execute(order);
                })
                .setNegativeButton("No", null)
                .show();
    }
}