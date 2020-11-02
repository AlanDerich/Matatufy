package com.derich.matatufy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.derich.matatufy.AddRideShare.encode;

public class RideShareMoreInfo extends AppCompatActivity implements RequestsAdapter.OnRequestClickListener{
private TextView textViewName,textViewDate,textViewFrom,textViewDestination,textViewTime,textViewFare,textViewRideshare,textViewModel;
private ImageButton btnCall,btnMessage,btnEdit,btnDelete;
private RecyclerView rvRequests;
private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
private Button bookRide;
    private String name,date,from,destination,time,fare,ridesharees,model,remainder;
    private String phone;
    private String email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<ActiveRideshares> mActiveRideshares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_share_more_info);
        Intent share =getIntent();
        name = share.getStringExtra("RidesharerName");
        date = share.getStringExtra("RidesharerDate");
        from = share.getStringExtra("RidesharerFrom");
        destination = share.getStringExtra("RidesharerDestination");
        time = share.getStringExtra("RidesharerTime");
        fare = share.getStringExtra("RidesharerAmount");
        ridesharees = share.getStringExtra("RidesharerSharees");
        model = share.getStringExtra("RidesharerModel");
        phone = share.getStringExtra("RidesharerPhone");
        email = share.getStringExtra("RidesharerEmail");
        remainder = share.getStringExtra("RidesharerRemainder");

        btnEdit=findViewById(R.id.imageButton_edit_details);
        btnDelete=findViewById(R.id.imageButtonDeleteRideshare);
        rvRequests=findViewById(R.id.rvRequests);
        textViewName = findViewById(R.id.textViewInfoName);
        textViewDate = findViewById(R.id.textViewInfoDate);
        textViewFrom = findViewById(R.id.textViewInfoFrom);
        textViewDestination = findViewById(R.id.textViewInfoDestination);
        textViewTime = findViewById(R.id.textViewInfoTime);
        textViewFare = findViewById(R.id.textViewInfoFare);
        textViewRideshare= findViewById(R.id.textViewInfoPassengers);
        textViewModel = findViewById(R.id.textViewInfoModel);
        btnCall= findViewById(R.id.imageButtonCallRidesharer);
        btnMessage = findViewById(R.id.imageButtonMessageRidesharer);
        bookRide = findViewById(R.id.button_bookride);

        textViewName.setText(name);
        textViewDate.setText(date);
        textViewFrom.setText(from);
        textViewDestination.setText(destination);
        textViewTime.setText(time);
        textViewFare.setText(fare);
        textViewRideshare.setText(ridesharees);
        textViewModel.setText(model);
//        checkBooked();
        if (mUser.getEmail().equals(email)){
            btnCall.setVisibility(View.GONE);
            bookRide.setVisibility(View.GONE);
            populateRecyclerview();
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String collectionId="all rideshares";
                db.collection("RideShares").document(email).collection("all rideshares")
                        .document(encode(date)+" at " + time).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RideShareMoreInfo.this,"Deleted successfully",Toast.LENGTH_SHORT).show();
                                db.collection("bookRideshares").document("available requests").collection(email+encode(" "+date +" "+ time)).document().delete()

                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(RideShareMoreInfo.this,"Data deleted successfully.",Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RideShareMoreInfo.this,"Not all data deleted." + e,Toast.LENGTH_LONG).show();
                                            }
                                        });
                                Intent intent=new Intent(RideShareMoreInfo.this,MainActivity.class);
                                startActivity(intent);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RideShareMoreInfo.this,"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RideShareMoreInfo.this,AddRideShare.class);
                intent.putExtra("RidesharerName",name);
                intent.putExtra("RidesharerDate",date);
                intent.putExtra("RidesharerFrom",from);
                intent.putExtra("RidesharerDestination",destination);
                intent.putExtra("RidesharerTime",time);
                intent.putExtra("RidesharerAmount",fare);
                intent.putExtra("RidesharerSharees",ridesharees);
                intent.putExtra("RidesharerModel",model);
                intent.putExtra("RidesharerPhone",phone);
                intent.putExtra("RidesharerEmail",email);
                startActivity(intent);
            }
        });}
        else {
            rvRequests.setVisibility(View.GONE);
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            checkBooked();
        }
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",phone,null));
                startActivity(intentCall);
            }
        });
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessage = new Intent(RideShareMoreInfo.this,ChattingActivity.class);
                String extra = encode(date)+" at " + time;
                intentMessage.putExtra("documentName",extra);
                intentMessage.putExtra("RidesharerEmail", email);
                startActivity(intentMessage);
            }
        });
        bookRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                final String userEmail = mUser.getEmail();
                AlertDialog.Builder builderSelect = new AlertDialog.Builder(RideShareMoreInfo.this,R.style.AlertDialogStyle);
                builderSelect.setTitle("Book Ride");
                builderSelect.setMessage("Do you want to book a seat on the rideshare ?");
                builderSelect.setCancelable(false);
                builderSelect.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(RideShareMoreInfo.this,R.style.AlertDialogStyle);
                        builder.setTitle("Phone Number");

