package com.derich.matatufy.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.derich.matatufy.AddRideShare;
import com.derich.matatufy.FirebaseUI;
import com.derich.matatufy.R;
import com.derich.matatufy.RideShareAdapter;
import com.derich.matatufy.RideShareInfo;
import com.derich.matatufy.RideShareMoreInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
public class RideShare extends Fragment implements RideShareAdapter.OnRideClickListener {
    private TextView tvFilters;
    private List<RideShareInfo> mRideshareInfo;
    private RecyclerView mRecyclerView;
    private FirebaseUser user;
    private FirebaseFirestore mFirestone;
    private List<String> mSpinnerDate;
    private List<String> mSpinnerFrom;
    private List<String> mSpinnerTo;
    private Boolean mApplyFrom;
    private Boolean mApplyTo;
    private Boolean mApplyDate;
    private String mFrom;
    private String mTo;
    private String mDate;

    public RideShare() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ride_share, container, false);
        FloatingActionButton fAdd = view.findViewById(R.id.fButtonAdd);
        mRecyclerView =view.findViewById(R.id.recyclerView_rideShares);
        tvFilters = view.findViewById(R.id.textViewRideShareFilters);
        mSpinnerDate = new ArrayList<>();
        mSpinnerFrom = new ArrayList<>();
        mSpinnerTo = new ArrayList<>();
        mApplyFrom= false;
        mApplyTo = false;
        mApplyDate = false;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null) {
            mFirestone = FirebaseFirestore.getInstance();
            populateRecyclerView();
        }
        else {
            Toast.makeText(getContext(), "Please Login to continue", Toast.LENGTH_SHORT).show();
            Intent intent= new Intent(getContext(),FirebaseUI.class);
            startActivity(intent);
        }

        fAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddRideShare.class);
                startActivity(intent);
            }
        });
        tvFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearDefaults();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
                builder.setTitle("Choose filters to apply.");

// Add a checkbox list
                String[] filters = {"From", "to", "date"};
                boolean[] checkedItems = {false, false, false};
                builder.setMultiChoiceItems(filters, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // The user checked or unchecked a box
                        if (which==0){
                                mApplyFrom = isChecked;
                            }
                            else if(which==1){
                                mApplyTo = isChecked;
                            }
                            else if(which==2){
                                mApplyDate = isChecked;
                            }

                        }
                });

// Add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user clicked OK
                        if (mApplyDate && mApplyFrom && mApplyTo){
                            fromToDateFilters();
                        }
                        else if (mApplyFrom && mApplyTo && !mApplyDate){
                            fromToFilters();

                        }
                        else if (mApplyFrom && mApplyDate && !mApplyTo){
                            fromDateFilters();

                        }
                        else if (mApplyTo && mApplyDate && !mApplyFrom){
                            toDateFilters();

                        }
                        else if (mApplyTo && !mApplyDate && !mApplyFrom){
                            toFilters();

                        }
                        else if(mApplyFrom && !mApplyTo && !mApplyDate){
                            fromFilters();

                        }
                        else if (mApplyDate && !mApplyTo && !mApplyFrom){
                            dateFilters();

                        }
                        else {
                            clearDefaults();
                            populateRecyclerView();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);

