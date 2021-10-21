package com.example.ecommerce.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Created by Subhasmith Thapa on 21,October,2021
 */
@Database(entities = CartItem.class, exportSchema = false, version = 1)
public abstract class CartItemDataBase extends RoomDatabase {

    public abstract CartItemDao cartItemDao();
}

