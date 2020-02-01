package com.derich.matatufy.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.derich.matatufy.AddStage;
import com.derich.matatufy.MarkerInfo;
import com.derich.matatufy.R;
import com.derich.matatufy.RideShareInfo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Stages extends Fragment {
    private GoogleMap mMap;
    private FirebaseUser mUser;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(0.5143, 35.2698);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.


    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private Location mLastKnownLocation;
    private Context mContext;
    private boolean mStoragePermissionGranted;

    public Stages() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        getLocationPermission();
        DevicePermission();
        mContext = getContext();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        Places.initialize(getContext(), getString(R.string.google_maps_key));

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap map) {
                mMap = map;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                updateLocationUI();
                // Get the current location of the device and set the position of the map.
                getDeviceLocation();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                final List<MarkerInfo> questionsList=new ArrayList<>();
                db.collectionGroup("allstages").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                if (!queryDocumentSnapshots.isEmpty()){
                                    for (DocumentSnapshot snapshot:queryDocumentSnapshots)
                                        questionsList.add(snapshot.toObject(MarkerInfo.class));
                                    int size = questionsList.size();
                                    int position=0;
                                    for (position=0;position<size;position++) {
                                    MarkerInfo markerInfo = questionsList.get(position);
                                        mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble(markerInfo.latitude),Double.parseDouble(markerInfo.longitude)))
                                            .icon(bitmapDescriptorFromVector(getContext(),R.drawable.ic_local_taxi_black_24dp))
                                            .title(markerInfo.sName)
                                            .snippet("Destination :"+ markerInfo.destination + "\n" + "Fare :"+ markerInfo.price +"\n" + "Open time :" + markerInfo.openingT +":"+ markerInfo.closingT + "\n" + "Every :" + markerInfo.days)
                                    );
                                    }
                                }
                            }
                        });




                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        LinearLayout info = new LinearLayout(mContext);
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(mContext);
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(mContext);
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(final LatLng latLng) {
                        if (mUser!=null)
                        if (!(mUser.getEmail().isEmpty())){
                            if (mUser.getEmail().equals("alangitonga15@gmail.com") || mUser.getEmail().equals("mwanjirug25@gmail.com")){
                                MarkerOptions markerOptions= new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title("Click to add new stage");
                                 mMap.clear();
                                 mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                 mMap.addMarker(markerOptions);
                                 mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                Integer clickCount = (Integer) marker.getTag();
                                String longitude = String.valueOf(latLng.longitude);
                                String latitude = String.valueOf(latLng.latitude);
                                Intent addStage = new Intent(getContext(), AddStage.class);
                                addStage.putExtra("latitude",latitude);
                                addStage.putExtra("longitude",longitude);
                                startActivity(addStage);
                                return true;
                            }
                        });
                        }
                    }
                    }
                });
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (marker.isInfoWindowShown()){
                            marker.hideInfoWindow();
                        }
                        else {
                            marker.showInfoWindow();
                        }
                    }
                });

            }
        });

        return rootView;
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(this.getContext(), vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {

                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        mStoragePermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mStoragePermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private void DevicePermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            mStoragePermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }



}