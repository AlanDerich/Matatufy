package com.derich.matatufy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.derich.matatufy.Fragments.RideShare;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddRideShare extends AppCompatActivity {
    private EditText et_name,et_model,et_phone,et_from,et_to,et_cash,et_rideSharee;
    private TextView et_date,et_time;
    private FirebaseUser mUser;
    private int selMin;
    private int selHour;
    private String name,date,from,destination,time,fare,ridesharees,model,phone,email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ride_share);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        et_name = findViewById(R.id.editText_add_name);
        et_rideSharee = findViewById(R.id.editText_rideSharees);
        et_model = findViewById(R.id.editText_add_model);
        et_phone = findViewById(R.id.editText_add_phone);
        et_date = findViewById(R.id.editText_add_date);
        et_from = findViewById(R.id.editText_add_from);
        et_to = findViewById(R.id.editText_add_to);
        et_time = findViewById(R.id.editText_add_time);
        et_cash = findViewById(R.id.editText_add_cash);
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
        if (date!=null){
            et_name.setText(name);
            et_rideSharee.setText(ridesharees);
            et_model.setText(model);
            et_phone.setText(phone);
            et_date.setText(date);
            et_date.setClickable(false);
            et_from.setText(from);
            et_to.setText(destination);
            et_time.setText(time);
            et_cash.setText(fare);
            et_cash.setClickable(false);
        }
        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(AddRideShare.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        et_time.setText(String.format("%02d",selectedHour) + " : " +String.format("%02d",selectedMinute));
                        selHour = selectedHour;
                        selMin = selectedMinute;
                    }
                },hour,minute,true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar mDate = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mDate.set(Calendar.YEAR,year);
                        mDate.set(Calendar.MONTH,month);
                        mDate.set(Calendar.DAY_OF_MONTH,day);
                        String format = "dd/MM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);et_date.setText(sdf.format(mDate.getTime()));
                    }
                };
                new DatePickerDialog(AddRideShare.this,date,mDate.get(Calendar.YEAR),mDate.get(Calendar.MONTH),mDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
                
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        MenuItem item = menu.findItem(R.id.login);
        item.setVisible(false);
        MenuItem item1 = menu.findItem(R.id.saveStage);
        item1.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.saveRideShare:
                saveRideShare();

                default:
                    return super.onOptionsItemSelected(item);
        }

    }
    public static String encode(String coOrdns){
        return coOrdns.replace("/",",");
    }
    private void saveRideShare(){
        String driverName = et_name.getText().toString().trim();
        String carModel = et_model.getText().toString().trim();
        String driverPhone= et_phone.getText().toString().trim();
        String date = et_date.getText().toString().trim();
        String from = et_from.getText().toString().trim();
        String destination= et_to.getText().toString().trim();
        String time = et_time.getText().toString();
        String amount= et_cash.getText().toString().trim();
        String rideSharees = et_rideSharee.getText().toString().trim();

        FirebaseFirestore mFirestone = FirebaseFirestore.getInstance();
            String email = mUser.getEmail();
            Boolean verified = mUser.isEmailVerified();
        if (email != null) {
            if (email.equals("")){
                Toast.makeText(this,"You don't have an email address. Please click on your profile and add an email to continue.",Toast.LENGTH_LONG).show();
            }

            else {
                if (driverName.isEmpty() || carModel.isEmpty() || driverPhone.isEmpty() || date.equals("Click to select Date") || from.isEmpty() || destination.isEmpty() || time.equals("Click to select time") || amount.isEmpty() || rideSharees.isEmpty()){
                    Toast.makeText(this,"All fields are required. Please fill to continue",Toast.LENGTH_SHORT).show();

                }
                else {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.US);
                        final String currentDateandTimeOfAdd = sdf.format(new Date());
                        if (sdf.parse(date + " "+selHour + ":" + selMin).before(sdf.parse(currentDateandTimeOfAdd))) {
                            Toast.makeText(AddRideShare.this,"Please select a valid date and time",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            RideShareInfo mRideShareInfo = new RideShareInfo(driverName, carModel, driverPhone, date, from, destination, time, amount,rideSharees,email,rideSharees);
                            mFirestone.collection("RideShares").document(email).collection("all rideshares")
                                    .document(encode(date)+" at " + time).set(mRideShareInfo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(new Intent(AddRideShare.this, MainActivity.class));
                                            Toast.makeText(AddRideShare.this,"RideShare started successfully",Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddRideShare.this,"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
        }
        }
    }
}
