package com.example.roomdatabase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.roomdatabase.entity.Product;

import java.util.List;

@Dao
public interface ProductDao {
    
    @Query("SELECT * FROM products")
    List<Product> getAllProducts();
    
    @Query("SELECT * FROM products WHERE id = :id")
    Product getProductById(int id);
    
    @Query("SELECT * FROM products WHERE name LIKE :name")
    List<Product> getProductsByName(String name);
    
    @Query("SELECT * FROM products WHERE category = :category")
    List<Product> getProductsByCategory(String category);
    
    @Query("SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice")
    List<Product> getProductsByPriceRange(double minPrice, double maxPrice);
    
    @Query("SELECT * FROM products WHERE stock > 0")
    List<Product> getAvailableProducts();
    
    @Insert
    void insertProduct(Product product);
    
    @Insert
    void insertProducts(Product... products);
    
    @Update
    void updateProduct(Product product);
    
    @Delete
    void deleteProduct(Product product);
    
    @Query("DELETE FROM products WHERE id = :id")
    void deleteProductById(int id);
    
    @Query("DELETE FROM products")
    void deleteAllProducts();
}
