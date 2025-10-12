package com.example.roomdatabase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.roomdatabase.entity.Customer;

import java.util.List;

@Dao
public interface CustomerDao {
    
    @Query("SELECT * FROM customers")
    List<Customer> getAllCustomers();
    
    @Query("SELECT * FROM customers WHERE id = :id")
    Customer getCustomerById(int id);
    
    @Query("SELECT * FROM customers WHERE name LIKE :name")
    List<Customer> getCustomersByName(String name);
    
    @Query("SELECT * FROM customers WHERE email = :email")
    Customer getCustomerByEmail(String email);
    
    @Query("SELECT * FROM customers WHERE customerType = :customerType")
    List<Customer> getCustomersByType(String customerType);
    
    @Insert
    void insertCustomer(Customer customer);
    
    @Insert
    void insertCustomers(Customer... customers);
    
    @Update
    void updateCustomer(Customer customer);
    
    @Delete
    void deleteCustomer(Customer customer);
    
    @Query("DELETE FROM customers WHERE id = :id")
    void deleteCustomerById(int id);
    
    @Query("DELETE FROM customers")
    void deleteAllCustomers();
}
