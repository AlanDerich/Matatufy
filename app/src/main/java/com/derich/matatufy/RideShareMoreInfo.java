package com.derich.matatufy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RideShareMoreInfo extends AppCompatActivity {
TextView textViewName,textViewDate,textViewFrom,textViewDestination,textViewTime,textViewFare,textViewRideshare,textViewModel;
ImageButton btnCall,btnMessage;
    private String name,date,from,destination,time,fare,ridesharees,model;
    private String phone;
    private String email;

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

        textViewName.setText(name);
        textViewDate.setText(date);
        textViewFrom.setText(from);
        textViewDestination.setText(destination);
        textViewTime.setText(time);
        textViewFare.setText(fare);
        textViewRideshare.setText(ridesharees);
        textViewModel.setText(model);
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
                String extra = AddRideShare.encode(date)+" at " + time;
                intentMessage.putExtra("documentName",extra);
                intentMessage.putExtra("RidesharerEmail", email);
                startActivity(intentMessage);
            }
        });
    }
}
