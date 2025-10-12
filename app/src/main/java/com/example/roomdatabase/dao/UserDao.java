package com.example.roomdatabase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.roomdatabase.entity.User;

import java.util.List;

@Dao
public interface UserDao {
    
    @Query("SELECT * FROM users")
    List<User> getAllUsers();
    
    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(int id);
    
    @Query("SELECT * FROM users WHERE name LIKE :name")
    List<User> getUsersByName(String name);
    
    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);
    
    @Insert
    void insertUser(User user);
    
    @Insert
    void insertUsers(User... users);
    
    @Update
    void updateUser(User user);
    
    @Delete
    void deleteUser(User user);
    
    @Query("DELETE FROM users WHERE id = :id")
    void deleteUserById(int id);
    
    @Query("DELETE FROM users")
    void deleteAllUsers();
}
