package com.example.ecommerce.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.adapter.AddressListAdapter;
import com.example.ecommerce.utils.AppConstants;
import com.example.ecommerce.utils.NetworkUtil;
import com.example.ecommerce.utils.Prefs;
import com.example.ecommerce.utils.TinyDB;
import com.example.ecommerce.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressListActivity extends AppCompatActivity implements MapViewFragment.OnLocationSelectedListener, AddressListAdapter.OnItemClickListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rvAddressList)
    RecyclerView mRecyclerView;

    AddressListAdapter myRecyclerViewAdapter;
    LinearLayoutManager mLayoutManager;
    ArrayList<String> address = new ArrayList<>();
    TinyDB tinydb;
    ProgressBar progress_circular;
    RelativeLayout errorLayout;
    TextView tvToolbar;
    private static String TAG = AddressListActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        ButterKnife.bind(this);
        init();
        setUp();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void checkForRTL() {
        String language = Prefs.getString(AppConstants.LANGUAGE);
        if (language.equals(AppConstants.LANGUAGE_ARABIC)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }
    }
    private void setUp() {
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);


        myRecyclerViewAdapter = new AddressListAdapter(this);
        myRecyclerViewAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(myRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        for (String str : address) {
            if (!str.equals("")) {
                if (myRecyclerViewAdapter.getItemCount() > 1) {
                    myRecyclerViewAdapter.add(myRecyclerViewAdapter.getItemCount(), str);
                } else {
                    myRecyclerViewAdapter.add(0, str);
                }
            }
        }

        if (address.isEmpty()){
            errorLayout.setVisibility(View.VISIBLE);
            tvToolbar.setVisibility(View.GONE);

        }else {
            errorLayout.setVisibility(View.GONE);
            tvToolbar.setVisibility(View.VISIBLE);
        }

    }

    public void init() {
        tinydb = new TinyDB(AddressListActivity.this);
        if (tinydb.getListString(AppConstants.LOCATION_LIST) != null) {
            address = tinydb.getListString(AppConstants.LOCATION_LIST);
        }
        progress_circular = findViewById(R.id.progress_circular);
        customiseToolBar();
        errorLayout = findViewById(R.id.error_layout);
        tvToolbar = findViewById(R.id.tvToolbar);
        Utils.changeStatusBarColour(this);

        findViewById(R.id.addAddress).setOnClickListener(view -> {

            if(NetworkUtil.isNetworkConnected(this)){
                findViewById(R.id.addAddress).setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                progress_circular.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                Runnable runnable = () -> progress_circular.setVisibility(View.GONE);
                handler.postDelayed(runnable, 2000);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top);
                fragmentTransaction.replace(R.id.your_placeholder, new MapViewFragment());
                fragmentTransaction.addToBackStack(TAG);
                fragmentTransaction.commit();
            }else{
                Toast.makeText(this, AppConstants.NO_INTERNET_MESSAGE, Toast.LENGTH_SHORT).show();
                //ConstraintLayout viewCons= findViewById(R.id.constraint);
                //Utils.showSnackBar(viewCons,AppConstants.NO_INTERNET_MESSAGE);

            }


        });

        if(NetworkUtil.isNetworkConnected(this)){

        }else{
            Toast.makeText(this, AppConstants.NO_INTERNET_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }

    public void customiseToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        // Customize the back button
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.checkForRTL(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLocationSelected(String location) {
        if (NetworkUtil.isNetworkConnected(this)){
            findViewById(R.id.addAddress).setVisibility(View.VISIBLE);
            tvToolbar.setVisibility(View.VISIBLE);
            address.add(location);
            tinydb.putListString(AppConstants.LOCATION_LIST, address);
            progress_circular.setVisibility(View.GONE);
            if (!location.equals("")) {
                if (myRecyclerViewAdapter.getItemCount() > 1) {
                    myRecyclerViewAdapter.add(myRecyclerViewAdapter.getItemCount(), location);
                } else {
                    myRecyclerViewAdapter.add(0, location);
                }
            }
        }else {
            findViewById(R.id.addAddress).setVisibility(View.VISIBLE);

            Toast.makeText(this, AppConstants.NO_INTERNET_MESSAGE, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onItemClick(AddressListAdapter.ItemHolder item, int position) {

    }
}