package com.derich.matatufy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RideShareAdapter extends RecyclerView.Adapter<RideShareAdapter.ViewHolder>  {
    private List<RideShareInfo> mShareInfoList;
    private Context mContext;
    private OnRideClickListener onRideClickListener;

    public RideShareAdapter(List<RideShareInfo> mShareInfoList, OnRideClickListener onRideClickListener){
        this.onRideClickListener = onRideClickListener;
        this.mShareInfoList = mShareInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_rideshare,parent,false);
        mContext = parent.getContext();
        return new ViewHolder(view,onRideClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RideShareAdapter.ViewHolder holder, int position) {
        RideShareInfo rI = mShareInfoList.get(position);
        holder.driverName.setText(rI.getDriverName());
        holder.from.setText(rI.getFrom());
        holder.to.setText(rI.getDestination());
        holder.date.setText(rI.getDate());
        holder.time.setText(rI.getTime());
        holder.rideSharee.setText(rI.getRideSharee());

    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mShareInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView driverName,from,to,date,time,rideSharee;
        OnRideClickListener onRideClickListener;
        public ViewHolder(View itemView,OnRideClickListener onRideClickListener) {
            super(itemView);
            this.onRideClickListener = onRideClickListener;
            driverName=itemView.findViewById(R.id.listDriverName);
            from= itemView.findViewById(R.id.listFrom);
            to= itemView.findViewById(R.id.listTo);
            date= itemView.findViewById(R.id.listDate);
            time= itemView.findViewById(R.id.listTime);
            rideSharee= itemView.findViewById(R.id.listStatus);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRideClickListener.onRideClick(getAdapterPosition());

        }
    }
    public interface OnRideClickListener{
        void onRideClick(int position);
    }
}


