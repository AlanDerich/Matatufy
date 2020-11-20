package com.derich.matatufy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static com.derich.matatufy.AddRideShare.encode;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {
    private List<ActiveRideshares> requestList;
    private Context mContext;
    private String name;
    private String date;
    private String from;
    private String destination;
    private String time;
    private String fare;
    private String ridesharees;
    private String model;
    private String phone;
    private String email;
    private String remainder;
    private RequestsAdapter.OnRequestClickListener onRequestClickListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private RideShareInfo mRideShareInfo;

    public RequestsAdapter(List<ActiveRideshares> mActiveRideshares, RideShareMoreInfo rideShareMoreInfo,String name,String date,String from,String destination,String time,String fare,String ridesharees,String model,String phone,String email,String remainder) {
        this.requestList = mActiveRideshares;
        this.onRequestClickListener=rideShareMoreInfo;
        this.name=name;
        this.from=from;
        this.destination=destination;
        this.fare=fare;
        this.ridesharees=ridesharees;
        this.model=model;
        this.phone=phone;
        this.email=email;
        this.date=date;
        this.time=time;
        this.remainder=remainder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_requests,parent,false);
        mContext = parent.getContext();
        return new RequestsAdapter.ViewHolder(view,onRequestClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ActiveRideshares activeRideshares= requestList.get(position);
        final String phoneNum = activeRideshares.getPhone();
        final String email = activeRideshares.getRideShareeName();
        final String ridesharerEmail = activeRideshares.getRideSharerEmail();
        final String approval = activeRideshares.getStatus();
        final String date = activeRideshares.getRideShareDate();
        final String time = activeRideshares.getTime();
        holder.phone.setText(phoneNum);
        holder.email.setText(email);
        if (approval.equals("Approved")){
            holder.approval.setText(R.string.approved);
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
        else if (approval.equals("Declined")){
            holder.approval.setText(mContext.getString(R.string.declined));
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
        else {
            holder.approval.setText(R.string.pending);
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }
        holder.btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",activeRideshares.getPhone(),null));
                mContext.startActivity(intentCall);
            }
        });
        holder.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSelect = new AlertDialog.Builder(mContext);
                builderSelect.setTitle("APPROVE");
                builderSelect.setMessage("Are you sure you want to approve the passenger to join your rideshare?");
                builderSelect.setCancelable(false);
                builderSelect.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ActiveRideshares activeRideshares = new ActiveRideshares(ridesharerEmail, email, date,time, phoneNum,"Approved");
                        db.collection("bookRideshares").document(ridesharerEmail+encode(" "+date +" "+ time)).collection("available requests").document(email)
                                .set(activeRideshares)

                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //mContext.startActivity(new Intent(mContext, MainActivity.class));
                                        Toast.makeText(mContext, R.string.request_approved, Toast.LENGTH_LONG).show();
                                        holder.approval.setText(R.string.approved);
                                        holder.btnApprove.setVisibility(View.GONE);
                                        holder.btnDelete.setVisibility(View.GONE);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, mContext.getString(R.string.request_not_approved) + e, Toast.LENGTH_LONG).show();
                                    }
                                });
                        mRideShareInfo = new RideShareInfo(name, model, phone, date, from, destination, time, fare,ridesharees,ridesharerEmail,String.valueOf(Integer.valueOf(remainder)-1));
                        db.collection("RideShares").document(ridesharerEmail).collection("all rideshares")
                                .document(encode(date)+" at " + time).set(mRideShareInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(mContext,"RideShare updated successfully",Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext,"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                                    }
                                });

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
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSelect = new AlertDialog.Builder(mContext);
                builderSelect.setTitle("DECLINE");
                builderSelect.setMessage("Are you sure you want to decline the passenger to join your rideshare?");
                builderSelect.setCancelable(false);
                builderSelect.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ActiveRideshares activeRideshares = new ActiveRideshares(ridesharerEmail, email, date,time, phoneNum,approval);
                        db.collection("bookRideshares").document("available requests").collection(ridesharerEmail).document(email)
                                .set(activeRideshares)

                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //mContext.startActivity(new Intent(mContext, MainActivity.class));
                                        Toast.makeText(mContext, "Request declined successfully.", Toast.LENGTH_LONG).show();
                                        holder.approval.setText(R.string.declined);
                                        holder.btnApprove.setVisibility(View.GONE);
                                        holder.btnDelete.setVisibility(View.GONE);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, "Not approved. Try again later." + e, Toast.LENGTH_LONG).show();
                                    }
                                });
                        mRideShareInfo = new RideShareInfo(name, model, phone, date, from, destination, time, fare,ridesharees,ridesharerEmail,String.valueOf(Integer.valueOf(remainder)+1));
                        db.collection("RideShares").document(email).collection("all rideshares")
                                .document(encode(date)+" at " + time).set(mRideShareInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(mContext,"RideShare started successfully",Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext,"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                                    }
                                });

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

    @Override
    public int getItemCount() {
        return requestList.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView email,phone,approval;
        ImageButton btnDelete,btnApprove,btnPhone;
        RequestsAdapter.OnRequestClickListener onRequestClickListener;
        ViewHolder(View itemView, RequestsAdapter.OnRequestClickListener onRequestClickListener) {
            super(itemView);
            this.onRequestClickListener = onRequestClickListener;
            email= itemView.findViewById(R.id.lr_email);
            phone= itemView.findViewById(R.id.lr_phone);
            approval= itemView.findViewById(R.id.lr_approval);
            btnDelete= itemView.findViewById(R.id.imageButtonDecline);
            btnApprove = itemView.findViewById(R.id.imageButtonApprove);
            btnPhone= itemView.findViewById(R.id.imageButtonCall);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRequestClickListener.onRequestClick(getAdapterPosition());

        }
    }
    public interface OnRequestClickListener{
        void onRequestClick(int position);
    }
}
