package com.example.ecommerce.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.ecommerce.R;
import com.example.ecommerce.utils.AppConstants;
import com.example.ecommerce.utils.NetworkUtil;
import com.example.ecommerce.utils.Prefs;
import com.example.ecommerce.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Subhasmith Thapa on 20,October,2021
 */
public class MapViewFragment extends BottomSheetDialogFragment implements OnMapReadyCallback {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;

    private MapView mapView;
    private GoogleMap gmap;
    private LatLng latLng;
    private LocationManager locationManager;
    private String locationSelected;
    private SearchView searchView;

    OnLocationSelectedListener mCallback;
    private BottomSheetDialog mBottomSheetDialog;

    // Container Activity must implement this interface
    public interface OnLocationSelectedListener {
        void onLocationSelected(String location);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container context has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnLocationSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void checkIfLocationServices() {
        LocationManager lm = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            final View bottomSheetLayout = getLayoutInflater().inflate(R.layout.location_bottom_sheet, null);
            (bottomSheetLayout.findViewById(R.id.button_allow)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requireActivity().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    mBottomSheetDialog.dismiss();
                }
            });
            (bottomSheetLayout.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            mBottomSheetDialog = new BottomSheetDialog(requireActivity());
            mBottomSheetDialog.setContentView(bottomSheetLayout);
            mBottomSheetDialog.show();

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        checkLocationPermission();
        return inflater.inflate(R.layout.add_address, parent, false);

    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        checkIfLocationServices();
        mapView = requireView().findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        searchView = requireView().findViewById(R.id.idSearchView);

        //SupportMapFragment mapFragment = (SupportMapFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.mapView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                // checking if the entered location is null or not.
                if (location != null || location.equals("")) {
                    // on below line we are creating and initializing a geo coder.
                    Geocoder geocoder = new Geocoder(requireActivity());
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // on below line we are getting the location
                    // from our list a first position.
                    if (NetworkUtil.hasNetwork(requireActivity())) {
                        if (addressList.get(0) != null) {
                            Address address = addressList.get(0);

                            // on below line we are creating a variable for our location
                            // where we will add our locations latitude and longitude.
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                            locationSelected = getCompleteAddressString(latLng.latitude, latLng.longitude);
                            // on below line we are adding marker to that position.
                            gmap.addMarker(new MarkerOptions().position(latLng).title(location));

                            // below line is to animate camera to that position.
                            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        } else {
                            Toast.makeText(requireActivity(), AppConstants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();


                        }
                    } else {
                        Toast.makeText(requireActivity(), AppConstants.NO_INTERNET_MESSAGE, Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        requireActivity().findViewById(R.id.chooseLocation).setOnClickListener(view1 -> {
            Prefs.putString(AppConstants.RECENT_LOCATION, locationSelected);
            //requireActivity().finish();


            mCallback.onLocationSelected(locationSelected);
            FragmentManager manager = requireActivity().getSupportFragmentManager();
            manager.beginTransaction().remove(this).commit();
        });

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(requireActivity())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(requireActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden){
            Utils.checkForRTL(requireActivity());
        }


        checkIfLocationServices();


        mapView.getMapAsync(this);
        searchView = requireView().findViewById(R.id.idSearchView);

        //SupportMapFragment mapFragment = (SupportMapFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.mapView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                // checking if the entered location is null or not.
                if (location != null || location.equals("")) {
                    // on below line we are creating and initializing a geo coder.
                    Geocoder geocoder = new Geocoder(requireActivity());
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // on below line we are getting the location
                    // from our list a first position.
                    if (NetworkUtil.hasNetwork(requireActivity())) {
                        if (addressList.get(0) != null) {
                            Address address = addressList.get(0);

                            // on below line we are creating a variable for our location
                            // where we will add our locations latitude and longitude.
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                            locationSelected = getCompleteAddressString(latLng.latitude, latLng.longitude);
                            // on below line we are adding marker to that position.
                            gmap.addMarker(new MarkerOptions().position(latLng).title(location));

                            // below line is to animate camera to that position.
                            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        } else {
                            Toast.makeText(requireActivity(), AppConstants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();


                        }
                    } else {
                        Toast.makeText(requireActivity(), AppConstants.NO_INTERNET_MESSAGE, Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        requireActivity().findViewById(R.id.chooseLocation).setOnClickListener(view1 -> {
            Prefs.putString(AppConstants.RECENT_LOCATION, locationSelected);
            //requireActivity().finish();


            mCallback.onLocationSelected(locationSelected);
            FragmentManager manager = requireActivity().getSupportFragmentManager();
            manager.beginTransaction().remove(this).commit();
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

/*
    private void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                LatLng latLng = gmap.getCameraPosition().target;
                Geocoder geocoder = new Geocoder(requireActivity());

                try {
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        String locality = addressList.get(0).getAddressLine(0);
                        String country = addressList.get(0).getCountryName();
                        if (!locality.isEmpty() && !country.isEmpty()){}
                            //resutText.setText(locality + "  " + country);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }
*/


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(0);

        animateCamera();

        gmap.getUiSettings().setMyLocationButtonEnabled(false);
        latLng = gmap.getCameraPosition().target;
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 880, 180, 0);

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setZoomGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gmap.setMyLocationEnabled(true);

        gmap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                requireActivity().findViewById(R.id.imgLocationPinUp).setVisibility(View.VISIBLE);
                // Cleaning all the markers.
                if (gmap != null) {
                    gmap.clear();
                }
                //Toast.makeText(requireActivity(), "Camera is moving", Toast.LENGTH_SHORT).show();
            }
        });

        gmap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Cleaning all the markers.
                if (gmap != null) {
                    gmap.clear();
                }
                requireActivity().findViewById(R.id.imgLocationPinUp).setVisibility(View.GONE);
                LatLng mPosition = gmap.getCameraPosition().target;
                gmap.addMarker(new MarkerOptions()
                        .position(mPosition)
                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_baseline_add_location_24)));

                //Toast.makeText(requireActivity(), "mPosition is" + mPosition, Toast.LENGTH_SHORT).show();

                double latitude = mPosition.latitude;
                double longitude = mPosition.longitude;

                locationSelected = getCompleteAddressString(latitude, longitude);
//                Toast.makeText(requireActivity(), "mPosition is" + getCompleteAddressString(latitude,longitude), Toast.LENGTH_SHORT).show();
            }
        });

        //gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //gmap.addMarker(new MarkerOptions().position(latLng).title("Seattle"));

    }
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private Location getLastKnownLocation() {
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
    private void animateCamera() {
        Location location = getLastKnownLocation();
        if (location != null) {

            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            gmap.setMyLocationEnabled(true);
            gmap.getUiSettings().setMyLocationButtonEnabled(true);
            gmap.getUiSettings().setAllGesturesEnabled(true);
            //delay is for after map loaded animation starts
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));

                }
            }, 2000);
        }
    }
}
