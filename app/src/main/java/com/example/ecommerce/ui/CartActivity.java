package com.example.ecommerce.ui;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.adapter.CartAdapterWithFooter;
import com.example.ecommerce.database.CartItem;
import com.example.ecommerce.database.DatabaseClient;
import com.example.ecommerce.utils.PaginationAdapterCallback;
import com.example.ecommerce.utils.Utils;

import java.util.List;
import java.util.Objects;

public class CartActivity extends AppCompatActivity implements CartAdapterWithFooter.CartInterface, PaginationAdapterCallback, CartAdapterWithFooter.FooterClickListener {
    private RecyclerView recyclerView;
    CartAdapterWithFooter adapter;
    private ProgressBar movie_progress;
    RelativeLayout rlEmpty,rlIfDataPresent;
    Button explore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.rvCart);
        movie_progress = findViewById(R.id.movie_progress);
        rlEmpty = findViewById(R.id.rlEmpty);
        rlIfDataPresent = findViewById(R.id.ifDataPresent);
        explore = findViewById(R.id.explore);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        init();
        getCartItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.checkForRTL(this);
    }

    public void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Cart");
        Utils.changeStatusBarColour(this);

        explore.setOnClickListener(view -> {
            Utils.openMainPage(this);
        });
    }

    private void updateTask(final CartItem item, int quantity) {

        Log.d("CART ITEM QUANTITY", String.valueOf(quantity));
        @SuppressLint("StaticFieldLeak")
        class UpdateTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                movie_progress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                int cost = item.getUnitCost()*quantity;
                item.setTotalCost(cost);
                item.setId(item.getId());
                item.setImagePath(item.getImagePath());
                item.setTitle(item.getTitle());
                item.setQuantity(quantity);
                DatabaseClient.getInstance(getApplicationContext()).getCartItemDataBase()
                        .cartItemDao()
                        .updateCartItem(item);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                movie_progress.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();

                finish();
                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);

            }
        }

        UpdateTask ut = new UpdateTask();
        ut.execute();
    }


    private void deleteTask(final CartItem cartItem) {
        class DeleteTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                movie_progress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                DatabaseClient.getInstance(getApplicationContext()).getCartItemDataBase()
                        .cartItemDao()
                        .deleteCartItem(cartItem);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                movie_progress.setVisibility(View.GONE);
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                getCartItems();
                //finish();
                //startActivity(new Intent(UpdateTaskActivity.this, MainActivity.class));
            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();

    }

    private void getCartItems() {
        class GetTasks extends AsyncTask<Void, Void, List<CartItem>> {

            @Override
            protected void onPreExecute() {
                movie_progress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected List<CartItem> doInBackground(Void... voids) {

                List<CartItem> taskList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getCartItemDataBase()
                        .cartItemDao()
                        .getCartItems();
                return taskList;
            }

            @Override
            protected void onPostExecute(List<CartItem> tasks) {
                if (tasks.isEmpty()){
                    rlIfDataPresent.setVisibility(View.GONE);
                    rlEmpty.setVisibility(View.VISIBLE);
                }else{
                    rlIfDataPresent.setVisibility(View.VISIBLE);
                    rlEmpty.setVisibility(View.GONE);
                }
                super.onPostExecute(tasks);
                movie_progress.setVisibility(View.GONE);
                adapter = new CartAdapterWithFooter(CartActivity.this);
                adapter.addAll(tasks);
                adapter.addLoadingFooter();
                recyclerView.setAdapter(adapter);
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    @Override
    public void deleteItem(CartItem cartItem) {
        deleteTask(cartItem);

    }

    @Override
    public void changeQuantity(CartItem cartItem, int quantity) {
        updateTask(cartItem, quantity);
    }

    @Override
    public void retryPageLoad() {

    }

    @Override
    public void onProceedClicked() {
/*
        OrderConfirmationFragment orderConfirmationFragment =
                OrderConfirmationFragment.newInstance();
        orderConfirmationFragment.show(getSupportFragmentManager(),
                "add_photo_dialog_fragment");
*/

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top);
        fragmentTransaction.add(R.id.your_placeholder, new OrderConfirmationFragment());
        fragmentTransaction.commit();
    }
}