package com.derich.matatufy.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.derich.matatufy.AddStage;
import com.derich.matatufy.BoundLocationManager;
import com.derich.matatufy.FirebaseUI;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class Stages extends Fragment implements LifecycleOwner {
    private GoogleMap mMap;
    private FirebaseUser mUser;
   // LifecycleOwner lifecycleOwner;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(0.5143, 35.2698);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private boolean mLocationPermissionGranted;
    private LocationListener mGpsListener = new MyLocationListener();

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.


    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private Location mLastKnownLocation;
    private TextView tvFilters;
    private Context mContext;
    private boolean mStoragePermissionGranted;
    private String longitude;
    private String latitude;
    private Boolean open;
    private List<MarkerInfo> stagesList;
    private String openDays;
    private String from;
    private String closingTime;
    private String openingTime;
    private String saccoName;
    private boolean destinationFilter;
    private boolean fromFilter;
    private boolean saccoFilter;
    private boolean fareFilter;
    private QuerySnapshot querySnapshot;

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
        mContext = getContext();
        getLocationPermission();
        DevicePermission();
        open = false;
        tvFilters = rootView.findViewById(R.id.filters);
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
                getDeviceLocation();
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                updateLocationUI();
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
               getLocations();

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
                        title.setTextColor(Color.parseColor("#0BF5AB"));
                        SpannableString spanTitle = new SpannableString(marker.getTitle());
                        spanTitle.setSpan(new UnderlineSpan(),0,spanTitle.length(),0);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setTextSize(22);
                        title.setText(spanTitle);

                        TextView snippet = new TextView(mContext);
                        snippet.setTextColor(Color.WHITE);
                        snippet.setTypeface(null,Typeface.BOLD);
                        snippet.setTextSize(20);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(final LatLng latLng) {
                        if (mUser!=null){
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
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
                                builder.setTitle("Confirmation.")
                                        .setMessage("Are you sure you want to add a new stage to this location?")
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                getLocations();
                                            }
                                        })
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                //   Integer clickCount = (Integer) marker.getTag();
                                longitude = String.valueOf(latLng.longitude);
                                latitude = String.valueOf(latLng.latitude);
                                Intent addStage = new Intent(getContext(), AddStage.class);
                                addStage.putExtra("latitude", latitude);
                                addStage.putExtra("longitude", longitude);
                                startActivity(addStage);
                                            }
                                        });
                                AlertDialog alertDialogConfirm = builder.create();
                                alertDialogConfirm.show();
                                return true;
                            }
                        });
                        }
                    }
                        else {
                            Toast.makeText(getContext(),"Please login to continue",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), FirebaseUI.class));
                        }
                }

                });
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                        if (open){
                            marker.hideInfoWindow();
                            open = false;
                        }
                        else {
                            marker.showInfoWindow();
                            open = true;
                        }
                    return true;
                    }
                });
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        LatLng markerPos = marker.getPosition();
                        latitude = String.valueOf(markerPos.latitude);
                        longitude = String.valueOf(markerPos.longitude);
                        if (mUser!=null){
                            if (mUser.getEmail().equals("alangitonga15@gmail.com") || mUser.getEmail().equals("mwanjirug25@gmail.com")){
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
                                builder.setTitle("Choose an action");
                                String[] options = {"Add New Destination","Delete destination","Edit destination info","View destinations info"};
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        switch (which){
                                            case 0:
                                                Intent add= new Intent(getContext(),AddStage.class);
                                                add.putExtra("latitude",latitude);
                                                add.putExtra("longitude",longitude);
                                                startActivity(add);
                                                break;
                                            case 1:
                                                db.collection("stages").document(AddStage.encode(latitude)+ ":" + AddStage.encode(longitude)).collection("allstages").get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()){
                                                                    final List<String> destns = new ArrayList<>();
                                                                  for (QueryDocumentSnapshot doc: task.getResult())
                                                                      destns.add(doc.getId());
                                                                  AlertDialog.Builder builderDestns = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
                                                                  builderDestns.setTitle("Destinations on the stage");
                                                                  String[] listDestns = destns.toArray(new String[destns.size()]);
                                                                  builderDestns.setItems(listDestns, new DialogInterface.OnClickListener() {
                                                                      @Override
                                                                      public void onClick(DialogInterface dialog, int which) {
                                                                          String destine = destns.get(which);
                                                                          db.collection("stages").document(AddStage.encode(latitude)+ ":" + AddStage.encode(longitude)).collection("allstages").document(destine)
                                                                                  .delete()
                                                                                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                      @Override
                                                                                      public void onSuccess(Void aVoid) {
                                                                                          Toast.makeText(getContext(),    "Destination deleted successfully", Toast.LENGTH_SHORT).show();
                                                                                      }
                                                                                  })
                                                                                  .addOnFailureListener(new OnFailureListener() {
                                                                                      @Override
                                                                                      public void onFailure(@NonNull Exception e) {
                                                                                          Toast.makeText(getContext(),    "Error" + e, Toast.LENGTH_SHORT).show();
                                                                                      }
                                                                                  });
                                                                      }
                                                                  });
                                                                  AlertDialog dialog1 = builderDestns.create();
                                                                  dialog1.show();
                                                                }
                                                                else {
                                                                    Toast.makeText(getContext(),    "Error getting associated destinations", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                        break;
                                            case 3:
                                                displayStageInfo();
                                                break;

                                        }
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                            else {
                                displayStageInfo();

                            }
                        }
                        else {
                            Toast.makeText(getContext(),    "Please login to get more information on this stage", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        tvFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MarkerInfo> stageInfo = new ArrayList<>();
                MarkerInfo markerInfo;
                List<String> spinnerFrom =  new ArrayList<>();
                List<String> spinnerDestn =  new ArrayList<>();
                List<String> spinnerSacco =  new ArrayList<>();
                spinnerFrom.add("* From");
                spinnerDestn.add("* Destination");
                spinnerSacco.add("Sacco name");
                if (!querySnapshot.isEmpty()) {
                    for (DocumentSnapshot snapshot : querySnapshot)
                        stageInfo.add(snapshot.toObject(MarkerInfo.class));
                    int size = stageInfo.size();
                    int position=0;
                    for (position=0;position<size;position++){
                        markerInfo= stageInfo.get(position);
                        spinnerFrom.add(markerInfo.from);
                        spinnerDestn.add(markerInfo.destination);
                        spinnerSacco.add(markerInfo.sName);
                    }

                } else {
                    Toast.makeText(getContext(),"No data found.",Toast.LENGTH_LONG).show();
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
                Context context = getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                final Spinner fromBox = new Spinner(context);
                final Spinner destinationBox = new Spinner(context);
                final Spinner saccoBox = new Spinner(context);
                ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(mContext,R.layout.my_spinner_item,spinnerFrom);
                ArrayAdapter<String> toAdapter = new ArrayAdapter<>(mContext,R.layout.my_spinner_item,spinnerDestn);
                ArrayAdapter<String> saccoAdapter = new ArrayAdapter<>(mContext,R.layout.my_spinner_item,spinnerSacco);
                fromAdapter.setDropDownViewResource(R.layout.my_spinner_item);
                toAdapter.setDropDownViewResource(R.layout.my_spinner_item);
                saccoAdapter.setDropDownViewResource(R.layout.my_spinner_item);
                fromBox.setAdapter(fromAdapter);
                destinationBox.setAdapter(toAdapter);
                saccoBox.setAdapter(saccoAdapter);
                final EditText fareBox = new EditText(context);
                fareBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                fareBox.setHint("Fare");
                final TextView mandatory = new TextView(context);
                mandatory.setText("* fields are mandatory");
                layout.addView(fromBox);
                layout.addView(destinationBox);
                layout.addView(saccoBox);
                layout.addView(fareBox);
                layout.addView(mandatory);
                dialog.setTitle("Filters")
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String from = fromBox.getSelectedItem().toString();
                        String destn= destinationBox.getSelectedItem().toString();
                        String sacco= saccoBox.getSelectedItem().toString();
                        String fare = fareBox.getText().toString();
                        if (from.equals("* From") || destn.equals("* Destination")){
                            Toast.makeText(mContext,"Sorry from and destination fields cannot be empty",Toast.LENGTH_LONG).show();
                        }
                        else {
                            if (!(sacco.equals("Sacco name")) && !(fare.isEmpty())){
                                db.collectionGroup("allstages").whereEqualTo("from",from).whereEqualTo("destination",destn).whereEqualTo("sName",sacco).whereEqualTo("price",fare).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                queryDocSnapShotMethod(queryDocumentSnapshots);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(),"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                            else if (sacco.equals("Sacco name") && !(fare.isEmpty())){
                                db.collectionGroup("allstages").whereEqualTo("from",from).whereEqualTo("destination",destn).whereEqualTo("price",fare).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                queryDocSnapShotMethod(queryDocumentSnapshots);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(),"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }
                            else if(fare.isEmpty() && !(sacco.equals("Sacco name"))){
                                db.collectionGroup("allstages").whereEqualTo("from",from).whereEqualTo("destination",destn).whereEqualTo("sName",sacco).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                queryDocSnapShotMethod(queryDocumentSnapshots);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(),"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }
                            else{
                                db.collectionGroup("allstages").whereEqualTo("from",from).whereEqualTo("destination",destn).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                queryDocSnapShotMethod(queryDocumentSnapshots);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(),"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", null);
                dialog.setView(layout);
                dialog.show();

            }
        });

        return rootView;
    }

    private void getLocations() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        stagesList = new ArrayList<>();
        db.collectionGroup("allstages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "listen:error", e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                        case MODIFIED:
                        case REMOVED:
                            queryDocSnapShotMethod(queryDocumentSnapshots);
                            querySnapshot = queryDocumentSnapshots;
                            break;
                    }
                }

            }
        });

    }

    private void queryDocSnapShotMethod(QuerySnapshot queryDocumentSnapshots) {
        mMap.clear();
        open = false;
        stagesList = new ArrayList<>();
        if (!queryDocumentSnapshots.isEmpty()){
            for (DocumentSnapshot snapshot:queryDocumentSnapshots)
                stagesList.add(snapshot.toObject(MarkerInfo.class));
            int size = stagesList.size();
            int position;
            String snip = "Destinations :" + "\n";
            for (position=0;position<size;position++) {
                MarkerInfo markerInfo = stagesList.get(position);
                if (position>0){
                    MarkerInfo previous = stagesList.get(position-1);
                    String markerPreviousPosition = previous.latitude + ":" + previous.longitude;
                    String markerCurrentPosition = markerInfo.latitude + ":" + markerInfo.longitude;
                    if (markerCurrentPosition.equals(markerPreviousPosition)){
                        snip = snip + markerInfo.destination + "\n";

                    }
                    else {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(previous.latitude),Double.parseDouble(previous.longitude)))
                                .icon(bitmapDescriptorFromVector(mContext,R.drawable.ic_local_taxi_black_24dp))
                                .title(previous.sName)
                                .snippet(snip)
                        );
                        snip = "Destinations :" + "\n" + markerInfo.destination + "\n";
                    }

                }
                else {
                    snip = snip + markerInfo.destination + "\n";
                }
            }
            if (position == size){
                MarkerInfo previous = stagesList.get(position-1);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(previous.latitude),Double.parseDouble(previous.longitude)))
                        .icon(bitmapDescriptorFromVector(mContext,R.drawable.ic_local_taxi_black_24dp))
                        .title(previous.sName)
                        .snippet(snip)
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(Double.parseDouble(previous.latitude),
                                Double.parseDouble(previous.longitude)), DEFAULT_ZOOM));

            }
        }
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(mContext, vectorResId);
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
                    bindLocationListener();
                }
                else {
                    Toast.makeText(mContext,"Location permission denied",Toast.LENGTH_LONG).show();
                }
            }
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mStoragePermissionGranted = true;
                }
                else {
                    DevicePermission();
                }
            }
        }
        updateLocationUI();
    }
    private void displayStageInfo(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
        int size = stagesList.size();

        int position=0;
        String markerCurrentPos = latitude + ":" + longitude;
        ArrayList<String> arrayList = new ArrayList<>();
        for (position=0;position<size;position++) {
            MarkerInfo markerInfo = stagesList.get(position);
            String markerCurrentPosition = markerInfo.latitude + ":" + markerInfo.longitude;
            if (markerCurrentPos.equals(markerCurrentPosition)) {
                saccoName = "Sacco name : " +markerInfo.sName;
                openingTime = "Opening time : "+markerInfo.openingT;
                from = "From : " + markerInfo.from;
                closingTime = "Closing time : "+markerInfo.closingT;
                openDays = "Open days : " +markerInfo.days + "\n";

                arrayList.add("Destination : "+markerInfo.destination);
                arrayList.add("Fare : " + markerInfo.price);
                arrayList.add("\n");

                final ArrayAdapter<String> aa1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrayList);

                builder.setAdapter(aa1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builderSelect = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
                        builderSelect.setTitle("Directions");
                        builderSelect.setMessage("Do you want to get the directions to the stage from your location?");
                        builderSelect.setCancelable(false);
                        builderSelect.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);

                            }
                        });
                        builderSelect.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog select = builderSelect.create();
                        select.show();
                    }
                });
            }
        }
        arrayList.add(saccoName);
        arrayList.add(openingTime);
        arrayList.add(from);
        arrayList.add(closingTime);
        arrayList.add(openDays);
        AlertDialog dialog1 = builder.create();
        dialog1.show();
    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            bindLocationListener();
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(mContext,"The app needs this permission to show you stages around you.",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
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
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            mStoragePermissionGranted = true;
        }
        else if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(mContext,"The app needs this permission to upload your profile image.",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        else {
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
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            //textView.setText(location.getLatitude() + ", " + location.getLongitude());
            mLastKnownLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getContext(),
                    "Provider enabled: " + provider, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }
    private void bindLocationListener() {
        BoundLocationManager.bindLocationListenerIn(this, mGpsListener, getContext());
    }

}