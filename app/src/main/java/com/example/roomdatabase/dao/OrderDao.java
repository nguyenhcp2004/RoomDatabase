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
    
    @Query("SELECT * FROM orders WHERE userId = :userId")
    List<Order> getOrdersByUserId(int userId);
    
    @Query("SELECT * FROM orders WHERE productId = :productId")
    List<Order> getOrdersByProductId(int productId);
    
    @Query("SELECT * FROM orders WHERE status = :status")
    List<Order> getOrdersByStatus(String status);
    
    @Query("SELECT * FROM orders WHERE orderDate = :orderDate")
    List<Order> getOrdersByDate(String orderDate);
    
    @Query("SELECT * FROM orders WHERE userId = :userId AND productId = :productId")
    List<Order> getOrdersByUserAndProduct(int userId, int productId);
    
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
