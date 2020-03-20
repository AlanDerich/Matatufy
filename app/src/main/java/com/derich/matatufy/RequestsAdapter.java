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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static com.derich.matatufy.AddRideShare.encode;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {
    private List<ActiveRideshares> requestList;
    private Context mContext;
    private RequestsAdapter.OnRequestClickListener onRequestClickListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    public RequestsAdapter(List<ActiveRideshares> mActiveRideshares, RideShareMoreInfo rideShareMoreInfo) {
        this.requestList = mActiveRideshares;
        this.onRequestClickListener=rideShareMoreInfo;
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
            holder.approval.setText("Approved");
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
        else if (approval.equals("Declined")){
            holder.approval.setText("Declined");
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
        else {
            holder.approval.setText("Pending");
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
                AlertDialog.Builder builderSelect = new AlertDialog.Builder(mContext,R.style.AlertDialogStyle);
                builderSelect.setTitle("APPROVE");
                builderSelect.setMessage("Are you sure you want to approve the passenger to join your rideshare?");
                builderSelect.setCancelable(false);
                builderSelect.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ActiveRideshares activeRideshares = new ActiveRideshares(ridesharerEmail, email, date,time, phoneNum,"Approved");
                        db.collection("bookRideshares").document("available requests").collection(ridesharerEmail+encode(" "+date +" "+ time)).document(email)
                                .set(activeRideshares)

                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //mContext.startActivity(new Intent(mContext, MainActivity.class));
                                        Toast.makeText(mContext, "Request approved successfully.", Toast.LENGTH_LONG).show();
                                        holder.approval.setText("Approved");
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
                AlertDialog.Builder builderSelect = new AlertDialog.Builder(mContext,R.style.AlertDialogStyle);
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
                                        holder.approval.setText("Declined");
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
        public ViewHolder(View itemView, RequestsAdapter.OnRequestClickListener onRequestClickListener) {
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