// Create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                           }
        });

                return view;
    }


    /*public static String decode(String date){
        return date.replace(",","/");
    }*/
    private void clearDefaults(){
        mApplyDate = false;
        mApplyTo = false;
        mApplyFrom = false;
    }
    private void fromToDateFilters(){
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mSpinnerFrom);
        AlertDialog.Builder from = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
        from.setTitle("From");
        //  String[] types = {"By Zip", "By Category"};
        from.setAdapter(fromAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mFrom = mSpinnerFrom.get(which);
                tvFilters.setText(R.string.tap_filters);
                dialog.dismiss();
                populateRecyclerView();

            }

        });

        from.show();
        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mSpinnerTo);
        AlertDialog.Builder to = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
        to.setTitle("Destination");
        to.setAdapter(toAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTo = mSpinnerTo.get(which);
                tvFilters.setText(R.string.tap_filters);
                dialog.dismiss();

            }

        });

        to.show();
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mSpinnerDate);
        AlertDialog.Builder date = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
        date.setTitle("Date");
        date.setAdapter(dateAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDate = mSpinnerDate.get(which);
                tvFilters.setText(R.string.tap_filters);
                dialog.dismiss();

            }

        });

        date.show();
    }
    private void fromToFilters(){
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mSpinnerFrom);
        AlertDialog.Builder from = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
        from.setTitle("From");
        //  String[] types = {"By Zip", "By Category"};
        from.setAdapter(fromAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int dee = mSpinnerFrom.size();
                mFrom = mSpinnerFrom.get(which);
                tvFilters.setText(R.string.tap_filters);
                dialog.dismiss();
                populateRecyclerView();

            }

        });

        from.show();
        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mSpinnerTo);
        AlertDialog.Builder to = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
        to.setTitle("Destination");
        to.setAdapter(toAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTo = mSpinnerTo.get(which);
                tvFilters.setText(R.string.tap_filters);
                dialog.dismiss();

            }

        });

        to.show();



    }
    private void fromDateFilters(){
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mSpinnerFrom);
        AlertDialog.Builder from = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
        from.setTitle("From");
        //  String[] types = {"By Zip", "By Category"};
        from.setAdapter(fromAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mFrom = mSpinnerFrom.get(which);
                tvFilters.setText(R.string.tap_filters);
                dialog.dismiss();
                populateRecyclerView();

            }

        });

        from.show();
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mSpinnerDate);
        AlertDialog.Builder date = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
        date.setTitle("Date");
        date.setAdapter(dateAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDate = mSpinnerDate.get(which);
                tvFilters.setText(R.string.tap_filters);
                dialog.dismiss();

            }

        });

        date.show();


    }
    private void toDateFilters(){

        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mSpinnerTo);
        AlertDialog.Builder to = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
        to.setTitle("Destination");
        to.setAdapter(toAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTo = mSpinnerTo.get(which);
                tvFilters.setText(R.string.tap_filters);
                dialog.dismiss();
                populateRecyclerView();
            }

        });

        to.show();
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mSpinnerDate);
        AlertDialog.Builder date = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
        date.setTitle("Date");
        date.setAdapter(dateAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDate = mSpinnerDate.get(which);
                tvFilters.setText(R.string.tap_filters);
                dialog.dismiss();

            }

        });

        date.show();


    }
    private void fromFilters(){
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mSpinnerFrom);
        AlertDialog.Builder from = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
        from.setTitle("From");
        //  String[] types = {"By Zip", "By Category"};
        from.setAdapter(fromAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
               int dee = mSpinnerFrom.size();
                mFrom = mSpinnerFrom.get(which);
                tvFilters.setText(R.string.tap_filters);
                dialog.dismiss();
                populateRecyclerView();

            }

        });

        from.show();


    }
    private void toFilters(){
        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mSpinnerTo);
        AlertDialog.Builder to = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
        to.setTitle("Destination");
        to.setAdapter(toAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTo = mSpinnerTo.get(which);
                tvFilters.setText(R.string.tap_filters);
                dialog.dismiss();
                populateRecyclerView();

            }

        });

        to.show();


    }
    private void dateFilters(){
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mSpinnerDate);
        AlertDialog.Builder date = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
        date.setTitle("Date");
        date.setAdapter(dateAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDate = mSpinnerDate.get(which);
                tvFilters.setText(R.string.tap_filters);
                dialog.dismiss();
                populateRecyclerView();

            }

        });

        date.show();

    }

    private void populateRecyclerView(){
        if (user !=null) {
            mRideshareInfo=new ArrayList<>();
            String collectionId="all rideshares";
            if (mApplyFrom && mApplyTo && mApplyDate){
                mFirestone.collectionGroup(collectionId).whereEqualTo("from",mFrom).whereEqualTo("destination",mTo).whereEqualTo("date",mDate).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mRideshareInfo.add(snapshot.toObject(RideShareInfo.class));
                            populate();
                        } else {
                       Toast.makeText(getContext(),"No data found.",Toast.LENGTH_LONG).show();
                       populate();
                    }
                }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                            }
                        });
        }
            else if (mApplyFrom && mApplyTo && !mApplyDate){
                mFirestone.collectionGroup(collectionId).whereEqualTo("from",mFrom).whereEqualTo("destination",mTo).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                        mRideshareInfo.add(snapshot.toObject(RideShareInfo.class));
                                    populate();
                                } else {
                                    Toast.makeText(getContext(),"No data found.",Toast.LENGTH_LONG).show();
                                    populate();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                            }
                        });

            }
            else if (mApplyFrom && mApplyDate && !mApplyTo){
                mFirestone.collectionGroup(collectionId).whereEqualTo("from",mFrom).whereEqualTo("date",mDate).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                        mRideshareInfo.add(snapshot.toObject(RideShareInfo.class));
                                    populate();
                                } else {
                                    Toast.makeText(getContext(),"No data found.",Toast.LENGTH_LONG).show();
                                    populate();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                            }
                        });

            }
            else if (mApplyTo && mApplyDate && !mApplyFrom){
                mFirestone.collectionGroup(collectionId).whereEqualTo("destination",mTo).whereEqualTo("date",mDate).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                        mRideshareInfo.add(snapshot.toObject(RideShareInfo.class));
                                    populate();
                                } else {
                                    Toast.makeText(getContext(),"No data found.",Toast.LENGTH_LONG).show();
                                    populate();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                            }
                        });

            }
            else if (mApplyTo && !mApplyDate && !mApplyFrom){
                mFirestone.collectionGroup(collectionId).whereEqualTo("destination",mTo).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                        mRideshareInfo.add(snapshot.toObject(RideShareInfo.class));
                                    populate();
                                } else {
                                    Toast.makeText(getContext(),"No data found.",Toast.LENGTH_LONG).show();
                                    populate();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                            }
                        });


            }
            else if(mApplyFrom && !mApplyTo && !mApplyDate){
                mFirestone.collectionGroup(collectionId).whereEqualTo("from",mFrom).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                        mRideshareInfo.add(snapshot.toObject(RideShareInfo.class));
                                    populate();
                                } else {
                                    Toast.makeText(getContext(),"No data found.",Toast.LENGTH_LONG).show();
                                    populate();
                                }
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                    }
                });

            }
            else if (mApplyDate && !mApplyFrom && !mApplyTo){
                mFirestone.collectionGroup(collectionId).whereEqualTo("date",mDate).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                        mRideshareInfo.add(snapshot.toObject(RideShareInfo.class));
                                    populate();
                                } else {
                                    Toast.makeText(getContext(),"No data found.",Toast.LENGTH_LONG).show();
                                    populate();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                            }
                        });

            }

            else {
                mSpinnerDate = new ArrayList<>();
                mSpinnerFrom = new ArrayList<>();
                mSpinnerTo = new ArrayList<>();
                mFirestone.collectionGroup(collectionId).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                        mRideshareInfo.add(snapshot.toObject(RideShareInfo.class));
                                    populate();
                                    int size = mRideshareInfo.size();
                                    int position;
                                    for (position=0;position<size;position++){
                                        RideShareInfo rideShareInfo= mRideshareInfo.get(position);
                                        mSpinnerDate.add(rideShareInfo.date);
                                        mSpinnerFrom.add(rideShareInfo.from);
                                        mSpinnerTo.add(rideShareInfo.destination);
                                    }
                                } else {
                                    Toast.makeText(getContext(),"No data found.",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        }
        else {
            Toast.makeText(getContext(), "Please Login to continue", Toast.LENGTH_SHORT).show();
            Intent intent= new Intent(getContext(),FirebaseUI.class);
            startActivity(intent);
        }

    }

    private void populate(){
        RideShareAdapter mRideShareAdapter = new RideShareAdapter(mRideshareInfo,this);
        mRideShareAdapter.setHasStableIds(true);
        mRideShareAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mRideShareAdapter);

    }

    @Override
    public void onRideClick(int position) {
        RideShareInfo minfo = mRideshareInfo.get(position);
        if (user.getEmail().isEmpty()){
         Toast.makeText(getContext(),"You have to verify your email address to continue",Toast.LENGTH_LONG).show();

        }
        else {
            Intent intent = new Intent(getContext(), RideShareMoreInfo.class);
            intent.putExtra("RidesharerName", minfo.driverName);
            intent.putExtra("RidesharerDate", minfo.date);
            intent.putExtra("RidesharerFrom", minfo.from);
            intent.putExtra("RidesharerDestination", minfo.destination);
            intent.putExtra("RidesharerTime", minfo.time);
            intent.putExtra("RidesharerAmount", minfo.amount);
            intent.putExtra("RidesharerSharees", minfo.rideSharee);
            intent.putExtra("RidesharerModel", minfo.carModel);
            intent.putExtra("RidesharerPhone", minfo.driverPhone);
            intent.putExtra("RidesharerEmail", minfo.email);
            intent.putExtra("RidesharerRemainder", minfo.remainder);
            startActivity(intent);
        }
    }
}
