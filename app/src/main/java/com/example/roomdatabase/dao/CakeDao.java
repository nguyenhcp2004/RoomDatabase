package com.example.roomdatabase.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.roomdatabase.entity.Cake;

import java.util.List;

@Dao
public interface CakeDao {
    
    @Query("SELECT * FROM cakes ORDER BY id DESC")
    LiveData<List<Cake>> getAllCakes();
    
    @Query("SELECT * FROM cakes WHERE id = :id")
    LiveData<Cake> getCakeById(int id);
    
    @Query("SELECT * FROM cakes WHERE name LIKE :name ORDER BY id DESC")
    LiveData<List<Cake>> getCakesByName(String name);
    
    @Query("SELECT * FROM cakes WHERE category = :category ORDER BY id DESC")
    LiveData<List<Cake>> getCakesByCategory(String category);
    
    @Query("SELECT * FROM cakes WHERE flavor = :flavor ORDER BY id DESC")
    LiveData<List<Cake>> getCakesByFlavor(String flavor);
    
    @Query("SELECT * FROM cakes WHERE size = :size ORDER BY id DESC")
    LiveData<List<Cake>> getCakesBySize(String size);
    
    @Query("SELECT * FROM cakes WHERE price BETWEEN :minPrice AND :maxPrice ORDER BY id DESC")
    LiveData<List<Cake>> getCakesByPriceRange(double minPrice, double maxPrice);
    
    @Query("SELECT * FROM cakes WHERE stock > 0 ORDER BY id DESC")
    LiveData<List<Cake>> getAvailableCakes();
    
    @Insert
    void insertCake(Cake cake);
    
    @Insert
    void insertCakes(Cake... cakes);
    
    @Update
    void updateCake(Cake cake);
    
    @Delete
    void deleteCake(Cake cake);
    
    @Query("DELETE FROM cakes WHERE id = :id")
    void deleteCakeById(int id);
    
    @Query("DELETE FROM cakes")
    void deleteAllCakes();
}
