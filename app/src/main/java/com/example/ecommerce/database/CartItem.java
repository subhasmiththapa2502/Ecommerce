package com.example.ecommerce.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by Subhasmith Thapa on 21,October,2021
 */
@Entity(tableName = "cart")
public class CartItem implements Serializable {
    @PrimaryKey(autoGenerate = false)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "unitCost")
    private int unitCost;


    @ColumnInfo(name = "totalCost")
    private int totalCost;


    @ColumnInfo(name = "imagePath")
    private String imagePath;

    @ColumnInfo(name = "cost")
    private int cost;

    @ColumnInfo(name = "quantity")
    private int quantity;

    public CartItem(int id, String title, String imagePath, int cost, int quantity, int totalCost, int unitCost){
        this.cost = cost;
        this.id = id;
        this.title = title;
        this.imagePath = imagePath;
        this.quantity = quantity;
        this.totalCost = totalCost;
        this.unitCost = unitCost;

    }

    public CartItem() {

    }

    public int getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(int unitCost) {
        this.unitCost = unitCost;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
