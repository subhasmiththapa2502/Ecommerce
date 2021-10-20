package com.example.ecommerce.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.adapter.AddressListAdapter;
import com.example.ecommerce.utils.AppConstants;
import com.example.ecommerce.utils.TinyDB;
import com.example.ecommerce.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressListActivity extends AppCompatActivity implements MapViewFragment.OnLocationSelectedListener, AddressListAdapter.OnItemClickListener {

    @BindView(R.id.rvAddressList)
    RecyclerView mRecyclerView;

    AddressListAdapter myRecyclerViewAdapter;
    LinearLayoutManager mLayoutManager;
    ArrayList<String> address = new ArrayList<>();
    TinyDB tinydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        ButterKnife.bind(this);
        init();
        setUp();
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
        for(String str : address){
            if(!str.equals("")){
                if(myRecyclerViewAdapter.getItemCount()>1){
                    myRecyclerViewAdapter.add(myRecyclerViewAdapter.getItemCount(), str);
                }else{
                    myRecyclerViewAdapter.add(0, str);
                }
            }
        }


    }

    public void init(){
        tinydb = new TinyDB(AddressListActivity.this);
        if(tinydb.getListString(AppConstants.LOCATION_LIST) != null){
            address = tinydb.getListString(AppConstants.LOCATION_LIST);
        }
        customiseToolBar();

        Utils.changeStatusBarColour(this);

        findViewById(R.id.addAddress).setOnClickListener(view -> {
        findViewById(R.id.addAddress).setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top);
        fragmentTransaction.add(R.id.your_placeholder, new MapViewFragment());
        fragmentTransaction.commit();

        });

    }

    public void customiseToolBar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        // Customize the back button
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        findViewById(R.id.addAddress).setVisibility(View.VISIBLE);

        address.add(location);
        tinydb.putListString(AppConstants.LOCATION_LIST, address);

        if(!location.equals("")){
            if(myRecyclerViewAdapter.getItemCount()>1){
                myRecyclerViewAdapter.add(myRecyclerViewAdapter.getItemCount(), location);
            }else{
                myRecyclerViewAdapter.add(0, location);
            }
        }

    }


    @Override
    public void onItemClick(AddressListAdapter.ItemHolder item, int position) {

    }
}