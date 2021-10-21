package com.example.ecommerce.database;

import android.content.Context;

import androidx.room.Room;

/**
 * Created by Subhasmith Thapa on 21,October,2021
 */
public class DatabaseClient {
    private Context context;
    private static DatabaseClient databaseClient;

    private CartItemDataBase cartItemDataBase;
    private static final String DB_NAME = "cartItem_Db";



    private DatabaseClient(Context context){
        this.context = context;
        cartItemDataBase = Room.databaseBuilder(context.getApplicationContext(), CartItemDataBase.class,DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context){
        if (databaseClient == null) {
            databaseClient = new DatabaseClient(context);
        }
        return databaseClient;
    }

    public CartItemDataBase getCartItemDataBase(){
        return cartItemDataBase;
    }
}
