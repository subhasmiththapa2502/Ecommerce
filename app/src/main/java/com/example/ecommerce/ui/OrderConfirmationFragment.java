package com.example.ecommerce.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ecommerce.MainActivity;
import com.example.ecommerce.R;
import com.example.ecommerce.database.DatabaseClient;
import com.example.ecommerce.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Created by Subhasmith Thapa on 22,October,2021
 */
public class OrderConfirmationFragment extends BottomSheetDialogFragment {

    Button button;
    public static OrderConfirmationFragment newInstance(){
        return new OrderConfirmationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container,
                false);

        // get the views and attach the listener

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
     button = requireActivity().findViewById(R.id.orderConfirmationClicked);

     button.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

             deleteCartItems();
         }
     });
    }


    private void deleteCartItems() {
        class DeleteCartItems extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                 DatabaseClient
                        .getInstance(requireActivity())
                        .getCartItemDataBase()
                        .cartItemDao()
                        .nukeTable();

                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                //requireActivity().finish();
                openMainPage();

            }
        }

        DeleteCartItems gt = new DeleteCartItems();
        gt.execute();
    }

    private void openMainPage(){
        Utils.openMainPage(requireActivity());
/*
        Intent i = new Intent(requireActivity(), MainActivity.class);        // Specify any activity here e.g. home or splash or login etc
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("EXIT", true);
        startActivity(i);
        requireActivity().finish();
*/
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            Utils.checkForRTL(requireActivity());
        }
    }
}