// Set up the input
                        final EditText input = new EditText(RideShareMoreInfo.this);
                        input.setTextColor(Color.parseColor("#0BF5AB"));
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        builder.setView(input);

// Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!(input.getText().toString().isEmpty())) {
                                    String phoneNum = input.getText().toString();
                                    final ActiveRideshares activeRideshares = new ActiveRideshares(email, userEmail, date,time, phoneNum,"Pending");
                                    if (userEmail != null) {
                                        db.collection("bookRideshares").document(email+encode(" "+date +" "+ time)).collection("available requests").document(userEmail)
                                                .set(activeRideshares)

                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        startActivity(new Intent(RideShareMoreInfo.this, MainActivity.class));
                                                        bookRide.setEnabled(false);
                                                        bookRide.setText(R.string.booked);
                                                        Toast.makeText(RideShareMoreInfo.this, "Request sent successfully.", Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(RideShareMoreInfo.this, "Not saved. Try again later." + e, Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.makeText(RideShareMoreInfo.this, "Please enter a valid phone number.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();



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

    private void checkBooked() {
        db.collectionGroup("available requests").whereEqualTo("rideShareeName",mUser.getEmail()).whereEqualTo("rideShareDate",date).whereEqualTo("rideSharerEmail",email).whereEqualTo("status","Approved").whereEqualTo("time",time).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            bookRide.setText(R.string.approved);
                            bookRide.setEnabled(false);

                        } else {
                            db.collectionGroup("available requests").whereEqualTo("rideShareeName",mUser.getEmail()).whereEqualTo("rideShareDate",date).whereEqualTo("rideSharerEmail",email).whereEqualTo("time",time).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                bookRide.setText(R.string.not_yet_approved);
                                                bookRide.setEnabled(false);

                                            } else {
                                                bookRide.setEnabled(true);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RideShareMoreInfo.this,"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                                            Log.d("RideshreMoreInfo","Error: "+e);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RideShareMoreInfo.this,"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                        Log.d("RideshreMoreInfo","Error: "+e);
                    }
                });
    }

    private void populateRecyclerview() {
        mActiveRideshares=new ArrayList<>();
        db.collectionGroup("available requests").whereEqualTo("rideSharerEmail",mUser.getEmail()).whereEqualTo("time",time).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mActiveRideshares.add(snapshot.toObject(ActiveRideshares.class));
                            populate();
                        } else {
                            Toast.makeText(RideShareMoreInfo.this,"No data found.",Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RideShareMoreInfo.this,"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                        Log.d("MainActivity","error "+ e);
                    }
                });
    }

    private void populate(){
        rvRequests.setHasFixedSize(true);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        RequestsAdapter mRequestsAdapter = new RequestsAdapter(mActiveRideshares,this,name,date,from,destination,time,fare,ridesharees,model,phone,email,remainder);
        mRequestsAdapter.setHasStableIds(true);
        mRequestsAdapter.notifyDataSetChanged();
        rvRequests.setAdapter(mRequestsAdapter);

    }

    @Override
    public void onRequestClick(int position) {

    }
}
