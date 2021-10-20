package com.example.ecommerce.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ecommerce.R;
import com.example.ecommerce.utils.AppConstants;
import com.example.ecommerce.utils.Prefs;

import java.util.Objects;

/**
 * Created by Subhasmith Thapa on 19,October,2021
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    CardView card_view1, card_view2,card_view3;
    LinearLayoutCompat llProfile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_profile, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        init();
    }

    public void init(){
        card_view1 = requireActivity().findViewById(R.id.card_view1);
        card_view1.setOnClickListener(view -> toggleRTLMethod());

        card_view2 = requireActivity().findViewById(R.id.card_view2);
        card_view2.setOnClickListener(view -> openNotificationAndSettingPage());

        card_view3 = requireActivity().findViewById(R.id.card_view3);
        card_view3.setOnClickListener(view -> openAddressListingPage());

        llProfile = requireActivity().findViewById(R.id.llProfile);

    }

    public void openAddressListingPage(){

        // Begin the transaction
        Intent intent = new Intent(requireActivity(), AddressListActivity.class);
        startActivity(intent);
//        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top);
//        fragmentTransaction.replace(R.id.your_placeholder, new AddressListingFragment());
//        fragmentTransaction.commit();

    }

    public void openNotificationAndSettingPage(){
        //Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        //intent.setData(uri);
        //startActivity(intent);

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    public void toggleRTLMethod(){

        if (ViewCompat.getLayoutDirection(requireView()) == ViewCompat.LAYOUT_DIRECTION_LTR) {
            //IF LEFT TO RIGHT
            Prefs.putString(AppConstants.LANGUAGE,AppConstants.LANGUAGE_ARABIC);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                requireActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
        else {
            //IF RIGHT TO LEFT
            Prefs.putString(AppConstants.LANGUAGE,AppConstants.LANGUAGE_ENGLISH);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                requireActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

        }


    }

    @Override
    public void onClick(View view) {

    }
}
