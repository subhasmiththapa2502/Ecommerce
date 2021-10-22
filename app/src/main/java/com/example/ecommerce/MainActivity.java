package com.example.ecommerce;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements PaginationAdapterCallback {
    @BindView(R.id.chipNav)
    ChipNavigationBar chipNavigationBar;

    int cart_count = 2;
    private NotificationManagerCompat mNotificationManagerCompat;
    private BottomSheetDialog mBottomSheetDialog;


    @RequiresApi(api = Build.VERSION_CODES.O)
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
        mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener
                (i -> {
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotification(){

        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
// Create a notification and set the notification channel.
        Notification notification = new Notification.Builder(MainActivity.this)
                .setContentTitle("New Message")
                .setContentText("Welcome to Infinity Movies")
                .setSmallIcon(R.drawable.ic_shopping_cart)
                .setChannelId(CHANNEL_ID)
                .build();



        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(mChannel);

            mNotificationManager.createNotificationChannel(mChannel);

// Issue the notification.
            mNotificationManager.notify(notifyID , notification);

        }
    }
    public void openNotificationAndSettingPage(){
        //Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        //intent.setData(uri);
        //startActivity(intent);

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void askForNotificationPermission(){
        boolean areNotificationsEnabled = mNotificationManagerCompat.areNotificationsEnabled();

        View bottomSheet = findViewById(R.id.your_placeholder);


        final View bottomSheetLayout = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
        (bottomSheetLayout.findViewById(R.id.button_allow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mBottomSheetDialog.dismiss();
                if (!areNotificationsEnabled){
                    openNotificationAndSettingPage();
                }else{
                    //ConstraintLayout viewCons= findViewById(R.id.constraint);
                    //Utils.showSnackBar(viewCons,AppConstants.PERMISSION_MESSAGE);
                }
                mBottomSheetDialog.dismiss();
            }
        });
        (bottomSheetLayout.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!areNotificationsEnabled){
                    openNotificationAndSettingPage();
                    mBottomSheetDialog.dismiss();
                }
//                Toast.makeText(getApplicationContext(), "Ok button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(bottomSheetLayout);
        mBottomSheetDialog.show();
/*            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Allow Ecommerce App to send you push Notification");
// Add the buttons
            builder.setPositiveButton(R.string.ok, (dialog, id) -> {
                if (!areNotificationsEnabled){
                    openNotificationAndSettingPage();
                }
                // User clicked OK button
                createNotification();
            });
            builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
                // User cancelled the dialog
            });
// Set other dialog properties


// Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();*/
    }

    @Override
    public void retryPageLoad() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.checkForRTL(this);
    }
}