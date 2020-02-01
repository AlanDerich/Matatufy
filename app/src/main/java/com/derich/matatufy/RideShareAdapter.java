package com.derich.matatufy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RideShareAdapter extends RecyclerView.Adapter<RideShareAdapter.ViewHolder> {
    private List<RideShareInfo> mShareInfoList;

    public RideShareAdapter(List<RideShareInfo> mShareInfoList){
        this.mShareInfoList = mShareInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_rideshare,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideShareAdapter.ViewHolder holder, int position) {
        RideShareInfo rI = mShareInfoList.get(position);
        holder.driverName.setText(rI.getDriverName());
        holder.from.setText(rI.getFrom());
        holder.to.setText(rI.getDestination());
        holder.date.setText(rI.getDate());
        holder.time.setText(rI.getTime());
    }

    @Override
    public int getItemCount() {
        return mShareInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView driverName,from,to,date,time;
        public ViewHolder(View itemView) {
            super(itemView);
            driverName=itemView.findViewById(R.id.listDriverName);
            from= itemView.findViewById(R.id.listFrom);
            to= itemView.findViewById(R.id.listTo);
            date= itemView.findViewById(R.id.listDate);
            time= itemView.findViewById(R.id.listTime);
        }
    }
}
