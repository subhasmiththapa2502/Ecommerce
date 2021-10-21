package com.example.ecommerce.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Created by Subhasmith Thapa on 21,October,2021
 */
@Dao
public interface CartItemDao {
    @Query("Select * from cart")
    List<CartItem> getCartItems();

    @Query("SELECT EXISTS (SELECT 1 FROM cart WHERE id = :id)")
    boolean exists(int id);

    @Insert
    void insertCartItem(CartItem cartItem);

    @Update
    void updateCartItem(CartItem cartItem);

    @Query("UPDATE cart SET quantity=:quantity WHERE id = :id")
    void updateQuantity(int quantity, int id);

    @Delete
    void deleteCartItem(CartItem cartItem);

    @Query("DELETE FROM cart")
    public void nukeTable();
}
