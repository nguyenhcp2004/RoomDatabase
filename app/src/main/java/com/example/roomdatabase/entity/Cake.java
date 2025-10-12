package com.example.roomdatabase.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cakes")
public class Cake {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String name;
    private String description;
    private double price;
    private int stock;
    private String category; // "Birthday", "Wedding", "Anniversary", "Custom"
    private String size; // "Small", "Medium", "Large", "Extra Large"
    private String flavor; // "Chocolate", "Vanilla", "Strawberry", "Mixed"
    private String imageUrl; // For future image support
    
    public Cake() {}
    
    public Cake(String name, String description, double price, int stock, String category, String size, String flavor) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.size = size;
        this.flavor = flavor;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public String getFlavor() {
        return flavor;
    }
    
    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
