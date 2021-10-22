package com.example.ecommerce;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.ecommerce.database.CartItem;
import com.example.ecommerce.database.DatabaseClient;
import com.example.ecommerce.ui.CartActivity;
import com.example.ecommerce.ui.ErrorFragment;
import com.example.ecommerce.ui.HomeFragment;
import com.example.ecommerce.ui.ProfileFragment;
import com.example.ecommerce.utils.AppConstants;
import com.example.ecommerce.utils.Converter;
import com.example.ecommerce.utils.NetworkUtil;
import com.example.ecommerce.utils.PaginationAdapterCallback;
import com.example.ecommerce.utils.Prefs;
import com.example.ecommerce.utils.Utils;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements PaginationAdapterCallback {
    @BindView(R.id.chipNav)
    ChipNavigationBar chipNavigationBar;

    int cart_count = 2;

    @Override
    protected void onResume() {
        super.onResume();
        if(Prefs.getBoolean(AppConstants.FIRST_TIME,true)){
            askForNotificationPermission();
            Prefs.putBoolean(AppConstants.FIRST_TIME,false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
        getCartCount();
        chipNavigationBar.setItemSelected(R.id.home,
                true);
        if (NetworkUtil.hasNetwork(this)){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.your_placeholder,
                            new HomeFragment()).commit();
        }else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.your_placeholder,
                            new ErrorFragment()).commit();
        }
        bottomMenu();
        /*// Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment
        ft.replace(R.id.your_placeholder, new ProfileFragment());
// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        ft.commit();*/
    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener
                (new ChipNavigationBar.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int i) {
                        Fragment fragment = null;
                        switch (i){
                            case R.id.home:
                                fragment = new HomeFragment();
                                break;
                            case R.id.profile:
                                fragment = new ProfileFragment();
                                break;
                        }
                        if (NetworkUtil.hasNetwork(MainActivity.this)){
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.your_placeholder,
                                            fragment).commit();
                        }else {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.your_placeholder,
                                            new ErrorFragment()).commit();
                        }
                        /*getSupportFragmentManager().beginTransaction()
                                .replace(R.id.your_placeholder,
                                        fragment).commit();*/
                    }
                });
    }
    public void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        Utils.changeStatusBarColour(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.cart);
        menuItem.setIcon(Converter.convertLayoutToImage(MainActivity.this, cart_count, R.drawable.ic_shopping_cart));

        return true;
    }
    private void getCartCount() {
        class GetTasks extends AsyncTask<Void, Void, List<CartItem>> {

            @Override
            protected void onPreExecute() {
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
                    cart_count = 0;
                }else{
                    cart_count = tasks.size();
                }
                super.onPostExecute(tasks);
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.cart:
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
                break;


        }
        return true;
    }

    public void askForNotificationPermission(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Allow Ecommerce App to send you push Notification");
// Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
// Set other dialog properties


// Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void retryPageLoad() {

    }
}