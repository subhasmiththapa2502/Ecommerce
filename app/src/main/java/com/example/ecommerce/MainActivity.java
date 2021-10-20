package com.example.ecommerce;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ecommerce.ui.HomeFragment;
import com.example.ecommerce.ui.ProfileFragment;
import com.example.ecommerce.utils.AppConstants;
import com.example.ecommerce.utils.Prefs;
import com.example.ecommerce.utils.Utils;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.chipNav)
    ChipNavigationBar chipNavigationBar;


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

        chipNavigationBar.setItemSelected(R.id.home,
                true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.your_placeholder,
                        new HomeFragment()).commit();
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
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.your_placeholder,
                                        fragment).commit();
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
}