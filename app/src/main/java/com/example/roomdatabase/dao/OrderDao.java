package com.example.roomdatabase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.roomdatabase.entity.Order;

import java.util.List;

@Dao
public interface OrderDao {
    
    @Query("SELECT * FROM orders")
    List<Order> getAllOrders();
    
    @Query("SELECT * FROM orders WHERE id = :id")
    Order getOrderById(int id);
    
    @Query("SELECT * FROM orders WHERE customerId = :customerId")
    List<Order> getOrdersByCustomerId(int customerId);
    
    @Query("SELECT * FROM orders WHERE cakeId = :cakeId")
    List<Order> getOrdersByCakeId(int cakeId);
    
    @Query("SELECT * FROM orders WHERE status = :status")
    List<Order> getOrdersByStatus(String status);
    
    @Query("SELECT * FROM orders WHERE orderDate = :orderDate")
    List<Order> getOrdersByDate(String orderDate);
    
    @Query("SELECT * FROM orders WHERE deliveryDate = :deliveryDate")
    List<Order> getOrdersByDeliveryDate(String deliveryDate);
    
    @Query("SELECT * FROM orders WHERE customerId = :customerId AND cakeId = :cakeId")
    List<Order> getOrdersByCustomerAndCake(int customerId, int cakeId);
    
    @Insert
    void insertOrder(Order order);
    
    @Insert
    void insertOrders(Order... orders);
    
    @Update
    void updateOrder(Order order);
    
    @Delete
    void deleteOrder(Order order);
    
    @Query("DELETE FROM orders WHERE id = :id")
    void deleteOrderById(int id);
    
    @Query("DELETE FROM orders")
    void deleteAllOrders();
}
