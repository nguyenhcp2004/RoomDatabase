package com.example.roomdatabase.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;

@Entity(
    tableName = "orders",
    foreignKeys = {
        @ForeignKey(
            entity = Customer.class,
            parentColumns = "id",
            childColumns = "customerId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Cake.class,
            parentColumns = "id",
            childColumns = "cakeId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index("customerId"),
        @Index("cakeId")
    }
)
public class Order {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int customerId;
    private int cakeId;
    private int quantity;
    private double totalPrice;
    private String orderDate;
    private String deliveryDate;
    private String status; // "Pending", "Confirmed", "Preparing", "Ready", "Delivered", "Cancelled"
    private String specialInstructions;
    private String deliveryAddress;
    
    public Order() {}
    
    public Order(int customerId, int cakeId, int quantity, double totalPrice, String orderDate, String deliveryDate, String status, String specialInstructions, String deliveryAddress) {
        this.customerId = customerId;
        this.cakeId = cakeId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.specialInstructions = specialInstructions;
        this.deliveryAddress = deliveryAddress;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public int getCakeId() {
        return cakeId;
    }
    
    public void setCakeId(int cakeId) {
        this.cakeId = cakeId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
